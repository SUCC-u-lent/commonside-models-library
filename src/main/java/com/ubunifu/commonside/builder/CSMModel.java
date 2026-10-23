package com.ubunifu.commonside.builder;

import com.google.common.util.concurrent.AtomicDouble;
import com.ubunifu.arctic_library.commonside.*;
import com.ubunifu.arctic_library.commonside.mixin.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.*;
import net.minecraft.util.math.*;
import org.joml.*;

import java.lang.Math;
import java.util.*;
import java.util.function.Consumer;

public class CSMModel implements AutoCloseable {
	ModelData modelData;
	boolean applyGlobal;
	Vec3d scale = new Vec3d(1,1,1);
	Vec3d offset = new Vec3d(0,0,0);

	/** The identity map, aka the unchanged source. */
	private final Map<String, CommonPart> BASE_PART_MAP = new HashMap<>();
	/** The animated map, aka the changed source. */
	private final Map<String, CommonPart> PART_MAP = new HashMap<>();

	CSMModel(ModelData modelData, boolean applyGlobal) {
		this.modelData = modelData;
		this.applyGlobal = applyGlobal;
		ModelPartData root = modelData.getRoot();
		process(root, applyGlobal);
	}

	public void setScale(float x, float y, float z) {
		this.scale = new Vec3d(x,y,z);
	}

	public void setOffset(float x, float y, float z) {
		this.offset = new Vec3d(x,y,z);
	}

	//region Animation
	private float getTickDelta()
	{
		MinecraftClient client = MinecraftClient.getInstance();
		if (client != null)
			return client.getTickDelta();
		else return 0;
	}
	private void resetModel()
	{
		PART_MAP.clear();
		PART_MAP.putAll(BASE_PART_MAP);
	}
	/**
	 * Apply the animations performed in {@link EntityModel#setAngles(Entity, float, float, float, float, float)} to the current model
	 * @param reset When True the model will be reset to its identity before each animate call.
	 */
	public <T extends LivingEntity & CSMEntity, M extends EntityModel<T> & CSModel> void animateModel(T entity, M model, boolean reset) {
		if (reset) resetModel();
		__animateModel(entity,model);
		syncTransforms(model);
	}

	private <T extends LivingEntity & CSMEntity, M extends EntityModel<T> & CSModel>
	void syncTransforms(M model) {
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.translate(-0.04f,1.501f,0);
		matrix4f.translate((float) this.offset.x, (float) this.offset.y, (float) this.offset.z);
		matrix4f.scale(
			(float)scale.x,
			(float)scale.y,
			(float)scale.z
		);
		syncTransforms(model, "root", model.getRoot(), matrix4f);
	}

	private <T extends LivingEntity & CSMEntity, M extends EntityModel<T> & CSModel>
	void syncTransforms(
		M model,
		String name,
		ModelPart part,
		Matrix4f parentDelta
	) {
		CommonPart base = BASE_PART_MAP.get(name);
		if (base == null) return;

		Matrix4f defaultLocal = new Matrix4f()
			.translate(
				base.pivot().x / 16.0F,
				base.pivot().y / 16.0F,
				base.pivot().z / 16.0F
			)
			.rotateZYX(
				base.rotation().z,
				base.rotation().y,
				base.rotation().x
			);

		Matrix4f animatedLocal = new Matrix4f()
			.translate(
				part.pivotX / 16.0F,
				part.pivotY / 16.0F,
				part.pivotZ / 16.0F
			)
			.rotateZYX(part.roll, part.yaw, part.pitch);

		Matrix4f localDelta = new Matrix4f(defaultLocal)
			.invert()
			.mul(animatedLocal);

		Matrix4f global = new Matrix4f(parentDelta)
			.mul(base.global())
			.mul(localDelta);

		Box localBounds = calculateLocalBounds(base.part());

		PART_MAP.put(name, new CommonPart(
			base.partCode(),
			base.part(),
			new Vector3f(part.pivotX, part.pivotY, part.pivotZ),
			new Vector3f(part.pitch, part.yaw, part.roll),
			global,
			transformBox(localBounds, global),
			base.childrenNames(),
			createOrientedBox(localBounds, global),
			base.isBone()
		));

		// Express this part's local change in the cached model-space frame.
		Matrix4f childDelta = new Matrix4f(parentDelta)
			.mul(base.global())
			.mul(localDelta)
			.mul(new Matrix4f(base.global()).invert());

		for (String childName : base.childrenNames()) {
			ModelPart child;

			try {
				child = part.getChild(childName);
			} catch (NoSuchElementException ignored) {
				child = model.getPartChild(part, childName);
			}

			syncTransforms(model, childName, child, childDelta);
		}
	}

	private <T extends LivingEntity & CSMEntity, M extends EntityModel<T>> void __animateModel(T entity, M model)
	{
		CSMModelAnimator.animate(this,entity,model);
	}
	private <T extends LivingEntity & CSMEntity> float animateGetAnimationProgress(T entity, float tickDelta) {
		return (float)entity.age + tickDelta;
	}

	private static float animateGetYaw(Direction direction) {
		switch (direction) {
			case SOUTH -> {
				return 90.0F;
			}
			case NORTH -> {
				return 270.0F;
			}
			case EAST -> {
				return 180.0F;
			}
			default -> {
				return 0.0F;
			}
		}
	}
	private <T extends LivingEntity & CSMEntity> float animateGetLyingAngle(T entity) {
		return 90.0F;
	}
	private <T extends LivingEntity & CSMEntity> boolean animateIsShaking(T entity) {
		return entity.isFrozen();
	}
	private <T extends LivingEntity & CSMEntity> void animateSetupTransforms(T entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta) {
		if (this.animateIsShaking(entity)) {
			bodyYaw += (float)(Math.cos((double)entity.age * (double)3.25F) * Math.PI * (double)0.4F);
		}

		if (!entity.hasPose(EntityPose.SLEEPING)) {
			matrices.rotate(Axis.Y_POSITIVE.rotationDegrees(180.0F - bodyYaw));
		}

		if (entity.deathTime > 0) {
			float f = ((float)entity.deathTime + tickDelta - 1.0F) / 20.0F * 1.6F;
			f = MathHelper.sqrt(f);
			if (f > 1.0F) {
				f = 1.0F;
			}

			matrices.rotate(Axis.Z_POSITIVE.rotationDegrees(f * this.animateGetLyingAngle(entity)));
		} else if (entity.isUsingRiptide()) {
			matrices.rotate(Axis.X_POSITIVE.rotationDegrees(-90.0F - entity.getPitch()));
			matrices.rotate(Axis.Y_POSITIVE.rotationDegrees(((float)entity.age + tickDelta) * -75.0F));
		} else if (entity.hasPose(EntityPose.SLEEPING)) {
			Direction direction = entity.getSleepingDirection();
			float g = direction != null ? animateGetYaw(direction) : bodyYaw;
			matrices.rotate(Axis.Y_POSITIVE.rotationDegrees(g));
			matrices.rotate(Axis.Z_POSITIVE.rotationDegrees(this.animateGetLyingAngle(entity)));
			matrices.rotate(Axis.Y_POSITIVE.rotationDegrees(270.0F));
		} else if (LivingEntityRenderer.renderFlipped(entity)) {
			matrices.translate(0.0F, entity.getHeight() + 0.1F, 0.0F);
			matrices.rotate(Axis.Z_POSITIVE.rotationDegrees(180.0F));
		}

	}

	//endregion
	//region Heart
	public Map<String, CommonPart> getParts() {
		return Collections.unmodifiableMap(this.PART_MAP);
	}

	private void process(ModelPartData root, boolean applyGlobal) {

		MatrixStack matrixStack = new MatrixStack();

		if (applyGlobal)
		{
			matrixStack.scale(
				-1.0F,
				-1.0F,
				1.0F
			);

			matrixStack.translate(
				0.0F,
				-1.501F,
				0.0F
			);
		}

		matrixStack.scale(
			(float) this.scale.x,
			(float) this.scale.y,
			(float) this.scale.z
		);

		processChild(
			"root",
			root,
			matrixStack
		);

		// Now finally we need to reiterate the children locating all without cuboids and setting those to have the bounds of all their children.
		traverse(root)
			.stream()
			.filter(p->((ModelPartDataAccessor)(Object)p).CSM$getCuboidData().isEmpty())
			.forEach(child->{
				Box finalBounds = calculateLocalBounds( child );
				Map.Entry<String, CommonPart> transform = this.getParts().entrySet().stream().filter(v->v.getValue().partCode() == child.hashCode()).findFirst().orElse(null);
				if (transform != null)
				{
					CommonPart newTransform = new CommonPart(
						transform.getValue().partCode(),
						transform.getValue().part(),
						transform.getValue().pivot(),
						transform.getValue().rotation(),
						transform.getValue().global(),
						finalBounds,
						transform.getValue().childrenNames(),
						new OmniBox(finalBounds.minX,finalBounds.minY,finalBounds.minZ,finalBounds.maxX,finalBounds.maxY,finalBounds.maxZ),
						true
					);
					BASE_PART_MAP.put( transform.getKey(), newTransform );
					PART_MAP.put( transform.getKey(), newTransform );
				}
			});
	}

	private List<ModelPartData> traverse(ModelPartData root) {
		List<ModelPartData> mpd = new ArrayList<>();
		List<ModelPartData> r = new ArrayList<>(((ModelPartDataAccessor) (Object) root).CSM$getChildren().values());
		for (ModelPartData data : r) {
			mpd.add( data );
			mpd.addAll( traverse( data ) );
		}
		return mpd;
	}

	private void postProcessChild(
		String name,
		ModelPartData child,
		AtomicDouble minX,
		AtomicDouble minY,
		AtomicDouble minZ,
		AtomicDouble maxX,
		AtomicDouble maxY,
		AtomicDouble maxZ
	){
		Box box = this.getPartBounds(name);
		minX.set(Math.min(
			minX.get(),
			box.minX
		));

		minY.set(Math.min(
			minY.get(),
			box.minY
		));

		minZ.set(Math.min(
			minZ.get(),
			box.minY
		));

		maxX.set(Math.max(
			maxX.get(),
			box.maxX
		));

		maxY.set(Math.max(
			maxY.get(),
			box.maxY
		));

		maxZ.set(Math.max(
			maxZ.get(),
			box.maxZ
		));
		Map<String, ModelPartData> children = ((ModelPartDataAccessor)(Object)child).CSM$getChildren();
		for (Map.Entry<String, ModelPartData> entry : children.entrySet()) postProcessChild( entry.getKey(),entry.getValue(),minX, minY, minZ, maxX, maxY, maxZ );
	}

	private Vector3f getScale(ModelPartData partData)
	{
		Box box = calculateLocalBounds( partData );
		return new Vector3f((float) box.getXLength(), (float) box.getYLength(), (float) box.getZLength());
	}

	private void processChild(
		String childName,
		ModelPartData child,
		MatrixStack matrixStack
	) {
		matrixStack.push();

		Consumer<MatrixStack> rotateMatrixStack = (MatrixStack matrix)->{
			ModelTransform transform = ((ModelPartDataAccessor)(Object)child).CSM$getRotationData();

			matrix.translate(transform.pivotX / 16.0F, transform.pivotY / 16.0F, transform.pivotZ / 16.0F);
			if (transform.pitch != 0.0F || transform.yaw != 0.0F || transform.roll != 0.0F) {
				matrix.rotate((new Quaternionf()).rotationZYX(transform.roll, transform.yaw, transform.pitch));
			}
		};
		rotateMatrixStack.accept( matrixStack );
		Box bounds;
		OmniBox orientatedBox;

		Matrix4f globalMatrix =
			new Matrix4f(matrixStack.peek().getModel());

		Box localBounds = calculateLocalBounds(child);

		if (localBounds == null) bounds = new Box(0,0,0,0,0,0);
		else bounds =
			transformBox(
				localBounds,
				globalMatrix
			);
		if (localBounds == null) orientatedBox = new OmniBox(0,0,0,0,0,0, Vec3d.ZERO);
		else orientatedBox = createOrientedBox(
			localBounds,
			globalMatrix
		);

		ModelTransform transform = ((ModelPartDataAccessor)(Object)child).CSM$getRotationData();
		Vector3f pivot = new Vector3f( transform.pivotX, transform.pivotY, transform.pivotZ );
		Vector3f rotation = new Vector3f( transform.pitch, transform.yaw, transform.roll );
		Map<String, ModelPartData> children =
			((ModelPartDataAccessor) (Object) child).CSM$getChildren();

		CommonPart partTransform = new CommonPart(
			child.hashCode(),
			child,
			pivot,
			rotation,
			new Matrix4f(matrixStack.peek().getModel()),
			bounds,
			children.keySet().toArray(String[]::new),
			orientatedBox,
			false
		);

		BASE_PART_MAP.put(childName, partTransform);
		PART_MAP.put(childName, partTransform);


		children.forEach(
			(name, childPart) ->
				processChild(
					name,
					childPart,
					matrixStack
				)
		);

		matrixStack.pop();
	}

	/**
	 * Calculates the bounds of this ModelPart's own cuboids.
	 * Descendant parts are intentionally NOT included.
	 */
	/**
	 * Calculates this part's own cuboid bounds in model units (blocks).
	 * Cuboid data and dilation are stored in pixels, so convert to blocks.
	 */
	private Box calculateLocalBounds(ModelPartData part) {
		List<ModelCuboidData> cuboids =
			((ModelPartDataAccessor) (Object) part).CSM$getCuboidData();

		if (cuboids.isEmpty()) {
			return new Box(0, 0, 0, 0, 0, 0);
		}

		double minX = Double.POSITIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double minZ = Double.POSITIVE_INFINITY;

		double maxX = Double.NEGATIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		double maxZ = Double.NEGATIVE_INFINITY;

		for (ModelCuboidData cuboid : cuboids) {
			Vector3f offset =
				((ModelCuboidDataAccessor) (Object) cuboid).CSM$getOffset();

			Vector3f dimensions =
				((ModelCuboidDataAccessor) (Object) cuboid).CSM$getDimensions();

			Dilation dilation =
				((ModelCuboidDataAccessor) (Object) cuboid).CSM$getExtraSize();

			DilationAccessor extra = (DilationAccessor) (Object) dilation;

			double x1 = (offset.x - extra.CSM$getRadiusX()) / 16.0;
			double y1 = (offset.y - extra.CSM$getRadiusY()) / 16.0;
			double z1 = (offset.z - extra.CSM$getRadiusZ()) / 16.0;

			double x2 = (offset.x + dimensions.x + extra.CSM$getRadiusX()) / 16.0;
			double y2 = (offset.y + dimensions.y + extra.CSM$getRadiusY()) / 16.0;
			double z2 = (offset.z + dimensions.z + extra.CSM$getRadiusZ()) / 16.0;

			minX = Math.min(minX, x1);
			minY = Math.min(minY, y1);
			minZ = Math.min(minZ, z1);

			// Important: these must be Math.max, not Math.min.
			maxX = Math.max(maxX, x2);
			maxY = Math.max(maxY, y2);
			maxZ = Math.max(maxZ, z2);
		}

		return new Box(minX, minY, minZ, maxX, maxY, maxZ);
	}

	/**
	 * Transforms all eight corners of a local-space box and creates
	 * an axis-aligned box containing the transformed result.
	 *
	 * This is the existing bounds calculation.
	 */
	private Box transformBox(
		Box box,
		Matrix4f matrix
	) {
		double minX = Double.POSITIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double minZ = Double.POSITIVE_INFINITY;

		double maxX = Double.NEGATIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		double maxZ = Double.NEGATIVE_INFINITY;

		double[] xs = {
			box.minX,
			box.maxX
		};

		double[] ys = {
			box.minY,
			box.maxY
		};

		double[] zs = {
			box.minZ,
			box.maxZ
		};

		for (double x : xs) {
			for (double y : ys) {
				for (double z : zs) {

					Vector3f point =
						new Vector3f(
							(float) x,
							(float) y,
							(float) z
						);

					point.mulPosition(matrix);

					minX = Math.min(
						minX,
						point.x
					);

					minY = Math.min(
						minY,
						point.y
					);

					minZ = Math.min(
						minZ,
						point.z
					);

					maxX = Math.max(
						maxX,
						point.x
					);

					maxY = Math.max(
						maxY,
						point.y
					);

					maxZ = Math.max(
						maxZ,
						point.z
					);
				}
			}
		}

		return new Box(
			minX,
			minY,
			minZ,
			maxX,
			maxY,
			maxZ
		);
	}

	/**
	 * Creates an oriented box from the local bounds and the model
	 * transformation matrix.
	 *
	 * The returned box is still in entity/model space. Entity world
	 * position and entity yaw are applied when the box is used.
	 */
	private OmniBox createOrientedBox(
		Box localBounds,
		Matrix4f globalMatrix
	) {
		Vector3f localCenter =
			new Vector3f(
				(float) ((localBounds.minX + localBounds.maxX) * 0.5D),
				(float) ((localBounds.minY + localBounds.maxY) * 0.5D),
				(float) ((localBounds.minZ + localBounds.maxZ) * 0.5D)
			);

		Vector3f transformedCenter =
			new Vector3f(localCenter);

		transformedCenter.mulPosition(globalMatrix);

		Vector3f matrixScale =
			new Vector3f();

		globalMatrix.getScale(matrixScale);

		double sizeX =
			(localBounds.maxX - localBounds.minX)
				* Math.abs(matrixScale.x);

		double sizeY =
			(localBounds.maxY - localBounds.minY)
				* Math.abs(matrixScale.y);

		double sizeZ =
			(localBounds.maxZ - localBounds.minZ)
				* Math.abs(matrixScale.z);

		Quaternionf modelRotation =
			new Quaternionf();

		globalMatrix.getUnnormalizedRotation(
			modelRotation
		);

		Vector3f euler =
			new Vector3f();

		modelRotation.getEulerAnglesXYZ(
			euler
		);

		Vec3d rotation =
			new Vec3d(
				Math.toDegrees(euler.x),
				Math.toDegrees(euler.y),
				Math.toDegrees(euler.z)
			);

		OmniBox result =
			OmniBox.of(
				new Vec3d(
					transformedCenter.x,
					transformedCenter.y,
					transformedCenter.z
				),
				sizeX,
				sizeY,
				sizeZ
			);

		return result.setRotation(rotation);
	}

	public String getNameFromPart(ModelPartData part)
	{
		for (Map.Entry<String, CommonPart> stringPartTransformEntry : PART_MAP.entrySet())
			if (stringPartTransformEntry.getValue().partCode() == part.hashCode()) return stringPartTransformEntry.getKey();
		return null;
	}

	public Matrix4f getMatrixForPart(ModelPartData part) {
		CommonPart partTransform = PART_MAP.getOrDefault(getNameFromPart(part),null);
		if (partTransform == null) throw new IllegalArgumentException("Part is not registered in this model");
		Matrix4f matrix = partTransform.global();

		return new Matrix4f(matrix);
	}

	public ModelPartData getPart(String name) {
		if (PART_MAP.containsKey(name)) return PART_MAP.get(name).part();
		throw new IllegalArgumentException("Part is not registered in this model");
	}
	public Box getPartBounds(ModelPartData part) {
		CommonPart partTransform = PART_MAP.getOrDefault(getNameFromPart(part),null);
		if (partTransform == null) throw new IllegalArgumentException("Part is not registered in this model");
		Box bounds = partTransform.bounds();

		Set<String> partNames = PART_MAP.keySet();

		if (bounds == null) {
			throw new IllegalArgumentException(
				"Part does not have registered bounds, check its name. Valid names are: ["+String.join(", ",partNames)+"]"
			);
		}

		return bounds;
	}

	public boolean isPartRegistered(ModelPartData part)
	{
		CommonPart partTransform = PART_MAP.getOrDefault(getNameFromPart(part),null);
		return partTransform != null;
	}

	public boolean isPartRegistered(String name) {
		return this.PART_MAP.containsKey(name);
	}

	public  Box getPartBounds(String name) {
		ModelPartData part = getPart(name);

		if (part == null) {
			throw new IllegalArgumentException(
				"Unknown model part: " + name
			);
		}

		return getPartBounds(part);
	}

	private OmniBox getOrientedPartBounds(ModelPartData part) {
		CommonPart partTransform = PART_MAP.getOrDefault(getNameFromPart(part),null);
		if (partTransform == null) throw new IllegalArgumentException("Part is not registered in this model");
		OmniBox bounds = partTransform.orientatedBox();

		if (bounds == null) {
			throw new IllegalArgumentException(
				"Part does not have registered oriented bounds"
			);
		}

		return new OmniBox(
			bounds.minX(),
			bounds.minY(),
			bounds.minZ(),
			bounds.maxX(),
			bounds.maxY(),
			bounds.maxZ(),
			bounds.getRotation()
		);
	}

	private OmniBox getOrientedPartBounds(String name) {
		ModelPartData part =
			getPart(name);

		if (part == null) {
			throw new IllegalArgumentException(
				"Unknown model part: " + name
			);
		}

		return getOrientedPartBounds(part);
	}
	//endregion

	@Override
	public void close() throws Exception {
		this.PART_MAP.clear();
		this.BASE_PART_MAP.clear();
		this.modelData = null;
	}
}

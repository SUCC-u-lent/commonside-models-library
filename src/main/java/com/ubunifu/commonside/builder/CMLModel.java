package com.ubunifu.commonside.builder;

import com.google.common.util.concurrent.AtomicDouble;
import com.ubunifu.commonside.mixin.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.*;
import org.joml.*;

import java.lang.Math;
import java.util.*;
import java.util.function.*;

public class CMLModel<T extends LivingEntity,M extends EntityModel<T>> {

	M entityModel;
	ModelData modelData;
	boolean applyGlobal;

	private final Map<String, CommonPart> PART_MAP = new HashMap<>();

	CMLModel(T entity, M entityModel, ModelData modelData, boolean applyGlobal) {
		this.modelData = modelData;
		this.applyGlobal = applyGlobal;
		this.entityModel = entityModel;
		ModelPartData root = modelData.getRoot();
		this.animateModel(entity);
		process(root, applyGlobal);
	}
	//region Animation
	private float getTickDelta()
	{
		MinecraftClient client = MinecraftClient.getInstance();
		if (client != null)
			return client.getTickDelta();
		else return 0;
	}
	private void animateModel(T entity)
	{
		float g = this.getTickDelta();
		float h = MathHelper.lerpAngleDegrees(g, entity.prevBodyYaw, entity.bodyYaw);
		float j = MathHelper.lerpAngleDegrees(g, entity.prevHeadYaw, entity.headYaw);
		float k = j - h;
		float n = 0.0F;
		float o = 0.0F;
		if (!entity.hasVehicle() && entity.isAlive()) {
			n = entity.limbData.getLimbDistance(g);
			o = entity.limbData.getLimbAngle(g);
			if (entity.isBaby()) {
				o *= 3.0F;
			}

			if (n > 1.0F) {
				n = 1.0F;
			}
		}
		float m = MathHelper.lerp(g, entity.prevPitch, entity.getPitch());
		if (LivingEntityRenderer.renderFlipped(entity)) {
			m *= -1.0F;
			k *= -1.0F;
		}
		MatrixStack matrixStack = new MatrixStack(); // Probably not how its implemented but eh, we'll find out.
		// g = tickDelta
		float l = this.animateGetAnimationProgress(entity, g);
		this.animateSetupTransforms(entity, matrixStack, l, h, g);
		entityModel.animateModel(entity, o, n, g);
		entityModel.setAngles(entity, o, n, l, k, m);
	}
	private float animateGetAnimationProgress(T entity, float tickDelta) {
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
	private float animateGetLyingAngle(T entity) {
		return 90.0F;
	}
	private boolean animateIsShaking(T entity) {
		return entity.isFrozen();
	}
	private void animateSetupTransforms(T entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta) {
		if (this.animateIsShaking(entity)) {
			bodyYaw += (float)(Math.cos((double)entity.age * (double)3.25F) * Math.PI * (double)0.4F);
		}

		if (!entity.hasPose(EntityPose.SLEEPING)) {
			matrices.multiply(Axis.Y_POSITIVE.rotationDegrees(180.0F - bodyYaw));
		}

		if (entity.deathTime > 0) {
			float f = ((float)entity.deathTime + tickDelta - 1.0F) / 20.0F * 1.6F;
			f = MathHelper.sqrt(f);
			if (f > 1.0F) {
				f = 1.0F;
			}

			matrices.multiply(Axis.Z_POSITIVE.rotationDegrees(f * this.animateGetLyingAngle(entity)));
		} else if (entity.isUsingRiptide()) {
			matrices.multiply(Axis.X_POSITIVE.rotationDegrees(-90.0F - entity.getPitch()));
			matrices.multiply(Axis.Y_POSITIVE.rotationDegrees(((float)entity.age + tickDelta) * -75.0F));
		} else if (entity.hasPose(EntityPose.SLEEPING)) {
			Direction direction = entity.getSleepingDirection();
			float g = direction != null ? animateGetYaw(direction) : bodyYaw;
			matrices.multiply(Axis.Y_POSITIVE.rotationDegrees(g));
			matrices.multiply(Axis.Z_POSITIVE.rotationDegrees(this.animateGetLyingAngle(entity)));
			matrices.multiply(Axis.Y_POSITIVE.rotationDegrees(270.0F));
		} else if (LivingEntityRenderer.renderFlipped(entity)) {
			matrices.translate(0.0F, entity.getHeight() + 0.1F, 0.0F);
			matrices.multiply(Axis.Z_POSITIVE.rotationDegrees(180.0F));
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

		processChild(
			"root",
			root,
			matrixStack
		);

		// Now finally we need to reitterate the children locating all without cuboids and setting those to have the bounds of all their children.
		traverse(root)
			.stream()
			.filter(p->((ModelPartDataAccessor)(Object)p).CML$getCuboidData().isEmpty())
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
						new OmniBox(finalBounds.minX,finalBounds.minY,finalBounds.minZ,finalBounds.maxX,finalBounds.maxY,finalBounds.maxZ),
						true
					);
					PART_MAP.put( transform.getKey(), newTransform );
				}
			});
	}

	private List<ModelPartData> traverse(ModelPartData root) {
		List<ModelPartData> mpd = new ArrayList<>();
		List<ModelPartData> r = new ArrayList<>(((ModelPartDataAccessor) (Object) root).CML$getChildren().values());
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
		Map<String, ModelPartData> children = ((ModelPartDataAccessor)(Object)child).CML$getChildren();
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
			ModelTransform transform = ((ModelPartDataAccessor)(Object)child).CML$getRotationData();

			matrix.translate(transform.pivotX / 16.0F, transform.pivotY / 16.0F, transform.pivotZ / 16.0F);
			if (transform.pitch != 0.0F || transform.yaw != 0.0F || transform.roll != 0.0F) {
				matrix.multiply((new Quaternionf()).rotationZYX(transform.roll, transform.yaw, transform.pitch));
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

		ModelTransform transform = ((ModelPartDataAccessor)(Object)child).CML$getRotationData();
		Vector3f pivot = new Vector3f( transform.pivotX, transform.pivotY, transform.pivotZ );
		Vector3f rotation = new Vector3f( transform.pitch, transform.yaw, transform.roll );

		CommonPart partTransform = new CommonPart(
			child.hashCode(),
			child,
			pivot,
			rotation,
			new Matrix4f(matrixStack.peek().getModel()),
			bounds,
			orientatedBox,
			false
		);

		PART_MAP.put(childName, partTransform);

		Map<String, ModelPartData> children =
			((ModelPartDataAccessor) (Object) child).CML$getChildren();

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
			((ModelPartDataAccessor) (Object) part).CML$getCuboidData();

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
				((ModelCuboidDataAccessor) (Object) cuboid).CML$getOffset();

			Vector3f dimensions =
				((ModelCuboidDataAccessor) (Object) cuboid).CML$getDimensions();

			Dilation dilation =
				((ModelCuboidDataAccessor) (Object) cuboid).CML$getExtraSize();

			DilationAccessor extra = (DilationAccessor) (Object) dilation;

			double x1 = (offset.x - extra.CML$getRadiusX()) / 16.0;
			double y1 = (offset.y - extra.CML$getRadiusY()) / 16.0;
			double z1 = (offset.z - extra.CML$getRadiusZ()) / 16.0;

			double x2 = (offset.x + dimensions.x + extra.CML$getRadiusX()) / 16.0;
			double y2 = (offset.y + dimensions.y + extra.CML$getRadiusY()) / 16.0;
			double z2 = (offset.z + dimensions.z + extra.CML$getRadiusZ()) / 16.0;

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

	/**
	 * Returns the oriented bounds of a model part in world space.
	 *
	 * The existing Box bounds are NOT modified by this.
	 */
	private OmniBox getWorldOrientedPartBounds(
		ModelPartData part,
		T entity
	) {
		OmniBox localBounds =
			getOrientedPartBounds(part);

		Vec3d entityPos =
			entity.getPos();

		float entityYaw = (float) entity.getRotationVec(1.0f).getY();

		Quaternionf entityRotation =
			Axis.Y_POSITIVE.rotationDegrees(
				180.0F - entityYaw
			);

		Quaternionf partRotation =
			new Quaternionf();

		Vec3d localRotation =
			localBounds.getRotation();

		partRotation.rotateXYZ(
			(float) Math.toRadians(localRotation.x),
			(float) Math.toRadians(localRotation.y),
			(float) Math.toRadians(localRotation.z)
		);

		entityRotation.mul(partRotation);

		Vector3f center =
			new Vector3f(
				(float) localBounds.getCenter().x,
				(float) localBounds.getCenter().y,
				(float) localBounds.getCenter().z
			);

		center.rotate(entityRotation);

		center.add(
			(float) entityPos.x,
			(float) entityPos.y,
			(float) entityPos.z
		);

		Vector3f euler =
			new Vector3f();

		entityRotation.getEulerAnglesXYZ(
			euler
		);

		Vec3d worldRotation =
			new Vec3d(
				Math.toDegrees(euler.x),
				Math.toDegrees(euler.y),
				Math.toDegrees(euler.z)
			);

		OmniBox worldBounds =
			new OmniBox(
				(float) (center.x - localBounds.getXLength() * 0.5D),
				(float) (center.y - localBounds.getYLength() * 0.5D),
				(float) (center.z - localBounds.getZLength() * 0.5D),
				(float) (center.x + localBounds.getXLength() * 0.5D),
				(float) (center.y + localBounds.getYLength() * 0.5D),
				(float) (center.z + localBounds.getZLength() * 0.5D),
				worldRotation
			);

		return worldBounds;
	}

	/**
	 * Tests whether the player is looking directly at a model part.
	 */
	public boolean isLookingAt(PlayerEntity player, T entity, ModelPartData part) {
		if (player == null || part == null)
			return false;

		Box bounds = getPartBounds(part);

		Vec3d origin = player.getEyePos();
		Vec3d direction = player.getRotationVec(1.0F);

		Vec3d end = origin.add(direction.multiply(64.0D));

		Vec3d entityPos = entity.getPos();

		origin = origin.subtract(entityPos);
		end = end.subtract(entityPos);

		float yaw = entity.getYaw();

		Quaternionf inverseEntityRotation =
			Axis.Y_POSITIVE
				.rotationDegrees(yaw);

		Vector3f localOrigin = new Vector3f(
			(float) origin.x,
			(float) origin.y,
			(float) origin.z
		);

		Vector3f localEnd = new Vector3f(
			(float) end.x,
			(float) end.y,
			(float) end.z
		);

		localOrigin.rotate(inverseEntityRotation);
		localEnd.rotate(inverseEntityRotation);

		Vec3d rayOrigin = new Vec3d(
			localOrigin.x,
			localOrigin.y,
			localOrigin.z
		);

		Vec3d rayEnd = new Vec3d(
			localEnd.x,
			localEnd.y,
			localEnd.z
		);

		return bounds.raycast(rayOrigin, rayEnd).isPresent();
	}
	//endregion
}

package com.ubunifu.commonside.builder;

import com.ubunifu.commonside.mixin.*;
import net.minecraft.client.model.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.*;
import org.joml.*;

import java.lang.Math;
import java.util.*;

class CMLConstructor {
	public record Compiled(HashMap<ModelPartId, CommonPart> commonPartHashMap){}
	public static Compiled compile(CommonsideModelInstance<?> instance) {
		ModelData modelData = instance.modelData;
		MatrixStack globalMatrix = new MatrixStack();
		HashMap<ModelPartId, CommonPart> commonPartHashMap = new HashMap<>();

		processChild(
			commonPartHashMap,
			modelData,
			"root",
			modelData.getRoot(),
			globalMatrix,
			globalMatrix
		);
		return new Compiled(commonPartHashMap);
	}

	@SuppressWarnings("DataFlowIssue")
	private static void processChild(HashMap<ModelPartId, CommonPart> commonPartHashMap,
									 ModelData modelData,
	                                 String partName,
	                                 ModelPartData partData,
	                                 MatrixStack globalMatrix,
	                                 MatrixStack parentMatrix) {

//		ModelPartId id,
//		Vec3d pivot,
//		Vec3d size,
//		Matrix4f globalMatrix,
//		Matrix4f parentMatrix
		ModelPartId partId = ModelPartId.create( partData );
		ModelTransform modelTransform = ((ModelPartDataAccessor)(Object)partData).CML$getRotationData();
		List<ModelCuboidData> cuboidData = ((ModelPartDataAccessor)(Object)partData).CML$getCuboidData();

		Vec3d pivot = new Vec3d(modelTransform.pivotX, modelTransform.pivotY, modelTransform.pivotZ);
		Vec3d rotation = new Vec3d(modelTransform.pitch, modelTransform.yaw, modelTransform.roll);
		double minX = Double.POSITIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double minZ = Double.POSITIVE_INFINITY;

		double maxX = Double.NEGATIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		double maxZ = Double.NEGATIVE_INFINITY;

		for (ModelCuboidData data : cuboidData) {
			Vector3f offset =
				((ModelCuboidDataAccessor) (Object) data).CML$getOffset();

			Vector3f dimensions =
				((ModelCuboidDataAccessor) (Object) data).CML$getDimensions();

			Dilation dilation =
				((ModelCuboidDataAccessor) (Object) data).CML$getExtraSize();

			DilationAccessor extra = (DilationAccessor) (Object) dilation;

			double x1 = offset.x - extra.CML$getRadiusX();
			double y1 = offset.y - extra.CML$getRadiusY();
			double z1 = offset.z - extra.CML$getRadiusZ();

			double x2 = offset.x + dimensions.x + extra.CML$getRadiusX();
			double y2 = offset.y + dimensions.y + extra.CML$getRadiusY();
			double z2 = offset.z + dimensions.z + extra.CML$getRadiusZ();

			minX = Math.min(minX, x1);
			minY = Math.min(minY, y1);
			minZ = Math.min(minZ, z1);

			maxX = Math.max(maxX, x2);
			maxY = Math.max(maxY, y2);
			maxZ = Math.max(maxZ, z2);
		}
		Box partSize = new Box(minX,minY,minZ,maxX,maxY,maxZ);
		Vec3d size = new Vec3d(partSize.getXLength(), partSize.getYLength(), partSize.getZLength());

		MatrixStack pMatrix = cloneMatrixStack( globalMatrix );

		globalMatrix.multiply(Axis.X_POSITIVE.rotationDegrees((float) rotation.getX()));
		globalMatrix.multiply(Axis.Y_POSITIVE.rotationDegrees((float) rotation.getY()));
		globalMatrix.multiply(Axis.Z_POSITIVE.rotationDegrees((float) rotation.getZ()));

		globalMatrix.scale((float) size.x, (float) size.y, (float) size.z);

		globalMatrix.translate(pivot.x, pivot.y, pivot.z);


		CommonPart commonPart = new CommonPart(
			partId,
			pivot,
			size,
			globalMatrix.peek().getModel(),
			pMatrix.peek().getModel()
		);
		commonPartHashMap.put( partId, commonPart );
		Map<String, ModelPartData> children = ((ModelPartDataAccessor)(Object)partData).CML$getChildren();
		children.forEach((childName,child)->{
			processChild(
				commonPartHashMap,
				modelData,
				childName,
				child,
				cloneMatrixStack(globalMatrix),
				pMatrix
			);
		});
	}

	private static MatrixStack cloneMatrixStack(MatrixStack matrixStack) {
		MatrixStack matrixStack1 = new MatrixStack();
		matrixStack1.multiplyMatrix(matrixStack.peek().getModel());
		return matrixStack1;
	}
}

package com.ubunifu.commonside;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ubunifu.commonside.builder.*;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.feature.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.*;
import org.joml.*;

import java.util.function.Supplier;

public class CSMHeldItemFeature<T extends LivingEntity & CSMEntity,M extends EntityModel<T> & CSMModelWithArms & CSModel> extends HeldItemFeatureRenderer<T,M> {
	private final HeldItemRenderer heldItemRenderer;
	private final Supplier<Vector3f> scale;
	public CSMHeldItemFeature(FeatureRendererContext<T, M> context, HeldItemRenderer heldItemRenderer, Supplier<Vector3f> scale) {
		super(context, heldItemRenderer);
		this.heldItemRenderer = heldItemRenderer;
		this.scale = scale;
	}

	@Override
	protected void renderItem(LivingEntity entity, ItemStack stack, ModelTransformationMode transformationMode, Arm arm, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		if (!stack.isEmpty()) {
			matrices.push();
			M model = this.getContextModel();
			String armPart = model.getArmName(arm);
			//noinspection unchecked
			try(CSMModelInstance modelInstance = CSMModelInstance.createWithModel((T)entity, true))
			{
				//noinspection unchecked
				modelInstance.animateModel((T)entity,model,false);
				CSMModel csmModel = modelInstance.getModel();
				ModelPartData commonPart = csmModel.getPart(armPart);
				Matrix4f partMatrix = new Matrix4f(csmModel.getMatrixForPart( commonPart ));
				Matrix4f modelConversion = new Matrix4f()
					.scale(-1.0F, -1.0F, 1.0F)
					.translate(0.0F, -1.501F, 0.0F);

				Matrix4f armMatrix = modelConversion.invert().mul(partMatrix);
				Vector3f scale = this.scale.get();
				matrices.multiply( armMatrix );
				renderAxis(matrices, vertexConsumers, light);

				matrices.rotate(Axis.X_POSITIVE.rotationDegrees(-90.0F));
				matrices.rotate(Axis.Y_POSITIVE.rotationDegrees(180.0F));
				boolean bl = arm == Arm.LEFT;
//				matrices.translate((float)(bl ? -1 : 1) / 16.0F, 0,0);
				matrices.scale(scale.x,scale.y,scale.z);
				CSMConstants.LOGGER.info(
					"Held item: {}, mode: {}, arm: {}",
					stack.getItem(),
					transformationMode,
					arm
				);
				this.heldItemRenderer.renderItem(entity, stack, transformationMode, bl, matrices, vertexConsumers, light);
			} catch (Exception e) {
				CSMConstants.LOGGER.warn("Failed to render item",e);
			}
			matrices.pop();
		}
	}
	private static void renderAxis(
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light
	) {
		VertexConsumer lines = vertexConsumers.getBuffer(RenderLayer.getLines());

		matrices.push();

		// Make the marker visible at the arm's origin.
		float length = 0.25F;

		WorldRenderer.drawBox(
			matrices,
			lines,
			new Box(-0.025, -0.025, -0.025, 0.025, 0.025, 0.025),
			1.0F, 1.0F, 1.0F, 1.0F
		);

		// X axis: red
		WorldRenderer.drawBox(
			matrices,
			lines,
			new Box(0, -0.002, -0.002, length, 0.002, 0.002),
			1.0F, 0.0F, 0.0F, 1.0F
		);

		// Y axis: green
		WorldRenderer.drawBox(
			matrices,
			lines,
			new Box(-0.002, 0, -0.002, 0.002, length, 0.002),
			0.0F, 1.0F, 0.0F, 1.0F
		);

		// Z axis: blue
		WorldRenderer.drawBox(
			matrices,
			lines,
			new Box(-0.002, -0.002, 0, 0.002, 0.002, length),
			0.0F, 0.0F, 1.0F, 1.0F
		);

		matrices.pop();
	}
}

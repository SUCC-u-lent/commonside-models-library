package com.ubunifu.commonside.mixin;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ubunifu.commonside.*;
import com.ubunifu.commonside.builder.*;
import com.ubunifu.commonside.registry.CSMRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.ColorUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.*;
import net.minecraft.util.math.Axis;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<E extends LivingEntity & CSMEntity, M extends EntityModel<E> & CSModel & CSMModelWithArms> {
	@Shadow
	public abstract M getModel();

	@Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",at= @At("TAIL"))
	private void render(E livingEntity, float f, float g, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci){
		EntityType<?> entityType = livingEntity.getType();
		if (!CSMRegistry.containsEntityType(entityType)) return;
		if (!CSMConstants.shouldRenderCommonsideModel()) return;
		if (!isHitboxesVisible()) return;
		VertexConsumer vertexConsumer =
			vertexConsumerProvider.getBuffer(RenderLayer.getLines());
		matrices.push();
		matrices.rotate(
			Axis.Y_POSITIVE.rotationDegrees(-f)
		);

		M model = this.getModel();

		matrices.scale(
			-1.0F,
			1.0F,
			-1.0F
		);
		int boundsColor = CSMConstants.getCommonsideBoundsColor();
		try (CSMModelInstance modelInstance = CSMModelInstance.createForGeneric(livingEntity, true)) {
			modelInstance.animateModel(livingEntity, model,false);
			for (CommonPart commonPart : modelInstance.getModel().getParts().values()) {
				if (commonPart.isBone()) continue; // Skip bones.
				WorldRenderer.drawBox(
					matrices,
					vertexConsumer,
					commonPart.bounds(),
					(float) ColorUtil.Argb32.getRed(boundsColor) / 255,
					(float) ColorUtil.Argb32.getGreen(boundsColor) / 255,
					(float) ColorUtil.Argb32.getBlue(boundsColor) / 255,
					(float) ColorUtil.Argb32.getAlpha(boundsColor) / 255
				);
			}
		} catch (Exception e) {
			CSMConstants.LOGGER.warn("Failed to draw bounds",e);
		}
		matrices.pop();
	}
	@Unique
	private static boolean isHitboxesVisible()
	{
		try{
			MinecraftClient client = MinecraftClient.getInstance();
			return client.getEntityRenderDispatcher().shouldRenderHitboxes();
		}catch (Exception ignored){ return false; }
	}
}

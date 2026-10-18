package com.ubunifu.commonside.mixin;

import com.mojang.blaze3d.vertex.*;
import com.ubunifu.commonside.CMLConstants;
import com.ubunifu.commonside.builder.*;
import com.ubunifu.commonside.registry.CMLRegistry;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.*;
import net.minecraft.util.math.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
	@Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",at= @At("TAIL"))
	private <E extends LivingEntity> void render(E livingEntity, float f, float g, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci){
		EntityType<?> entityType = livingEntity.getType();
		if (!CMLRegistry.containsEntityType(entityType)) return;
		CommonsideModelInstance<?,?> modelInstance = CommonsideModelInstance.createForGeneric(livingEntity, true );
		if (modelInstance == null) return;
		VertexConsumer vertexConsumer =
			vertexConsumerProvider.getBuffer(RenderLayer.getLines());
		matrices.push();
		matrices.multiply(
			Axis.Y_POSITIVE.rotationDegrees(-f)
		);

		matrices.scale(
			-1.0F,
			1.0F,
			-1.0F
		);
		for (CommonPart commonPart : modelInstance.getParts()) {
			if (commonPart.isBone()) continue; // Skip bones.
			WorldRenderer.drawBox(
				matrices,
				vertexConsumer,
				commonPart.bounds(),
				1,0,0,1
			);
		}
		matrices.pop();
	}
}

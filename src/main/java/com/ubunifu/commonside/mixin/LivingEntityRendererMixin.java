package com.ubunifu.commonside.mixin;

import com.ubunifu.commonside.builder.CommonsideModelInstance;
import com.ubunifu.commonside.registry.CMLRegistry;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.*;
import net.minecraft.util.math.*;
import org.joml.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
	@Inject(method = "render(Lnet/minecraft/entity/Entity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",at=@At("HEAD"))
	private void render(Entity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci){
		EntityType<?> entityType = entity.getType();
		if (!CMLRegistry.containsEntityType(entityType)) return;
		CommonsideModelInstance<?> modelInstance = CommonsideModelInstance.create( entity );
		modelInstance.map.forEach((partId,commonPart)->{
			Vector3f position = commonPart.globalMatrix().getTranslation(new Vector3f());
			Vector3f size = commonPart.size().toVector3f();
			WorldRenderer.drawBox(
				matrices,
				vertexConsumers.getBuffer(RenderLayer.LINES),
				Box.of( new Vec3d(position.x,position.y,position.z), size.x,size.y,size.z ),
				1,
				0,
				0,
				1
			);
		});
	}
}

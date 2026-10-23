package com.ubunifu.commonside.builder;

import com.ubunifu.commonside.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.tick.TickManager;

class CSMModelAnimator {
	public static <T extends LivingEntity & CSMEntity, M extends EntityModel<T>> void animate(CSMModel csmModel, T entity, M model)
	{
		World world = entity.getWorld();
		float tickDelta = getTickDelta();
		worldAnimate(world, entity, csmModel, model, tickDelta);
	}
	private static float getTickDelta()
	{
		try{
			MinecraftClient client = MinecraftClient.getInstance();
			return client.getTickDelta();
		}catch (Exception ignored){ return 1;}
	}

	// region WorldRenderer Animation
	private static <T extends LivingEntity & CSMEntity, M extends EntityModel<T>> void worldAnimate(World world, T entity, CSMModel csmModel, M model, float tickDelta) {
		TickManager tickManager = world.getTickManager();
		float f = tickManager.shouldTick() ? tickDelta : 1.0F;
		MatrixStack matrices = new MatrixStack();
		if (entity.age == 0) {
			entity.lastRenderX = entity.getX();
			entity.lastRenderY = entity.getY();
			entity.lastRenderZ = entity.getZ();
		}

		float j = tickManager.shouldSkipTick(entity) ? f : tickDelta;
		animateEntity(entity, j, matrices,csmModel,model);
	}

	@SuppressWarnings("unchecked")
	private static <T extends LivingEntity & CSMEntity, M extends EntityModel<T>> void animateEntity(Entity entity,
	                                                                       float tickDelta,
	                                                                       MatrixStack matrices,
																		   CSMModel csmModel,
																		   M model) {
		T e = (T) entity;
		double x = MathHelper.lerp((double) tickDelta, e.lastRenderX, e.getX());
		double y = MathHelper.lerp((double) tickDelta, e.lastRenderY, e.getY());
		double z = MathHelper.lerp((double) tickDelta, e.lastRenderZ, e.getZ());
		float yaw = MathHelper.lerp(tickDelta, e.prevYaw, e.getYaw());

		// camera is (0,0,0): position doesn't affect animation
		dispatcherAnimate(e, x, y, z, yaw, tickDelta, matrices, csmModel, model);
	}
	// endregion
	// region EntityRenderDispatcher Animation
	private static <T extends LivingEntity & CSMEntity, M extends EntityModel<T>> void dispatcherAnimate(
		T entity, double x, double y, double z, float yaw, float tickDelta,
		MatrixStack matrices, CSMModel csmModel, M model) {
		try {
			matrices.push();
			matrices.translate(x, y, z);
			rendererAnimate(yaw, tickDelta, matrices, csmModel, entity, model);
			matrices.pop();
		} catch (Throwable throwable) {
			CSMConstants.LOGGER.warn("Failed to handle animate",throwable);
		}
	}
	// endregion
	// region Living Entity Render Animation
	private static <T extends LivingEntity & CSMEntity, M extends EntityModel<T>> void rendererAnimate(float f,float g,MatrixStack matrices, CSMModel csmModel, T livingEntity, M model) {
		matrices.push();
		model.handSwingProgress = getHandSwingProgress(livingEntity, g);
		model.riding = livingEntity.hasVehicle();
		model.child = livingEntity.isBaby();
		float h = MathHelper.lerpDegrees(g, livingEntity.prevBodyYaw, livingEntity.bodyYaw);
		float j = MathHelper.lerpDegrees(g, livingEntity.prevHeadYaw, livingEntity.headYaw);
		float k = j - h;
		if (livingEntity.hasVehicle()) {
			Entity var11 = livingEntity.getVehicle();
			if (var11 instanceof LivingEntity) {
				LivingEntity livingEntity2 = (LivingEntity)var11;
				h = MathHelper.lerpDegrees(g, livingEntity2.prevBodyYaw, livingEntity2.bodyYaw);
				k = j - h;
				float l = MathHelper.wrapDegrees(k);
				if (l < -85.0F) {
					l = -85.0F;
				}

				if (l >= 85.0F) {
					l = 85.0F;
				}

				h = j - l;
				if (l * l > 2500.0F) {
					h += l * 0.2F;
				}

				k = j - h;
			}
		}

		float m = MathHelper.lerp(g, livingEntity.prevPitch, livingEntity.getPitch());
		if (renderFlipped(livingEntity)) {
			m *= -1.0F;
			k *= -1.0F;
		}

		k = MathHelper.wrapDegrees(k);
		if (livingEntity.hasPose(EntityPose.SLEEPING)) {
			Direction direction = livingEntity.getSleepingDirection();
			if (direction != null) {
				float n = livingEntity.getEyeHeight(EntityPose.STANDING) - 0.1F;
				matrices.translate((float)(-direction.getOffsetX()) * n, 0.0F, (float)(-direction.getOffsetZ()) * n);
			}
		}

		float l = livingEntity.getScaleFactor();
		matrices.scale(l, l, l);
		float n = getAnimationProgress(livingEntity, g);
		setupTransforms(livingEntity, matrices, n, h, g, l);
		matrices.scale(-1.0F, -1.0F, 1.0F);
		matrices.translate(0.0F, -1.501F, 0.0F);
		float o = 0.0F;
		float p = 0.0F;
		if (!livingEntity.hasVehicle() && livingEntity.isAlive()) {
			o = livingEntity.limbData.getLimbDistance(g);
			p = livingEntity.limbData.getLimbAngle(g);
			if (livingEntity.isBaby()) {
				p *= 3.0F;
			}

			if (o > 1.0F) {
				o = 1.0F;
			}
		}

		model.animateModel(livingEntity, p, o, g);
		model.setAngles(livingEntity, p, o, n, k, m);
		matrices.pop();
	}

	protected static <T extends LivingEntity & CSMEntity> boolean isShaking(T entity) {
		return entity.isFrozen();
	}
	protected static <T extends LivingEntity & CSMEntity> void setupTransforms(T entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta, float f) {
		if (isShaking(entity)) {
			bodyYaw += (float)(Math.cos((double)entity.age * (double)3.25F) * Math.PI * (double)0.4F);
		}

		if (!entity.hasPose(EntityPose.SLEEPING)) {
			matrices.rotate(Axis.Y_POSITIVE.rotationDegrees(180.0F - bodyYaw));
		}

		if (entity.deathTime > 0) {
			float g = ((float)entity.deathTime + tickDelta - 1.0F) / 20.0F * 1.6F;
			g = MathHelper.sqrt(g);
			if (g > 1.0F) {
				g = 1.0F;
			}

			matrices.rotate(Axis.Z_POSITIVE.rotationDegrees(g * getLyingAngle(entity)));
		} else if (entity.isUsingRiptide()) {
			matrices.rotate(Axis.X_POSITIVE.rotationDegrees(-90.0F - entity.getPitch()));
			matrices.rotate(Axis.Y_POSITIVE.rotationDegrees(((float)entity.age + tickDelta) * -75.0F));
		} else if (entity.hasPose(EntityPose.SLEEPING)) {
			Direction direction = entity.getSleepingDirection();
			float h = direction != null ? getYaw(direction) : bodyYaw;
			matrices.rotate(Axis.Y_POSITIVE.rotationDegrees(h));
			matrices.rotate(Axis.Z_POSITIVE.rotationDegrees(getLyingAngle(entity)));
			matrices.rotate(Axis.Y_POSITIVE.rotationDegrees(270.0F));
		} else if (renderFlipped(entity)) {
			matrices.translate(0.0F, (entity.getHeight() + 0.1F) / f, 0.0F);
			matrices.rotate(Axis.Z_POSITIVE.rotationDegrees(180.0F));
		}
	}

	private static float getYaw(Direction direction) {
		return switch (direction) {
			case SOUTH -> 90.0F;
			case NORTH -> 270.0F;
			case EAST -> 180.0F;
			default -> 0.0F;
		};
	}
	protected static <T extends LivingEntity & CSMEntity> float getLyingAngle(T entity) {
		return 90.0F;
	}
	protected static <T extends LivingEntity & CSMEntity> boolean isVisible(T entity) {
		return !entity.isInvisible();
	}
	protected static <T extends LivingEntity & CSMEntity> float getAnimationProgress(T entity, float tickDelta) {
		return (float)entity.age + tickDelta;
	}
	protected static <T extends LivingEntity & CSMEntity> float getHandSwingProgress(T entity, float tickDelta) {
		return entity.getHandSwingProgress(tickDelta);
	}
	private static boolean renderFlipped(LivingEntity entity) {
		if (entity instanceof PlayerEntity || entity.hasCustomName()) {
			String string = Formatting.strip(entity.getName().getString());
			if ("Dinnerbone".equals(string) || "Grumm".equals(string)) {
				return !(entity instanceof PlayerEntity) || ((PlayerEntity)entity).isPartVisible(PlayerModelPart.CAPE);
			}
		}

		return false;
	}
	// endregion
}

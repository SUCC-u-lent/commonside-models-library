package com.ubunifu.commonside.mixin;

import net.minecraft.client.model.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.*;

@Mixin(ModelPart.class)
public interface ModelPartAccessor {
	@Accessor("pivotX")
	float CSM$getPivotX();
	@Accessor("pivotY")
	float CSM$getPivotY();
	@Accessor("pivotZ")
	float CSM$getPivotZ();

	@Accessor("pitch")
	float CSM$getPitch();
	@Accessor("yaw")
	float CSM$getYaw();
	@Accessor("roll")
	float CSM$getRoll();

	@Accessor("scaleX")
	float CSM$getScaleX();
	@Accessor("scaleY")
	float CSM$getScaleY();
	@Accessor("scaleZ")
	float CSM$getScaleZ();

	@Accessor("visible")
	boolean CSM$getVisible();
	@Accessor("skipDraw")
	boolean CSM$getSkipDraw();

	@Accessor("cuboids")
	List<ModelPart.Cuboid> CSM$getCuboids();

	@Accessor("children")
	Map<String, ModelPart> CSM$getChildren();

	@SuppressWarnings("SpellCheckingInspection")
	@Accessor("initalTransform")
	ModelTransform CSM$getInitialTransform();
}

package com.ubunifu.commonside.mixin;

import net.minecraft.client.model.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.*;

@Mixin(ModelPart.class)
public interface ModelPartAccessor {
	@Accessor("pivotX")
	float CML$getPivotX();
	@Accessor("pivotY")
	float CML$getPivotY();
	@Accessor("pivotZ")
	float CML$getPivotZ();

	@Accessor("pitch")
	float CML$getPitch();
	@Accessor("yaw")
	float CML$getYaw();
	@Accessor("roll")
	float CML$getRoll();

	@Accessor("scaleX")
	float CML$getScaleX();
	@Accessor("scaleY")
	float CML$getScaleY();
	@Accessor("scaleZ")
	float CML$getScaleZ();

	@Accessor("visible")
	boolean CML$getVisible();
	@Accessor("skipDraw")
	boolean CML$getSkipDraw();

	@Accessor("cuboids")
	List<ModelPart.Cuboid> CML$getCuboids();

	@Accessor("children")
	Map<String, ModelPart> CML$getChildren();

	@SuppressWarnings("SpellCheckingInspection")
	@Accessor("initalTransform")
	ModelTransform CML$getInitialTransform();
}

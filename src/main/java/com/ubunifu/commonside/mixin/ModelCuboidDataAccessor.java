package com.ubunifu.commonside.mixin;

import net.minecraft.client.model.*;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(ModelCuboidData.class)
public interface ModelCuboidDataAccessor {
	@Accessor("name")
	String CML$getName();

	@Accessor("offset")
	Vector3f CML$getOffset();
	@Accessor("dimensions")
	Vector3f CML$getDimensions();

	@Accessor("extraSize")
	Dilation CML$getExtraSize();

	@Accessor("mirror")
	boolean CML$getMirror();

	@Accessor("textureUV")
	Vector2f CML$textureUV();
	@Accessor("textureScale")
	Vector2f CML$textureScale();

	@Accessor("directions")
	Set<Direction> CML$getDirections();
}

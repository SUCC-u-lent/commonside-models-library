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
	String CSM$getName();

	@Accessor("offset")
	Vector3f CSM$getOffset();
	@Accessor("dimensions")
	Vector3f CSM$getDimensions();

	@Accessor("extraSize")
	Dilation CSM$getExtraSize();

	@Accessor("mirror")
	boolean CSM$getMirror();

	@Accessor("textureUV")
	Vector2f CSM$textureUV();
	@Accessor("textureScale")
	Vector2f CSM$textureScale();

	@Accessor("directions")
	Set<Direction> CSM$getDirections();
}

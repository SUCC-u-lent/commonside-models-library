package com.ubunifu.commonside.builder;

import net.minecraft.util.math.Vec3d;
import org.joml.*;

public record CommonPart(
	ModelPartId id,
	Vec3d pivot,
	Vec3d size,
	Matrix4f globalMatrix,
	Matrix4f parentMatrix
) {}

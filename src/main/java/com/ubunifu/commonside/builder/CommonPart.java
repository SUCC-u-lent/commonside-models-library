package com.ubunifu.commonside.builder;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.*;
import org.joml.*;

public record CommonPart(
	int partCode,
	ModelPartData part,
	Vector3f pivot,
	Vector3f rotation,
	Matrix4f global,
	Box bounds,
	String[] childrenNames,
	OmniBox orientatedBox,
	boolean isBone
) { }

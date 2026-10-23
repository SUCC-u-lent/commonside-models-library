package com.ubunifu.commonside.builder;

import net.minecraft.client.model.ModelPartData;
import net.minecraft.util.math.Box;
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

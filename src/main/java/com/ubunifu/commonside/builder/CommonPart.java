package com.ubunifu.commonside.builder;

import net.minecraft.client.model.*;
import net.minecraft.util.math.*;
import org.joml.*;

public record CommonPart(
	int partCode,
	ModelPartData part,
	Vector3f pivot,
	Vector3f rotation,
	Matrix4f global,
	Box bounds,
	OmniBox orientatedBox,
	boolean isBone
) {}

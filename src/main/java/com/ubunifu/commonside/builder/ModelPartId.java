package com.ubunifu.commonside.builder;

import com.ubunifu.commonside.mixin.*;
import net.minecraft.client.model.*;

import java.util.*;

@SuppressWarnings("DataFlowIssue")
public record ModelPartId(int hash) {
	public static ModelPartId create(ModelPart part)
	{
		return new ModelPartId(Objects.hash(
			((ModelPartAccessor)(Object)part).CML$getInitialTransform(),
			((ModelPartAccessor)(Object)part).CML$getCuboids(),
			((ModelPartAccessor)(Object)part).CML$getChildren()
		));
	}
	public static ModelPartId create(ModelPartData part)
	{
		return new ModelPartId(Objects.hash(
			((ModelPartDataAccessor)(Object)part).CML$getCuboidData(),
			((ModelPartDataAccessor)(Object)part).CML$getRotationData(),
			((ModelPartDataAccessor)(Object)part).CML$getChildren()
		));
	}
}

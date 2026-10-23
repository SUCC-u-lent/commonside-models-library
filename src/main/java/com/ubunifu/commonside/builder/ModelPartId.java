package com.ubunifu.commonside.builder;

import com.ubunifu.arctic_library.commonside.mixin.*;
import net.minecraft.client.model.*;

import java.util.Objects;

@SuppressWarnings("DataFlowIssue")
public record ModelPartId(int hash) {
	public static ModelPartId create(ModelPart part)
	{
		return new ModelPartId(Objects.hash(
			((ModelPartAccessor)(Object)part).CSM$getInitialTransform(),
			((ModelPartAccessor)(Object)part).CSM$getCuboids(),
			((ModelPartAccessor)(Object)part).CSM$getChildren()
		));
	}
	public static ModelPartId create(ModelPartData part)
	{
		return new ModelPartId(Objects.hash(
			((ModelPartDataAccessor)(Object)part).CSM$getCuboidData(),
			((ModelPartDataAccessor)(Object)part).CSM$getRotationData(),
			((ModelPartDataAccessor)(Object)part).CSM$getChildren()
		));
	}
}

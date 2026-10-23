package com.ubunifu.commonside.mixin;

import net.minecraft.client.model.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.*;

@Mixin(ModelPartData.class)
public interface ModelPartDataAccessor {

	@Accessor("cuboidData")
	List<ModelCuboidData> CSM$getCuboidData();
	@Accessor("rotationData")
	ModelTransform CSM$getRotationData();
	@Accessor("children")
	Map<String, ModelPartData> CSM$getChildren();
}

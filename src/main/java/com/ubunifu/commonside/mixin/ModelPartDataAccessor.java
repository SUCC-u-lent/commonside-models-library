package com.ubunifu.commonside.mixin;

import com.google.common.collect.Maps;
import net.minecraft.client.model.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.*;

@Mixin(ModelPartData.class)
public interface ModelPartDataAccessor {

	@Accessor("cuboidData")
	List<ModelCuboidData> CML$getCuboidData();
	@Accessor("rotationData")
	ModelTransform CML$getRotationData();
	@Accessor("children")
	Map<String, ModelPartData> CML$getChildren();
}

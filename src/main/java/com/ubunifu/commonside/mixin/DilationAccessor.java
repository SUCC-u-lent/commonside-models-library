package com.ubunifu.commonside.mixin;

import net.minecraft.client.model.Dilation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Dilation.class)
public interface DilationAccessor {
	@Accessor("radiusX")
	float CSM$getRadiusX();
	@Accessor("radiusY")
	float CSM$getRadiusY();
	@Accessor("radiusZ")
	float CSM$getRadiusZ();
}

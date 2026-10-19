package com.ubunifu.commonside;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.util.Arm;

public interface CSMModelWithArms extends ModelWithArms {
	String getArmName(Arm arm);
}

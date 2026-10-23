package com.ubunifu.commonside;

import net.minecraft.client.model.ModelPart;

public interface CSModel {
	ModelPart getRoot();

	ModelPart getPartChild(ModelPart part, String childName);
}

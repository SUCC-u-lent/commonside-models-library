package com.ubunifu.commonside;

import com.ubunifu.commonside.builder.ModelPartId;
import net.minecraft.client.model.ModelPart;

public interface CSModel {
	ModelPart getRoot();

	ModelPart getPartChild(ModelPart part, String childName);
}

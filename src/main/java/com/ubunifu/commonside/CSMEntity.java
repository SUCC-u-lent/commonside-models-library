package com.ubunifu.commonside;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;

public interface CSMEntity {
	EntityModel<?> getEntityModel(ModelPart root);
	TexturedModelData getModelData();
}

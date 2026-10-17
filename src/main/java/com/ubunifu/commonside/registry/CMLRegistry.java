package com.ubunifu.commonside.registry;

import com.ubunifu.commonside.CommonsideMod;
import com.ubunifu.commonside.interfaces.CommonsideModel;
import net.minecraft.client.model.ModelData;
import net.minecraft.entity.*;

import java.util.*;

public class CMLRegistry {
	private static final Map<EntityType<?>, ModelData> MODEL_MAP = new HashMap<>();
	/**
	 * Register the model data for an entity, this is how our system maps the model.
	 * The ModelData you provide should be the instance fed into {@link net.minecraft.client.model.TexturedModelData#of(ModelData, int, int)}
	 */
	public static ModelData register(EntityType<?> entityType, ModelData modelData) {
		MODEL_MAP.put( entityType, modelData );
		return modelData;
	}
	public static ModelData getModelFor(EntityType<?> entityType) {
		return MODEL_MAP.get( entityType );
	}
	public static boolean containsEntityType(EntityType<?> entityType)
	{
		return MODEL_MAP.containsKey( entityType );
	}
}

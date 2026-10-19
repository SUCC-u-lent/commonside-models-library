package com.ubunifu.commonside.registry;

import net.minecraft.client.model.ModelData;
import net.minecraft.entity.*;
import org.joml.*;

import java.util.*;

public class CSMRegistry {
	@FunctionalInterface
	public interface Factory {
		RegistryData get();
	}
	public record RegistryData(ModelData data, int textureWidth, int textureHeight){}
	private static final Map<EntityType<?>, RegistryData> MODEL_MAP = new HashMap<>();
	/**
	 * Register the model data for an entity, this is how our system maps the model.
	 * The ModelData you provide should be the instance fed into {@link net.minecraft.client.model.TexturedModelData#of(ModelData, int, int)}
	 */
	public static ModelData register(EntityType<?> entityType, Factory modelData) {
		MODEL_MAP.put( entityType, modelData.get() );
		return modelData.get().data();
	}
	public static ModelData getModelFor(EntityType<?> entityType) {
		return MODEL_MAP.get( entityType ).data();
	}
	public static Vector2i getTextureDimsFor(EntityType<?> entityType) {
		RegistryData registryData = MODEL_MAP.get( entityType );
		return new Vector2i( registryData.textureWidth(), registryData.textureHeight() );
	}
	public static boolean containsEntityType(EntityType<?> entityType)
	{
		return MODEL_MAP.containsKey( entityType );
	}
}

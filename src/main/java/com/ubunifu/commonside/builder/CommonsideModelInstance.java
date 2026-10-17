package com.ubunifu.commonside.builder;

import com.ubunifu.commonside.registry.CMLRegistry;
import net.minecraft.client.model.ModelData;
import net.minecraft.entity.*;
import org.apache.commons.lang3.NotImplementedException;

import java.util.HashMap;

public class CommonsideModelInstance<T extends Entity> {
	EntityType<T> entityType;
	ModelData modelData;
	public final HashMap<ModelPartId, CommonPart> map;

	public CommonsideModelInstance(EntityType<T> entityType) {
		this.entityType = entityType;
		if (!CMLRegistry.containsEntityType( entityType )) throw new NotImplementedException("Entity type: "+entityType.toString()+" does not have a model data registered. Register the model data BEFORE running this.");
		this.modelData = CMLRegistry.getModelFor( entityType );
		this.map = CMLConstructor.compile( this ).commonPartHashMap();
	}
	public static <T extends Entity> CommonsideModelInstance<T> create(T entity) throws ClassCastException {
		//noinspection unchecked
		return CommonsideModelInstance.create( (EntityType<T>) entity.getType() );
	}
	public static <T extends Entity> CommonsideModelInstance<T> create(EntityType<T> entityType) {
		return new CommonsideModelInstance<T>( entityType );
	}
}

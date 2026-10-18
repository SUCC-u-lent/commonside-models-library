package com.ubunifu.commonside.builder;

import com.ubunifu.commonside.*;
import com.ubunifu.commonside.registry.CMLRegistry;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.*;
import org.apache.commons.lang3.NotImplementedException;

import java.util.*;

public class CommonsideModelInstance<T extends LivingEntity & CommonsideEntity, M extends EntityModel<T>> {
	EntityType<T> entityType;
	ModelData modelData;
	public final CMLModel<T,M> map;

	public CommonsideModelInstance(T entity, boolean applyGlobal) {
		//noinspection unchecked
		this(entity, (M) entity.getEntityModel( entity.getModelData().createModel() ), applyGlobal);
	}

	public CommonsideModelInstance(T entity, M model, boolean applyGlobal) {
		//noinspection unchecked
		this.entityType = (EntityType<T>) entity.getType();
		if (!CMLRegistry.containsEntityType( entityType )) throw new NotImplementedException("Entity type: "+entityType.toString()+" does not have a model data registered. Register the model data BEFORE running this.");
		this.modelData = CMLRegistry.getModelFor( entityType );
		this.map = new CMLModel<T,M>( entity, model, this.modelData, applyGlobal );
	}
	public static <T extends LivingEntity & CommonsideEntity, M extends EntityModel<T>> CommonsideModelInstance<T,M> create(T entity, boolean applyTransform) throws ClassCastException {
		return new CommonsideModelInstance<>( entity, applyTransform );
	}
	public static <T extends LivingEntity & CommonsideEntity, M extends EntityModel<T>> CommonsideModelInstance<?,M> createForGeneric(Entity entity, boolean applyTransform) throws ClassCastException {
		try{
			// In try loop which prevents errors from it not being valid.
			//noinspection unchecked
			return create((T)entity, applyTransform);
		} catch (Exception e) {
			CMLConstants.LOGGER.warn("Failed to create model!",e);
		}
		return null;
	}
	public static <T extends LivingEntity & CommonsideEntity, M extends EntityModel<T>> CommonsideModelInstance<T,M> createWithModel(T entity, M model, boolean applyTransform) throws ClassCastException {
		return new CommonsideModelInstance<>( entity, model, applyTransform );
	}
	public static <T extends LivingEntity & CommonsideEntity, M extends EntityModel<T>> CommonsideModelInstance<?,M> createWithModelForGeneric(Entity entity, M model, boolean applyTransform) throws ClassCastException {
		try{
			// In try loop which prevents errors from it not being valid.
			//noinspection unchecked
			return createWithModel((T)entity, model, applyTransform);
		} catch (Exception e) {
			CMLConstants.LOGGER.warn("Failed to create model!",e);
		}
		return null;
	}

	public Collection<CommonPart> getParts() {
		return map.getParts().values();
	}
}

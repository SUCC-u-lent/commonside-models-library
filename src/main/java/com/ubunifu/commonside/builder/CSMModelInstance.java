package com.ubunifu.commonside.builder;

import com.ubunifu.arctic_library.commonside.*;
import com.ubunifu.arctic_library.commonside.registry.CSMRegistry;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.*;
import org.apache.commons.lang3.NotImplementedException;
import org.joml.Vector2i;

public class CSMModelInstance implements AutoCloseable {
	EntityType<?> entityType;
	ModelData modelData;
	private CSMModel map;

	public CSMModelInstance(LivingEntity entity, boolean applyGlobal) {
		this(entity.getType(), applyGlobal);
	}

	public CSMModelInstance(EntityType<?> entityType, boolean applyGlobal) {
		this.entityType = entityType;
		if (!CSMRegistry.containsEntityType( entityType )) throw new NotImplementedException("Entity type: "+entityType.toString()+" does not have a model data registered. Register the model data BEFORE running this.");
		this.modelData = CSMRegistry.getModelFor( entityType );
		this.map = new CSMModel( this.modelData, applyGlobal );
		this.map.scale = CSMRegistry.getModelSize( entityType );
		this.map.offset = CSMRegistry.getModelOffset( entityType );
	}

	public <T extends LivingEntity & CSMEntity, M extends EntityModel<T> & CSModel> void animate(T entity, M model)
	{
		this.map.animateModel(entity,model,false);
	}

	public static <T extends LivingEntity & CSMEntity> CSMModelInstance create(T entity, boolean applyTransform) throws ClassCastException {
		return new CSMModelInstance( entity, applyTransform );
	}
	public static <T extends LivingEntity & CSMEntity> CSMModelInstance createForGeneric(Entity entity, boolean applyTransform) throws ClassCastException {
		try{
			// In try loop which prevents errors from it not being valid.
			//noinspection unchecked
			return create((T)entity, applyTransform);
		} catch (Exception e) {
			CSMConstants.LOGGER.warn("Failed to create model!",e);
			throw new RuntimeException(e);
		}
	}
	public static <T extends LivingEntity & CSMEntity> CSMModelInstance createWithModel(T entity, boolean applyTransform) throws ClassCastException {
		return new CSMModelInstance( entity, applyTransform );
	}
	public static <T extends LivingEntity & CSMEntity> CSMModelInstance createWithModelForGeneric(Entity entity, boolean applyTransform) throws ClassCastException {
		try{
			// In try loop which prevents errors from it not being valid.
			//noinspection unchecked
			return createWithModel((T)entity, applyTransform);
		} catch (Exception e) {
			CSMConstants.LOGGER.warn("Failed to create model!",e);
		}
		return null;
	}

	@Override
	public void close() throws Exception {
		this.modelData = null;
		this.entityType = null;
		this.map.close(); // Close this method first.
		this.map = null;
		// By setting them all to null we dispose of their reference clearing the resources used.
	}

	public static <T extends LivingEntity & CSMEntity> ModelPart createModel(T entity) {
		EntityType<?> entityType = entity.getType();
		ModelData data = CSMRegistry.getModelFor(entityType);
		Vector2i dims = CSMRegistry.getTextureDimsFor(entityType);
		TexturedModelData tmd = TexturedModelData.of(data, dims.x(), dims.y());
		return tmd.createModel();
	}

	public CSMModel getModel() { return this.map; }
	public <T extends LivingEntity & CSMEntity, M extends EntityModel<T> & CSModel> void animateModel(T entity, M model, boolean reset) {
		this.getModel().animateModel(entity,model,reset);
	}
}

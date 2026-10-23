package com.ubunifu.commonside.example;

import com.ubunifu.arctic_library.commonside.*;
import com.ubunifu.arctic_library.commonside.registry.CSMRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.*;
import net.minecraft.world.World;
import org.quiltmc.loader.api.QuiltLoader;

public class ExampleMobEntity extends MobEntity implements CSMEntity {
	public static EntityType<ExampleMobEntity> TYPE;

	public ExampleMobEntity(EntityType<? extends MobEntity> entityType, World world) {
		super(entityType, world);
	}

	public static DefaultAttributeContainer.Builder createAttributes() {
		return MobEntity.createAttributes()
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
			.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0);
	}
	public static void Register() {
		if (!QuiltLoader.isDevelopmentEnvironment()) return;
		TYPE = Registry.register(Registries.ENTITY_TYPE, CSMConstants.createIdentifier("example_mob"), EntityType.Builder.create(
			ExampleMobEntity::new,
			SpawnGroup.MISC
			)
			.setDimensions(1,1)
			.build());

		FabricDefaultAttributeRegistry.register(
			TYPE,
			createAttributes()
		);
		CSMRegistry.register(TYPE, ExampleMobEntityModel::buildRegistryData);
	}

	@Override
	public EntityModel<?> getEntityModel(ModelPart root) {
		return new ExampleMobEntityModel(root);
	}

	@Override
	public TexturedModelData getModelData() {
		return ExampleMobEntityModel.createModel();
	}
}

package com.ubunifu.commonside.example;

import com.ubunifu.commonside.CMLConstants;
import com.ubunifu.commonside.registry.CMLRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.*;
import net.minecraft.world.World;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.qsl.entity.api.QuiltEntityTypeBuilder;

public class ExampleMobEntity extends MobEntity {
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
		TYPE = Registry.register(Registries.ENTITY_TYPE, CMLConstants.createIdentifier("example_mob"), QuiltEntityTypeBuilder.create()
			.entityFactory(ExampleMobEntity::new)
			.setDimensions(EntityDimensions.changing(1,1))
			.build());

		FabricDefaultAttributeRegistry.register(
			TYPE,
			createAttributes()
		);
		CMLRegistry.register(TYPE, ExampleMobEntityModel.getTexturedModelData());
	}
}

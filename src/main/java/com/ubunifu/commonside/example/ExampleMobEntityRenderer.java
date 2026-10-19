package com.ubunifu.commonside.example;

import com.ubunifu.commonside.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.loader.api.minecraft.MinecraftQuiltLoader;

public class ExampleMobEntityRenderer extends LivingEntityRenderer<ExampleMobEntity, ExampleMobEntityModel> {
	public static final EntityModelLayer LAYER = new EntityModelLayer(CSMConstants.createIdentifier("example_mob"),"main");
	public ExampleMobEntityRenderer(EntityRendererFactory.Context ctx) {
		super(ctx, new ExampleMobEntityModel(ctx.getPart(LAYER)), 0.5f);
		this.addFeature(new CSMHeldItemFeature<>(this,ctx.getHeldItemRenderer(),(()->new Vector3f(1.25f,1.25f,1.25f))));
	}

	@Override
	public Identifier getTexture(ExampleMobEntity entity) {
		return CSMConstants.createIdentifier("textures","entity","texture.png");
	}

	public static void Register()
	{
		if (!QuiltLoader.isDevelopmentEnvironment()) return;
		if (MinecraftQuiltLoader.getEnvironmentType() != EnvType.CLIENT) return;
		EntityModelLayerRegistry.registerModelLayer(
			LAYER,
			ExampleMobEntityModel::createModel
		);
		EntityRendererRegistry.register(
			ExampleMobEntity.TYPE,
			ExampleMobEntityRenderer::new
		);
	}
}

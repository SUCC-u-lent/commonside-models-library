package com.ubunifu.commonside;

import com.ubunifu.commonside.example.*;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class CommonsideMod implements ModInitializer {
	@Override
	public void onInitialize(ModContainer mod) {
		ExampleMobEntity.Register();
		ExampleMobEntityRenderer.Register();
	}
}

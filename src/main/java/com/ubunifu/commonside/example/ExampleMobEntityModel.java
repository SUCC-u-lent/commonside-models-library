package com.ubunifu.commonside.example;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ubunifu.commonside.mixin.ModelPartAccessor;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class ExampleMobEntityModel extends EntityModel<ExampleMobEntity> {
	private final ModelPart _root;
	private final ModelPart body;
	private final ModelPart jacket;
	private final ModelPart bust;
	private final ModelPart bust_outer;
	private final ModelPart right_arm;
	private final ModelPart bone7;
	private final ModelPart right_upper_sleeve;
	private final ModelPart right_lower_arm_bone;
	private final ModelPart right_lower_sleeve;
	private final ModelPart right_heldItemAnchor;
	private final ModelPart left_arm;
	private final ModelPart bone5;
	private final ModelPart left_upper_sleeve;
	private final ModelPart left_lower_arm_bone;
	private final ModelPart left_lower_sleeve;
	private final ModelPart left_heldItemAnchor;
	private final ModelPart head;
	private final ModelPart left_eye;
	private final ModelPart right_eye;
	private final ModelPart hat;
	private final ModelPart right_leg;
	private final ModelPart right_foot_bone;
	private final ModelPart right_foot_pants;
	private final ModelPart right_upper_leg_bone;
	private final ModelPart right_upper_leg_pants;
	private final ModelPart left_leg;
	private final ModelPart left_foot_bone;
	private final ModelPart left_foot_pants;
	private final ModelPart left_upper_leg_bone;
	private final ModelPart left_upper_pants;
	private final ModelPart cloak;
	private final ModelPart ear;
	public ExampleMobEntityModel(ModelPart root) {
		this._root = root.getChild("_");
		this.body = this.getChild("body");
		this.jacket = this.getChild("jacket");
		this.bust = this.getChild("bust");
		this.bust_outer = this.getChild("bust_outer");
		this.right_arm = this.getChild("right_arm");
		this.bone7 = this.getChild("bone7");
		this.right_upper_sleeve = this.getChild("right_upper_sleeve");
		this.right_lower_arm_bone = this.getChild("right_lower_arm_bone");
		this.right_lower_sleeve = this.getChild("right_lower_sleeve");
		this.right_heldItemAnchor = this.getChild("right_heldItemAnchor");
		this.left_arm = this.getChild("left_arm");
		this.bone5 = this.getChild("bone5");
		this.left_upper_sleeve = this.getChild("left_upper_sleeve");
		this.left_lower_arm_bone = this.getChild("left_lower_arm_bone");
		this.left_lower_sleeve = this.getChild("left_lower_sleeve");
		this.left_heldItemAnchor = this.getChild("left_heldItemAnchor");
		this.head = this.getChild("head");
		this.left_eye = this.getChild("left_eye");
		this.right_eye = this.getChild("right_eye");
		this.hat = this.getChild("hat");
		this.right_leg = this.getChild("right_leg");
		this.right_foot_bone = this.getChild("right_foot_bone");
		this.right_foot_pants = this.getChild("right_foot_pants");
		this.right_upper_leg_bone = this.getChild("right_upper_leg_bone");
		this.right_upper_leg_pants = this.getChild("right_upper_leg_pants");
		this.left_leg = this.getChild("left_leg");
		this.left_foot_bone = this.getChild("left_foot_bone");
		this.left_foot_pants = this.getChild("left_foot_pants");
		this.left_upper_leg_bone = this.getChild("left_upper_leg_bone");
		this.left_upper_pants = this.getChild("left_upper_pants");
		this.cloak = this.getChild("cloak");
		this.ear = this.getChild("ear");
	}
	private ModelPart getChild(String name) {
		return getChild(_root, name);
	}
	private ModelPart getChild(ModelPart parent, String name){
		if (parent.containsPart(name)) return parent.getChild(name);
		for (ModelPart value : ((ModelPartAccessor) (Object) parent).CML$getChildren().values()) {
			ModelPart p = getChild( value, name );
			if (p != null) return p;
		}
		return null;
	}

	public static ModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData _root = modelPartData.addChild("_", ModelPartBuilder.create(), ModelTransform.pivot(-0.575F, 24.0F, 0.0F));

		ModelPartData body = _root.addChild("body", ModelPartBuilder.create().uv(33, 0).cuboid(-4.0F, -14.0F, -3.0F, 8.0F, 14.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.025F, -15.0F, 0.0F));

		ModelPartData jacket = body.addChild("jacket", ModelPartBuilder.create().uv(33, 20).cuboid(-4.0F, -7.0F, -3.0F, 8.0F, 14.0F, 6.0F, new Dilation(0.25F)), ModelTransform.pivot(0.0F, -7.0F, 0.0F));

		ModelPartData bust = body.addChild("bust", ModelPartBuilder.create(), ModelTransform.pivot(-0.1F, -10.8605F, -2.7093F));

		ModelPartData body_r1 = bust.addChild("body_r1", ModelPartBuilder.create().uv(0, 33).cuboid(-4.0F, -2.0F, -2.0F, 8.0F, 4.0F, 4.0F, new Dilation(-0.2F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

		ModelPartData bust_outer = bust.addChild("bust_outer", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0575F, 0.0828F));

		ModelPartData body_r2 = bust_outer.addChild("body_r2", ModelPartBuilder.create().uv(0, 41).cuboid(-4.0F, -2.0F, -2.0F, 8.0F, 4.0F, 4.0F, new Dilation(-0.1F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

		ModelPartData right_arm = body.addChild("right_arm", ModelPartBuilder.create(), ModelTransform.pivot(-4.975F, -12.0F, 0.0F));

		ModelPartData bone7 = right_arm.addChild("bone7", ModelPartBuilder.create().uv(62, 0).cuboid(-1.925F, -2.0F, -2.5F, 3.0F, 7.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData right_upper_sleeve = bone7.addChild("right_upper_sleeve", ModelPartBuilder.create().uv(62, 13).cuboid(-1.925F, -2.0F, -2.5F, 3.0F, 7.0F, 5.0F, new Dilation(0.1F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData right_lower_arm_bone = right_arm.addChild("right_lower_arm_bone", ModelPartBuilder.create().uv(78, 0).cuboid(-1.5F, 0.0F, -2.5F, 3.0F, 7.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.325F, 5.0F, 0.0F));

		ModelPartData right_lower_sleeve = right_lower_arm_bone.addChild("right_lower_sleeve", ModelPartBuilder.create().uv(78, 13).cuboid(-1.5F, 0.0F, -2.5F, 3.0F, 7.0F, 5.0F, new Dilation(0.1F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData right_heldItemAnchor = right_lower_arm_bone.addChild("right_heldItemAnchor", ModelPartBuilder.create(), ModelTransform.of(0.0F, 6.0F, 0.0F, 0.0F, 0.0F, -3.1416F));

		ModelPartData left_arm = body.addChild("left_arm", ModelPartBuilder.create(), ModelTransform.pivot(5.025F, -12.0F, 0.0F));

		ModelPartData bone5 = left_arm.addChild("bone5", ModelPartBuilder.create().uv(62, 0).mirrored().cuboid(-1.275F, -2.0F, -2.5F, 3.0F, 7.0F, 5.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData left_upper_sleeve = bone5.addChild("left_upper_sleeve", ModelPartBuilder.create().uv(62, 13).cuboid(-1.275F, -2.0F, -2.5F, 3.0F, 7.0F, 5.0F, new Dilation(0.1F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData left_lower_arm_bone = left_arm.addChild("left_lower_arm_bone", ModelPartBuilder.create().uv(78, 0).mirrored().cuboid(-1.5F, 0.0F, -2.5F, 3.0F, 7.0F, 5.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.075F, 5.0F, 0.0F));

		ModelPartData left_lower_sleeve = left_lower_arm_bone.addChild("left_lower_sleeve", ModelPartBuilder.create().uv(78, 13).cuboid(-1.5F, 0.0F, -2.5F, 3.0F, 7.0F, 5.0F, new Dilation(0.1F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData left_heldItemAnchor = left_lower_arm_bone.addChild("left_heldItemAnchor", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 6.0F, 0.0F));

		ModelPartData head = body.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.025F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.025F, -14.0F, 0.0F));

		ModelPartData left_eye = head.addChild("left_eye", ModelPartBuilder.create().uv(67, 30).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.525F, -2.5F, -3.525F));

		ModelPartData right_eye = head.addChild("right_eye", ModelPartBuilder.create().uv(67, 30).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(1.475F, -2.5F, -3.525F));

		ModelPartData hat = head.addChild("hat", ModelPartBuilder.create().uv(0, 16).cuboid(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.25F)), ModelTransform.pivot(-0.025F, -4.0F, 0.0F));

		ModelPartData right_leg = _root.addChild("right_leg", ModelPartBuilder.create(), ModelTransform.pivot(-2.0F, -15.0F, 0.0F));

		ModelPartData right_foot_bone = right_leg.addChild("right_foot_bone", ModelPartBuilder.create().uv(76, 26).cuboid(-2.125F, 0.0F, -3.0F, 4.0F, 7.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 8.0F, 0.0F));

		ModelPartData right_foot_pants = right_foot_bone.addChild("right_foot_pants", ModelPartBuilder.create().uv(76, 55).cuboid(-2.0F, 0.0F, -3.0F, 4.0F, 7.0F, 6.0F, new Dilation(0.25F)), ModelTransform.pivot(-0.125F, 0.0F, 0.0F));

		ModelPartData right_upper_leg_bone = right_leg.addChild("right_upper_leg_bone", ModelPartBuilder.create().uv(77, 40).cuboid(-1.825F, 0.0F, -3.0F, 3.5F, 8.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData right_upper_leg_pants = right_upper_leg_bone.addChild("right_upper_leg_pants", ModelPartBuilder.create().uv(77, 69).cuboid(-1.825F, 0.0F, -3.0F, 3.5F, 8.0F, 6.0F, new Dilation(0.25F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData left_leg = _root.addChild("left_leg", ModelPartBuilder.create(), ModelTransform.pivot(2.0F, -15.0F, 0.0F));

		ModelPartData left_foot_bone = left_leg.addChild("left_foot_bone", ModelPartBuilder.create().uv(76, 26).mirrored().cuboid(-2.0F, 0.0F, -3.0F, 4.0F, 7.0F, 6.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.125F, 8.0F, 0.0F));

		ModelPartData left_foot_pants = left_foot_bone.addChild("left_foot_pants", ModelPartBuilder.create().uv(76, 55).cuboid(-2.0F, 0.0F, -3.0F, 4.0F, 7.0F, 6.0F, new Dilation(0.25F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData left_upper_leg_bone = left_leg.addChild("left_upper_leg_bone", ModelPartBuilder.create().uv(77, 40).mirrored().cuboid(-1.675F, 0.0F, -3.0F, 3.5F, 8.0F, 6.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData left_upper_pants = left_upper_leg_bone.addChild("left_upper_pants", ModelPartBuilder.create().uv(77, 69).cuboid(-1.675F, 0.0F, -3.0F, 3.5F, 8.0F, 6.0F, new Dilation(0.25F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData cloak = _root.addChild("cloak", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData ear = _root.addChild("ear", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		return modelData;
	}
	@Override
	public void setAngles(ExampleMobEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		_root.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}

	public static TexturedModelData createModel() {
		return TexturedModelData.of( getTexturedModelData(), 96, 96 );
	}
}

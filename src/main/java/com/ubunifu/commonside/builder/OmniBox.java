package com.ubunifu.commonside.builder;

import net.minecraft.util.math.*;

public record OmniBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float pitch, float yaw, float roll) {
    public OmniBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
    	this(minX, minY, minZ, maxX, maxY, maxZ, 0, 0, 0);
	}
	public OmniBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		this((float) minX, (float) minY, (float) minZ, (float) maxX, (float) maxY, (float) maxZ, 0, 0, 0);
	}

	public OmniBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, Vec3d rotation) {
		this(minX,minY,minZ,maxX,maxY,maxZ, (float) rotation.x, (float) rotation.y, (float) rotation.z);
	}

	public static OmniBox of(Vec3d center, double sizeX, double sizeY, double sizeZ) {
		double halfX = sizeX / 2;
		double halfY = sizeY / 2;
		double halfZ = sizeZ / 2;

		return new OmniBox(
			center.x - halfX,
			center.y - halfY,
			center.z - halfZ,
			center.x + sizeX,
			center.y + sizeY,
			center.z + sizeZ
		);
	}

	public OmniBox setRotation(Vec3d rotation) {
		return new OmniBox(
			this.minX,
			this.minY,
			this.minZ,
			this.maxX,
			this.maxY,
			this.maxZ,
			(float) rotation.x,
			(float) rotation.y,
			(float) rotation.z
		);
	}

	public Vec3d getRotation() {
		return new Vec3d(pitch,yaw,roll);
	}

	public Box getBox() {
		return new Box(minX,minY,minZ,maxX,maxY,maxZ);
	}

	public Vec3d getCenter() {
		return this.getBox().getCenter();
	}

	public double getXLength() {
		return this.getBox().getXLength();
	}
	public double getYLength() {
		return this.getBox().getYLength();
	}
	public double getZLength() {
		return this.getBox().getZLength();
	}
}

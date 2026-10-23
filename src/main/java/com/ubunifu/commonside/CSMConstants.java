package com.ubunifu.commonside;

import net.minecraft.client.util.ColorUtil;
import net.minecraft.util.Identifier;
import org.slf4j.*;

public class CSMConstants {
	public static final String MOD_ID = "commonside";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static boolean renderCommonsideModel = true;
	private static int commonsideBoundsColor = ColorUtil.Argb32.of(255,255,0,0);

	public static void setRenderCommonsideModel(boolean state) { renderCommonsideModel = state; }
	public static void setCommonsideBoundsColor(int boundsColor) { commonsideBoundsColor = boundsColor; }
	public static boolean shouldRenderCommonsideModel() { return renderCommonsideModel; }
	public static int getCommonsideBoundsColor() { return commonsideBoundsColor; }
	public static Identifier createIdentifier(String... path) {
		return new Identifier(MOD_ID, String.join("/",path));
	}
}

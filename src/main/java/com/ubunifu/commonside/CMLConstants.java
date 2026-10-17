package com.ubunifu.commonside;

import net.minecraft.util.Identifier;
import org.slf4j.*;

public class CMLConstants {
	public static final String MOD_ID = "commonside";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Identifier createIdentifier(String... path) {
		return new Identifier(MOD_ID, String.join("/",path));
	}
}

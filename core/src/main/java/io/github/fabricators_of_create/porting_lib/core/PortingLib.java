package io.github.fabricators_of_create.porting_lib.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.resources.ResourceLocation;

/**
 * Core constants and utils.
 */
public class PortingLib {
	public static final String ID = "porting_lib";
	public static final String NAME = "Porting Lib";
	public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

	public static ResourceLocation id(String path) {
		return new ResourceLocation(ID, path);
	}
}

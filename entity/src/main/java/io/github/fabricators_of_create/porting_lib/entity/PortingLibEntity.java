package io.github.fabricators_of_create.porting_lib.entity;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;

public class PortingLibEntity implements ModInitializer {
	@Override
	public void onInitialize() {
		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			if (entity instanceof MultiPartEntity partEntity) {
				for (PartEntity<?> part : partEntity.getParts()) {
					world.getPartEntityMap().put(part.getId(), part);
				}
			}
		});
		ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
			if(entity instanceof MultiPartEntity partEntity) {
				for (PartEntity<?> part : partEntity.getParts()) {
					world.getPartEntityMap().remove(part.getId());
				}
			}
		});
	}
}

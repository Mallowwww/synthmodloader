package com.synth.modloader;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLLoader;

import java.lang.reflect.Type;

public class SynthAutoEventSubscriber {
    private final Type EVENT_BUS_SUBSCRIBER = EventBusSubscriber.class.getGenericSuperclass();
    private final Type MOD = SynthMod.class.getGenericSuperclass();

    private static final String MOD_BUS_TARGET = "MOD";
    private static final String GAME_BUS_TARGET = "GAME";
    private static IEventBus getGameBus() {
        return FMLLoader.getBindings().getGameBus();
    }

}

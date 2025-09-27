package com.synth.modloader.api;

import com.dylibso.chicory.runtime.ExportFunction;
import com.mojang.serialization.Lifecycle;
import com.synth.modloader.SynthModLoader;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.lang.reflect.Method;
@EventBusSubscriber
public class SynthRegistry {
    public static final ResourceKey<Registry<SynthInstance>> REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.parse("sml:synths"));
    public static final Registry<SynthInstance> REGISTRY = new MappedRegistry<>(REGISTRY_KEY, Lifecycle.stable());

    public static void subscribeEvent(String eventClassLocation, ResourceLocation instanceLocation, IEventBus eventBus) {
        try {
            var eventClass = Class.forName(eventClassLocation);
            var instance = REGISTRY.get(instanceLocation);
            if (eventClass.isAssignableFrom(Event.class)) {
                instance.export(eventClass.getName()).ifPresent(func -> {
                    eventBus.addListener((Class<Event>)eventClass, (Event event) -> func.apply());
                });
            }
        } catch (ReflectiveOperationException e) {
            SynthModLoader.LOGGER.error("Error trying to subscribe event {} to synth {}:\n{}",eventClassLocation,instanceLocation,e);
        }
    }
    @SubscribeEvent
    public static void commonLoad(FMLCommonSetupEvent event) {
        REGISTRY.forEach(instance -> {
            instance.export("commonLoad").ifPresentOrElse(
                    ExportFunction::apply,
                    () -> SynthModLoader.LOGGER.error("Synth {} missing commonLoad function!",instance.location)
            );
        });
    }
//    @SubscribeEvent
//    public static void serverTick(ServerTickEvent.Post post) {
//        REGISTRY.forEach(synthInstance -> synthInstance.export("serverTick").ifPresent(ExportFunction::apply));
//    }
}

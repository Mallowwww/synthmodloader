package com.synth.modloader.api;

import com.dylibso.chicory.runtime.ExportFunction;
import com.dylibso.chicory.runtime.HostFunction;
import com.dylibso.chicory.runtime.Instance;
import com.dylibso.chicory.runtime.Store;
import com.dylibso.chicory.wasm.Parser;
import com.dylibso.chicory.wasm.types.ValType;
import com.mojang.logging.LogUtils;
import com.synth.main.Synth;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.IModBusEvent;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class SynthInstance {
    private static final Logger LOGGER = LogUtils.getLogger();
    public final ResourceLocation location;
    private final Instance wasmInstance;
    private SynthInstance(ResourceLocation _location, Collection<HostFunction> functions) throws IllegalArgumentException {
        location = _location;
        var store = new Store();
        functions.forEach(store::addFunction);
        if (ModList.get().getModFileById(location.getNamespace()) == null)
            throw new IllegalArgumentException("Namespace `" + location.getNamespace() + "` does not exist !");
        var path = ModList.get().getModFileById(location.getNamespace()).getFile().findResource(location.getPath());
        wasmInstance = store.instantiate(location.getPath(), Parser.parse(path));
    }

    public Optional<ExportFunction> export(String functionName) {
        if (wasmInstance != null && wasmInstance.export(functionName) != null)
            return Optional.of(wasmInstance.export(functionName));
        else
            return Optional.empty();

    }

    public static class Builder {
        private List<HostFunction> functions;
        private ResourceLocation location;
        private IEventBus eventBus;
        private Builder() {}

        public Builder function(HostFunction function) {
            functions.add(function);
            return this;
        }
        public Builder location(ResourceLocation location) {
            this.location = location;
            return this;
        }
        public Builder eventBus(IEventBus eventBus) {
            this.eventBus = eventBus;
            return this;
        }
        public SynthInstance build() {
            if (this.location == null || this.eventBus == null)
                throw new IllegalStateException("Cannot build SynthInstance: Missing fields");
            try {
                functions.add(new HostFunction(
                        "sml",
                        "subscribeEvent",
                        List.of(ValType.I32, ValType.I32),
                        List.of(),
                        (Instance instance, long... args) -> {
                            var len = (int) args[0];
                            var offset = (int) args[1];
                            var eventString = instance.memory().readString(offset, len);
                            SynthRegistry.subscribeEvent(eventString, location, eventBus);
                            return null;
                        }
                ));
                functions.add(new HostFunction(
                        "sml",
                        "log",
                        List.of(ValType.I32, ValType.I32),
                        List.of(),
                        (Instance instance, long... args) -> {
                            var len = (int) args[0];
                            var offset = (int) args[1];
                            var logString = instance.memory().readString(offset, len);
                            LOGGER.info(logString);
                            return null;
                        }
                ));
                return new SynthInstance(location, functions);
            } catch (Exception e) {
                Synth.LOGGER.error("Couldn't create SynthInstance !", e);
            }
            return null;

        }

        public static Builder create() {
            var builder = new Builder();
            builder.functions = new ArrayList<>();
            return builder;
        }
    }
}

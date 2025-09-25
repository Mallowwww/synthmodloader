package com.synthmodloader.modloader.integration;

import com.dylibso.chicory.runtime.HostFunction;
import com.dylibso.chicory.runtime.Instance;
import com.dylibso.chicory.wasm.types.FunctionType;
import com.dylibso.chicory.wasm.types.ValType;
import com.synthmodloader.modloader.SynthModLoader;
import com.synthmodloader.modloader.synth.SynthInstance;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class ChicoryIntegration {
    public static void init() {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try {
//            var module = Parser.parse(classloader.getResourceAsStream("test.wasm"));
//            Instance instance = Instance.builder(module).build();
//            ExportFunction testFunction = instance.export("iterFact");
            var synth = SynthInstance.Builder.create()
                    .location(ResourceLocation.fromNamespaceAndPath("sml", "data/logger.wasm"))
                    .function(new HostFunction(
                            "console",
                            "log",
                            FunctionType.of(
                                    List.of(ValType.I32, ValType.I32),
                                    List.of()
                            ),
                            ChicoryIntegration::log
                    ))
                    .build();
            var testFunction = synth.export("logIt").orElseThrow();
            testFunction.apply();
            SynthModLoader.LOGGER.info("OUTPUT");

        } catch(Exception e) {
            SynthModLoader.LOGGER.error("Error loading test module !", e);
        }
    }
    private static long[] log(Instance instance, long... args) {
        var len = (int) args[0];
        var offset = (int) args[1];
        var message = instance.memory().readString(offset, len);
        SynthModLoader.LOGGER.info(message);
        return null;
    }
}

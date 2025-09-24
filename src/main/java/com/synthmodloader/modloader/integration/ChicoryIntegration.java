package com.synthmodloader.modloader.integration;

import com.dylibso.chicory.runtime.ExportFunction;
import com.dylibso.chicory.wasm.types.Value;
import com.dylibso.chicory.wasm.Parser;
import com.dylibso.chicory.runtime.Instance;
import com.synthmodloader.modloader.SynthModLoader;

import java.io.IOException;

public class ChicoryIntegration {
    public static void init() {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try {
            var module = Parser.parse(classloader.getResourceAsStream("test.wasm"));
            Instance instance = Instance.builder(module).build();
            ExportFunction testFunction = instance.export("testFunction");
            SynthModLoader.LOGGER.info("OUTPUT: " + testFunction.apply(1)[0]);
        } catch(Exception e) {
            SynthModLoader.LOGGER.error("Error loading test module !", e);
        }


    }
}

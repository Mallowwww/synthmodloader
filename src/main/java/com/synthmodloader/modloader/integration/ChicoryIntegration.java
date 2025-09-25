package com.synthmodloader.modloader.integration;

import com.dylibso.chicory.runtime.ExportFunction; // works fine
import com.dylibso.chicory.wasm.Parser; // breaks
import com.dylibso.chicory.runtime.Instance;
import com.synthmodloader.modloader.SynthModLoader;

import java.io.IOException;

public class ChicoryIntegration {
    public static void init() {
        SynthModLoader.LOGGER.info("ChicoryIntegration.init() called");
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try {
            SynthModLoader.LOGGER.info("Attempting to load test.wasm resource");
            var inputStream = classloader.getResourceAsStream("test.wasm");
            if (inputStream == null) {
                SynthModLoader.LOGGER.error("test.wasm resource not found!");
                return;
            }
            SynthModLoader.LOGGER.info("test.wasm resource found, parsing...");
            var module = Parser.parse(inputStream);
            SynthModLoader.LOGGER.info("WASM module parsed successfully, creating instance...");
            Instance instance = Instance.builder(module).build();
            SynthModLoader.LOGGER.info("Instance created, trying to find any function export...");
            
            // Try common function names that might exist in the WASM file
            String[] possibleFunctionNames = {"iterFact", "testFunction", "main", "add", "multiply", "hello", "test"};
            
            for (String functionName : possibleFunctionNames) {
                try {
                    SynthModLoader.LOGGER.info("Trying to find export: " + functionName);
                    ExportFunction function = instance.export(functionName);
                    SynthModLoader.LOGGER.info("Found function: " + functionName + ", calling with parameter 1...");
                    var result = function.apply(1);
                    SynthModLoader.LOGGER.info("OUTPUT from " + functionName + ": " + result[0]);
                    return; // Success, exit the function
                } catch (Exception e) {
                    SynthModLoader.LOGGER.info("Function " + functionName + " not found: " + e.getMessage());
                }
            }
            
            SynthModLoader.LOGGER.warn("No function exports found in the WASM module");
        } catch(Exception e) {
            SynthModLoader.LOGGER.error("Error loading test module!", e);
        }
    }
}

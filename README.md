# SynthModLoader - Chicory WASM Integration

This project demonstrates how to integrate Chicory WASM runtime into a NeoForge Minecraft mod, allowing you to load and execute WebAssembly modules within the game.

## Prerequisites

- Java 21 or higher
- Gradle (included via wrapper)

## Project Structure

- `src/main/java/com/synthmodloader/modloader/integration/ChicoryIntegration.java` - Main WASM integration code
- `src/main/resources/test.wasm` - Sample WASM module containing an iterative factorial function (`iterFact`)
- `build.gradle` - Build configuration with Chicory dependencies

## Building the Mod

To build the mod, run:

```bash
./gradlew build
```

This will:
- Download Chicory runtime dependencies from Maven Central
- Compile the Java source code
- Package the mod with the WASM file included

## Running the Mod

To run the mod in a Minecraft client:

```bash
./gradlew runClient
```

This will:
- Start a Minecraft client with the mod loaded
- Initialize the Chicory WASM runtime
- Load and execute the `test.wasm` module

## Verifying the Results

After running the mod, check the logs to verify the WASM integration is working:

```bash
# Check the latest log file for ChicoryIntegration output
grep -A 10 -B 5 "ChicoryIntegration\|iterFact\|OUTPUT" runs/client/logs/latest.log
```

### Expected Output

You should see the following log entries indicating successful WASM execution:

```
[modloading-worker-0/INFO] [com.synthmodloader.modloader.SynthModLoader/]: ChicoryIntegration.init() called
[modloading-worker-0/INFO] [com.synthmodloader.modloader.SynthModLoader/]: Attempting to load test.wasm resource
[modloading-worker-0/INFO] [com.synthmodloader.modloader.SynthModLoader/]: test.wasm resource found, parsing...
[modloading-worker-0/INFO] [com.synthmodloader.modloader.SynthModLoader/]: WASM module parsed successfully, creating instance...
[modloading-worker-0/INFO] [com.synthmodloader.modloader.SynthModLoader/]: Instance created, trying to find any function export...
[modloading-worker-0/INFO] [com.synthmodloader.modloader.SynthModLoader/]: Trying to find export: iterFact
[modloading-worker-0/INFO] [com.synthmodloader.modloader.SynthModLoader/]: Found function: iterFact, calling with parameter 1...
[modloading-worker-0/INFO] [com.synthmodloader.modloader.SynthModLoader/]: OUTPUT from iterFact: 1
```

The key success indicator is: **`OUTPUT from iterFact: 1`**

This shows that:
1. ✅ The WASM file was loaded successfully
2. ✅ Chicory parsed the WASM module correctly
3. ✅ The `iterFact` function was found and executed
4. ✅ The function returned `1` (factorial of 1)

## How It Works

1. **Mod Initialization**: When the mod loads, `ChicoryIntegration.init()` is called
2. **Resource Loading**: The `test.wasm` file is loaded from the mod's resources
3. **WASM Parsing**: Chicory parses the WASM binary into a module
4. **Instance Creation**: A WASM instance is created from the module
5. **Function Execution**: The `iterFact` function is found and called with parameter `1`
6. **Result Logging**: The result (`1`) is logged to the console

## Dependencies

The project uses the following Chicory dependency (managed via Maven):

- `com.dylibso.chicory:runtime:1.5.1` - Core WASM runtime (includes WASM parsing and compilation as transitive dependencies)

## Troubleshooting

If you don't see the expected output:

1. **Check build success**: Ensure `./gradlew build` completed without errors
2. **Verify WASM file**: Confirm `src/main/resources/test.wasm` exists
3. **Check logs**: Look for any error messages in `runs/client/logs/latest.log`
4. **Verify dependencies**: Ensure Chicory dependencies are properly resolved

## Customization

To use your own WASM module:

1. Replace `src/main/resources/test.wasm` with your WASM file
2. Update the function names in `ChicoryIntegration.java` to match your exports
3. Modify the function parameters and return value handling as needed

## License

This project is provided as an example of Chicory WASM integration with NeoForge mods.
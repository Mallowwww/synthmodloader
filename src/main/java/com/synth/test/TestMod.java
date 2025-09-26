package com.synth.test;

import com.mojang.logging.LogUtils;
import com.synth.modloader.SynthMod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import org.slf4j.Logger;

@SynthMod(TestMod.MODID)
public class TestMod {
    public static final String MODID = "testmod";
    public static final Logger LOGGER = LogUtils.getLogger();
    public TestMod(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Hello from TestMod");
    }
}

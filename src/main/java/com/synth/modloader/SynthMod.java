package com.synth.modloader;

import com.synth.modloader.api.SynthInstance;
import net.neoforged.api.distmarker.Dist;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Supplier;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SynthMod {
    /**
     * The unique mod identifier for this mod.
     * <b>Required to be lowercased in the english locale for compatibility. Will be truncated to 64 characters long.</b>
     * <p>
     * This will be used to identify your mod for third parties (other mods), it will be used to identify your mod for registries such as block and item registries.
     * By default, you will have a resource domain that matches the modid. All these uses require that constraints are imposed on the format of the modid.
     */
    String value();
    /**
     * {@return the side to load this mod entrypoint on}
     */
    Dist[] dist() default { Dist.CLIENT, Dist.DEDICATED_SERVER };
}

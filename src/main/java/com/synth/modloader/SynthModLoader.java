package com.synth.modloader;

import com.mojang.logging.LogUtils;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingIssue;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.AutomaticEventSubscriber;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.JarVersionLookupHandler;
import net.neoforged.neoforgespi.IIssueReporting;
import net.neoforged.neoforgespi.language.IModInfo;
import net.neoforged.neoforgespi.language.IModLanguageLoader;
import net.neoforged.neoforgespi.language.ModFileScanData;
import net.neoforged.neoforgespi.locating.IModFile;
import org.slf4j.Logger;

import java.lang.annotation.ElementType;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class SynthModLoader implements IModLanguageLoader {
    public static final Logger LOGGER = LogUtils.getLogger();
    @Override
    public String name() {
        return "synthmodloader";
    }

    @Override
    public String version() {
        return JarVersionLookupHandler.getVersion(this.getClass()).orElse("1.0.0");
    }

    @Override
    public ModContainer loadMod(IModInfo info, ModFileScanData modFileScanResults, ModuleLayer layer) {
        System.out.println("WOMP WOMP");
        final var modClasses = modFileScanResults.getAnnotatedBy(SynthMod.class, ElementType.TYPE)
                .filter(data -> data.annotationData().get("value").equals(info.getModId()))
                .filter(ad -> AutomaticEventSubscriber.getSides(ad.annotationData().get("dist")).contains(FMLLoader.getDist()))
                .sorted(Comparator.comparingInt(ad -> -AutomaticEventSubscriber.getSides(ad.annotationData().get("dist")).size()))
                .map(ad -> ad.clazz().getClassName())
                .toList();

        LOGGER.info("Loaded mod "+info.getDisplayName());
        return new SynthModContainer(info, modClasses, modFileScanResults, layer);
    }
    @Override
    public void validate(IModFile file, Collection<ModContainer> loadedContainers, IIssueReporting reporter) {
        final Set<String> modIds = new HashSet<>();
        for (IModInfo modInfo : file.getModInfos()) {
            if (modInfo.getLoader() == this) {
                modIds.add(modInfo.getModId());
            }
        }
        LOGGER.debug("Validating {}",file.getFileName());
        file.getScanResult().getAnnotatedBy(SynthMod.class, ElementType.TYPE)
                .filter(data -> !modIds.contains((String) data.annotationData().get("value")))
                .forEach(data -> {
                    var modId = data.annotationData().get("value");
                    var entrypointClass = data.clazz().getClassName();
                    var issue = ModLoadingIssue.error("fml.modloadingissue.javafml.dangling_entrypoint", modId, entrypointClass, file.getFilePath()).withAffectedModFile(file);
                    reporter.addIssue(issue);
                });
    }
}

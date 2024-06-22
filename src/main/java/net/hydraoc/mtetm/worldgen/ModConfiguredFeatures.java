package net.hydraoc.mtetm.worldgen;

import com.google.common.base.Suppliers;
import net.hydraoc.mtetm.MoreTetraMaterials;
import net.hydraoc.mtetm.block.ModBlocks;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.Supplier;

public class ModConfiguredFeatures {
    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES =
            DeferredRegister.create(Registry.CONFIGURED_FEATURE_REGISTRY, MoreTetraMaterials.MOD_ID);

    public static final Supplier<List<OreConfiguration.TargetBlockState>> MITHRIL_ORES = Suppliers.memoize(() -> List.of(
            OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, ModBlocks.MITHRIL_ORE.get().defaultBlockState()),
            OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, ModBlocks.DEEPSLATE_MITHRIL_ORE.get().defaultBlockState())));

    public static final Supplier<List<OreConfiguration.TargetBlockState>> ADAMANTIUM_ORES = Suppliers.memoize(() -> List.of(
            OreConfiguration.target(OreFeatures.NETHER_ORE_REPLACEABLES, ModBlocks.ADAMANTIUM_ORE.get().defaultBlockState())));
    public static final Supplier<List<OreConfiguration.TargetBlockState>> SCARLITE_ORES = Suppliers.memoize(() -> List.of(
            OreConfiguration.target(OreFeatures.NETHER_ORE_REPLACEABLES, ModBlocks.BLOOD_EMERALD_ORE.get().defaultBlockState())));

    public static final Supplier<List<OreConfiguration.TargetBlockState>> PANDORIUM_ORES = Suppliers.memoize(() -> List.of(
            OreConfiguration.target(new BlockMatchTest(Blocks.END_STONE), ModBlocks.PANDORIUM_ORE.get().defaultBlockState())));



    public static final RegistryObject<ConfiguredFeature<?, ?>> MITHRIL_ORE = CONFIGURED_FEATURES.register("mithril_ore",
            () -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(MITHRIL_ORES.get(),7)));

    public static final RegistryObject<ConfiguredFeature<?, ?>> ADAMANTIUM_ORE = CONFIGURED_FEATURES.register("adamantium_ore",
            () -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(ADAMANTIUM_ORES.get(), 9)));
    public static final RegistryObject<ConfiguredFeature<?, ?>> SCARLITE_ORE = CONFIGURED_FEATURES.register("scarlite_ore",
            () -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(SCARLITE_ORES.get(), 9)));

    public static final RegistryObject<ConfiguredFeature<?, ?>> PANDORIUM_ORE = CONFIGURED_FEATURES.register("pandorium_ore",
            () -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(PANDORIUM_ORES.get(), 9)));


    public static void register(IEventBus eventBus) {
        CONFIGURED_FEATURES.register(eventBus);
    }
}
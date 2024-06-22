package net.hydraoc.mtetm.block;

import net.hydraoc.mtetm.MoreTetraMaterials;
import net.hydraoc.mtetm.block.custom.HellforgeBlock;
import net.hydraoc.mtetm.item.ModCreativeModeTab;
import net.hydraoc.mtetm.item.ModItems;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public class ModBlocks {
    private static ToIntFunction<BlockState> litBlockEmission(int p_50760_) {
        return (p_50763_) -> {
            return p_50763_.getValue(BlockStateProperties.LIT) ? p_50760_ : 0;
        };
    }
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, MoreTetraMaterials.MOD_ID);

    public static final RegistryObject<Block> MITHRIL_BLOCK = registerBlock("mithril_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(6f).requiresCorrectToolForDrops()), ModCreativeModeTab.MTETM_ITEM_TAB);
    public static final RegistryObject<Block> RAW_MITHRIL_BLOCK = registerBlock("raw_mithril_block",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.STONE)
                    .strength(6f).requiresCorrectToolForDrops(), UniformInt.of(3, 6)), ModCreativeModeTab.MTETM_ITEM_TAB);
    public static final RegistryObject<Block> MITHRIL_ORE = registerBlock("mithril_ore",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)
                    .strength(6f).requiresCorrectToolForDrops()), ModCreativeModeTab.MTETM_ITEM_TAB);
    public static final RegistryObject<Block> DEEPSLATE_MITHRIL_ORE = registerBlock("deepslate_mithril_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.STONE).sound(SoundType.DEEPSLATE)
                    .strength(6f).requiresCorrectToolForDrops(), UniformInt.of(3, 6)), ModCreativeModeTab.MTETM_ITEM_TAB);


    public static final RegistryObject<Block> ADAMANTIUM_BLOCK = registerBlock("adamantium_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.NETHERITE_BLOCK)
                    .strength(8,1200f).requiresCorrectToolForDrops()), ModCreativeModeTab.MTETM_ITEM_TAB);
    public static final RegistryObject<Block> RAW_ADAMANTIUM_BLOCK = registerBlock("raw_adamantium_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)
                    .strength(8,1200f).requiresCorrectToolForDrops()), ModCreativeModeTab.MTETM_ITEM_TAB);
    public static final RegistryObject<Block> ADAMANTIUM_ORE = registerBlock("adamantium_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.STONE).sound(SoundType.GILDED_BLACKSTONE)
                    .strength(8,1200f).requiresCorrectToolForDrops(), UniformInt.of(3, 6)), ModCreativeModeTab.MTETM_ITEM_TAB);

    public static final RegistryObject<Block> ENDERFUSED_ADAMNTIUM_BLOCK = registerBlock("enderfused_adamantium_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.NETHERITE_BLOCK)
                    .strength(8,1200f).requiresCorrectToolForDrops()), ModCreativeModeTab.MTETM_ITEM_TAB);

    //public static final RegistryObject<Block> REINFORCED_ADAMANTIUM_BLOCK = registerBlock("reinforced_adamantium_block",
            //() -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.NETHERITE_BLOCK)
                    //.strength(8,1200f).requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> PANDORIUM_BLOCK = registerBlock("pandorium_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.NETHERITE_BLOCK)
                    .strength(8,1200f).requiresCorrectToolForDrops()), ModCreativeModeTab.MTETM_ITEM_TAB);
    public static final RegistryObject<Block> RAW_PANDORIUM_BLOCK = registerBlock("raw_pandorium_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)
                    .strength(8,1200f).requiresCorrectToolForDrops()), ModCreativeModeTab.MTETM_ITEM_TAB);
    public static final RegistryObject<Block> PANDORIUM_ORE = registerBlock("pandorium_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.STONE)
                    .strength(8,1200f).requiresCorrectToolForDrops(), UniformInt.of(3, 6)), ModCreativeModeTab.MTETM_ITEM_TAB);

    public static final RegistryObject<Block> BLOOD_EMERALD_BLOCK = registerBlock("blood_emerald_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(8,1200f).requiresCorrectToolForDrops()), ModCreativeModeTab.MTETM_ITEM_TAB);
    public static final RegistryObject<Block> BLOOD_EMERALD_ORE = registerBlock("blood_emerald_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.GILDED_BLACKSTONE)
                    .strength(8,1200f).requiresCorrectToolForDrops(), UniformInt.of(3, 6)), ModCreativeModeTab.MTETM_ITEM_TAB);

    public static final RegistryObject<Block> HELLFORGE = registerBlock("hellforge",
            () -> new HellforgeBlock(BlockBehaviour.Properties.copy(Blocks.NETHERITE_BLOCK).noOcclusion().lightLevel(litBlockEmission(13))), ModCreativeModeTab.MTETM_ITEM_TAB);

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, CreativeModeTab tab) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn, tab);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block, CreativeModeTab tab) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(tab)));
    }
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
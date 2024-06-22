package net.hydraoc.mtetm.item;

import net.hydraoc.mtetm.MoreTetraMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MoreTetraMaterials.MOD_ID);

    public static final RegistryObject<Item> MITHRIL_INGOT = ITEMS.register("mithril_ingot",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.MTETM_ITEM_TAB)));

    public static final RegistryObject<Item> RAW_MITHRIL = ITEMS.register("raw_mithril",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.MTETM_ITEM_TAB)));

    //public static final RegistryObject<Item> MITHRIL_NUGGET = ITEMS.register("mithril_nugget",
            //() -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> MITHRIL_DUST = ITEMS.register("mithril_dust",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.MTETM_ITEM_TAB)));


    public static final RegistryObject<Item> ADAMANTIUM_INGOT = ITEMS.register("adamantium_ingot",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.MTETM_ITEM_TAB)));

    public static final RegistryObject<Item> RAW_ADAMANTIUM = ITEMS.register("raw_adamantium",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.MTETM_ITEM_TAB)));

    //public static final RegistryObject<Item> ADAMANTIUM_NUGGET = ITEMS.register("adamantium_nugget",
            //() -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> ADAMANTIUM_DUST = ITEMS.register("adamantium_dust",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.MTETM_ITEM_TAB)));


    public static final RegistryObject<Item> ENDERFUSED_ADAMANTIUM_INGOT = ITEMS.register("enderfused_adamantium_ingot",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.MTETM_ITEM_TAB)));

    //public static final RegistryObject<Item> ENDERFUSED_ADAMANTIUM_NUGGET = ITEMS.register("enderfused_adamantium_nugget",
            //() -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> ENDERFUSED_ADAMANTIUM_DUST = ITEMS.register("enderfused_adamantium_dust",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.MTETM_ITEM_TAB)));

    //public static final RegistryObject<Item> REINFORCED_ADAMANTIUM_INGOT = ITEMS.register("reinforced_adamantium_ingot",
            //() -> new Item(new Item.Properties()));


    public static final RegistryObject<Item> PANDORIUM_INGOT = ITEMS.register("pandorium_ingot",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.MTETM_ITEM_TAB)));

    public static final RegistryObject<Item> RAW_PANDORIUM = ITEMS.register("raw_pandorium",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.MTETM_ITEM_TAB)));

    //public static final RegistryObject<Item> PANDORIUM_NUGGET = ITEMS.register("pandorium_nugget",
            //() -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> PANDORIUM_DUST = ITEMS.register("pandorium_dust",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.MTETM_ITEM_TAB)));


    public static final RegistryObject<Item> SCARLITE = ITEMS.register("blood_emerald",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.MTETM_ITEM_TAB)));

    public static final RegistryObject<Item> SCARLITE_DUST = ITEMS.register("blood_emerald_dust",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.MTETM_ITEM_TAB)));


    public static final RegistryObject<Item> ENDER_PEARL_DUST = ITEMS.register("ender_pearl_dust",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.MTETM_ITEM_TAB)));


    //public static final RegistryObject<Item> MITHRIL_PICKAXE = ITEMS.register("mithril_pickaxe", () -> new PickaxeItem(ModToolTiers.MITHRIL, 1, 1, new Item.Properties()));

    //public static final RegistryObject<Item> ADAMANTIUM_PICKAXE = ITEMS.register("adamantium_pickaxe", () -> new PickaxeItem(ModToolTiers.ADAMANTIUM, 1, 1, new Item.Properties()));

    //public static final RegistryObject<Item> THUNDER_CRYSTAL = ITEMS.register("thunder_crystal",() -> new Item(new Item.Properties()));



    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
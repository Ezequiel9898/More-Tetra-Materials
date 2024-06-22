package net.hydraoc.mtetm.item;

import net.hydraoc.mtetm.MoreTetraMaterials;
import net.hydraoc.mtetm.block.ModBlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTab {
    public static final CreativeModeTab MTETM_ITEM_TAB = new CreativeModeTab("mtetm_item_tab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.MITHRIL_INGOT.get());
        }
    };
}
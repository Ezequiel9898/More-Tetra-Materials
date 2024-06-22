package net.hydraoc.mtetm;

import com.mojang.logging.LogUtils;
import net.hydraoc.mtetm.block.entity.ModBlockEntities;
import net.hydraoc.mtetm.item.ModItems;
import net.hydraoc.mtetm.block.ModBlocks;
import net.hydraoc.mtetm.item.ModToolTiers;
import net.hydraoc.mtetm.menus.HellforgeScreen;
import net.hydraoc.mtetm.menus.ModMenuTypes;
import net.hydraoc.mtetm.recipe.ModRecipes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(MoreTetraMaterials.MOD_ID)
public class MoreTetraMaterials {
    public static final String MOD_ID = "mtetm";
    private static final Logger LOGGER = LogUtils.getLogger();

    // Very Important Comment
    public MoreTetraMaterials() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModToolTiers.init();
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);

        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);

        ModRecipes.register(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            MenuScreens.register(ModMenuTypes.HELLFORGE_MENU.get(), HellforgeScreen::new);
        }
    }
}

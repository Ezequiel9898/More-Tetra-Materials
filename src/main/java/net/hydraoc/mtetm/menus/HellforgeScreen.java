package net.hydraoc.mtetm.menus;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.hydraoc.mtetm.MoreTetraMaterials;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HellforgeScreen extends AbstractContainerScreen<HellforgeMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(MoreTetraMaterials.MOD_ID,"textures/gui/hellforge_gui.png");

    public HellforgeScreen(HellforgeMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
    }

    int imageWidth = 171;
    int imageHeight = 173;

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void renderBg(PoseStack poseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - 176) / 2;
        int y = (height - 166) / 2;

        this.blit(poseStack, x, y, 0, 0, imageWidth, imageHeight);
        renderFuelBar(poseStack,x+146,y+50);
        renderProgressArrow(poseStack,x+54,y+29);
        renderColorBars(poseStack,x,y);
    }

    //Renders the fuel bar
    private void renderFuelBar(PoseStack pPoseStack, int x, int y) {
        if(menu.isLit()) {
            int fuel = menu.getLitProgress();
            blit(pPoseStack, x, y-fuel, 172, 50-fuel, 13, fuel);
        }
    }

    private void renderProgressArrow(PoseStack pPoseStack, int x, int y) {
        if(menu.isCrafting()) {
            int progress = (this.menu).getBurnProgress();
            blit(pPoseStack, x, y, 0, 174, progress, 16);
        }
    }

    //Renders the color bars for the menu (Like 7 pixels, but needed its own handler)
    private void renderColorBars(PoseStack poseStack, int x , int y){
        int fuel = menu.getLitProgress();
        if(menu.isLit()) {
            blit(poseStack, x + 148, y + 7, 171, 7, 1, 3);
            blit(poseStack, x + 135, y + 37, 171, 11, 5, 1);
            if(menu.getBurnProgress()>0 || (menu.getSlot(2).hasItem() && menu.getSlot(0).hasItem())) {
                blit(poseStack, x + 114, y + 34, 177, 5, 17, 7);
            }else{
                blit(poseStack, x + 114, y + 34, 114, 34, 17, 7);
            }
        }
    }

    @Override
    public void render(PoseStack pPoseStack, int mouseX, int mouseY, float delta) {
        renderBackground(pPoseStack);
        super.render(pPoseStack, mouseX, mouseY, delta);
        renderTooltip(pPoseStack, mouseX, mouseY);
    }

}

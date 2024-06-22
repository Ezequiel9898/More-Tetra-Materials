package net.hydraoc.mtetm.recipe;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.hydraoc.mtetm.MoreTetraMaterials;
import net.hydraoc.mtetm.block.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class HellSmeltingCategory implements IRecipeCategory<HellSmeltingRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(MoreTetraMaterials.MOD_ID, "hellforge");
    public static final ResourceLocation TEXTURE = new ResourceLocation(MoreTetraMaterials.MOD_ID,
            "textures/gui/hellforge_jei.png");

    public static final RecipeType<HellSmeltingRecipe> HELL_SMELTING_TYPE =
            new RecipeType<>(UID, HellSmeltingRecipe.class);

    private final IDrawable background;
    private final IDrawableAnimated arrow;
    private final IDrawable icon;

    public HellSmeltingCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 25, 0, 146, 75);;
        this.arrow = helper.drawableBuilder(TEXTURE, 0, 174, 24, 17)
                .buildAnimated(400, IDrawableAnimated.StartDirection.LEFT, false);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.HELLFORGE.get()));
    }

    @Override
    public RecipeType<HellSmeltingRecipe> getRecipeType() {
        return HELL_SMELTING_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.mtetm.hell_smelting_jei");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, HellSmeltingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 31-25, 30).addIngredients(recipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 91-25, 30).addItemStack(recipe.getResultItem());
    }
}

package net.hydraoc.mtetm.menus;


import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.hydraoc.mtetm.MoreTetraMaterials;
import net.hydraoc.mtetm.recipe.HellSmeltingCategory;
import net.hydraoc.mtetm.recipe.HellSmeltingRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

@JeiPlugin
public class JEIMenu implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(MoreTetraMaterials.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new HellSmeltingCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        List<HellSmeltingRecipe> hellSmeltingRecipes = recipeManager.getAllRecipesFor(HellSmeltingRecipe.Type.INSTANCE);
        registration.addRecipes(HellSmeltingCategory.HELL_SMELTING_TYPE, hellSmeltingRecipes);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(HellforgeScreen.class, 54, 29, 24, 17, HellSmeltingCategory.HELL_SMELTING_TYPE);
    }
}

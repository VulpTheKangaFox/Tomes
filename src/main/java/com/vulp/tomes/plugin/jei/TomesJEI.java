package com.vulp.tomes.plugin.jei;

import com.google.gson.JsonSyntaxException;
import com.vulp.tomes.Tomes;
import com.vulp.tomes.TomesRegistry;
import com.vulp.tomes.items.crafting.GobletOfHeartsRecipe;
import com.vulp.tomes.plugin.jei.categories.GobletOfHeartsCategory;
import com.vulp.tomes.init.BlockInit;
import com.vulp.tomes.init.RecipeInit;
import com.vulp.tomes.plugin.jei.wrappers.GobletOfHeartsWrapper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@JeiPlugin
// @Mod.EventBusSubscriber(modid= Tomes.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class TomesJEI implements IModPlugin {

    private static final ResourceLocation PLUGIN_UID = TomesRegistry.location("jei_plugin");
    public static final ResourceLocation RECIPE_GUI_TOMES = TomesRegistry.location("textures/gui/jei/jei_backgrounds.png");
    // public static Screen GUI;

    private GobletOfHeartsCategory gobletCraftingCategory;

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }

    /*@Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        GUI = (Screen) jeiRuntime.getRecipesGui();
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        registration.register(TomesIngredientTypes.ENTITY_STACK, ForgeRegistries.ENTITIES.getEntries().stream().filter(e -> e.getValue().create(Minecraft.getInstance().world) instanceof LivingEntity).map(e -> new EntityStackHandler(e.getValue(), 1)).collect(Collectors.toCollection(ArrayList::new)), new EntityIngredientHelper(), new EntityIngredientRenderer());
        // registration.register(TomesIngredientTypes.EFFECT, ForgeRegistries.POTIONS.getEntries().stream().map(Map.Entry::getValue).collect(Collectors.toCollection(ArrayList::new)), new EntityIngredientHelper(), new EntityIngredientRenderer());
    }*/

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        this.gobletCraftingCategory = new GobletOfHeartsCategory(registration.getJeiHelpers().getGuiHelper());
        registration.addRecipeCategories(this.gobletCraftingCategory);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        Minecraft mc = Minecraft.getInstance();
        Set<GobletOfHeartsWrapper> finalList = new java.util.HashSet<>(Collections.emptySet());
        for (GobletOfHeartsRecipe recipe : Minecraft.getInstance().world.getRecipeManager().getRecipesForType(RecipeInit.goblet_crafting)) {
            String keyStart = recipe.getId().getNamespace() + "." + recipe.getId().getPath().replace("/", ".");
            String s = new TranslationTextComponent(keyStart + ".title_hex").copyRaw().getString();
            Color titleColor = Color.fromHex(s);
            int color = java.awt.Color.WHITE.getRGB();
            try {
                if (!s.isEmpty()) {
                    if (titleColor == null) {
                        throw new JsonSyntaxException("Unknown hex code in '" + mc.getLanguageManager().getCurrentLanguage().getCode() + ".json' at '" + s + "'. Should follow standard color hex format.");
                    } else {
                        color = titleColor.getColor();
                    }
                }
            } catch (JsonSyntaxException e) {
                Tomes.LOGGER.error(e.getLocalizedMessage());
            }
            finalList.add(new GobletOfHeartsWrapper(recipe, color, new TranslationTextComponent(keyStart + ".title"), new TranslationTextComponent(keyStart + ".desc")));
        }
        registration.addRecipes(finalList, this.gobletCraftingCategory.getUid());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(BlockInit.goblet_of_hearts), this.gobletCraftingCategory.getUid());
    }

    /*@SubscribeEvent
    public static void onMouseDragEvent(GuiScreenEvent.MouseDragEvent event) {
        if (event.getMouseButton() == 0 && event.getGui() instanceof IRecipesGui) {
            GobletOfHeartsCategory.MOUSE_HELD = true;
        }
    }

    @SubscribeEvent
    public static void onMouseReleaseEvent(GuiScreenEvent.MouseReleasedEvent event) {
        if (event.getButton() == 0 && event.getGui() instanceof IRecipesGui) {
            GobletOfHeartsCategory.MOUSE_HELD = false;
        }
    }*/



}

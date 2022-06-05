package com.vulp.tomes.init;

import com.vulp.tomes.TomesRegistry;
import com.vulp.tomes.items.crafting.GobletOfHeartsRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.SmithingRecipe;
import net.minecraft.util.registry.Registry;

public class RecipeInit {

    public static final IRecipeType<GobletOfHeartsRecipe> goblet_crafting = registerRecipeType("goblet_crafting");

    public static IRecipeSerializer<GobletOfHeartsRecipe> goblet_of_hearts_serializer = new GobletOfHeartsRecipe.GobletOfHeartsRecipeSerializer();

    static <T extends IRecipe<?>> IRecipeType<T> registerRecipeType(final String key) {
        return Registry.register(Registry.RECIPE_TYPE, TomesRegistry.location(key), new IRecipeType<T>() {
            public String toString() {
                return key;
            }
        });
    }

}

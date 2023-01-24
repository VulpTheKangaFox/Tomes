package com.vulp.tomes.plugin.jei.wrappers;

import com.vulp.tomes.items.crafting.GobletOfHeartsRecipe;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;

import javax.annotation.Nullable;
import java.util.List;

public class GobletOfHeartsWrapper {

    private final GobletOfHeartsRecipe recipe;
    private final int color;
    @Nullable private final ITextComponent title;
    @Nullable private final ITextComponent description;

    public GobletOfHeartsWrapper(GobletOfHeartsRecipe recipe, @Nullable int color, @Nullable ITextComponent title, @Nullable ITextComponent description) {
        this.recipe = recipe;
        this.color = color;
        this.title = title;
        this.description = description;
    }

    public GobletOfHeartsRecipe getRecipe() {
        return this.recipe;
    }

    public int getColor() {
        return this.color;
    }

    @Nullable
    public ITextComponent getTitle() {
        return this.title;
    }

    @Nullable
    public ITextComponent getDescription() {
        return this.description;
    }

}

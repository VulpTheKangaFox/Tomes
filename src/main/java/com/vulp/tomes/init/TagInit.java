package com.vulp.tomes.init;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class TagInit {

    public static final Tags.IOptionalNamedTag<Block> GRASS = createOptionalBlockTag("forge", "grass", new HashSet<>(Arrays.asList(() -> Blocks.GRASS_BLOCK, () -> Blocks.MYCELIUM, () -> Blocks.PODZOL)));
    public static final Tags.IOptionalNamedTag<Item> FEATHER = createOptionalItemTag("forge", "feathers", new HashSet<>(Collections.singletonList(() -> Items.FEATHER)));

    public static void init() {
    }

    private static Tags.IOptionalNamedTag<Item> createOptionalItemTag(String modID, String name, @javax.annotation.Nullable java.util.Set<java.util.function.Supplier<Item>> defaults) {
        return ItemTags.createOptional(new ResourceLocation(modID, name), defaults);
    }

    private static Tags.IOptionalNamedTag<Block> createOptionalBlockTag(String modID, String name, @javax.annotation.Nullable java.util.Set<java.util.function.Supplier<Block>> defaults) {
        return BlockTags.createOptional(new ResourceLocation(modID, name), defaults);
    }

}

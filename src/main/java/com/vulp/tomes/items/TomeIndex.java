package com.vulp.tomes.items;

import com.vulp.tomes.init.ItemInit;
import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;

import javax.annotation.Nullable;

public enum TomeIndex implements IStringSerializable {

    ARCHAIC("archaic", ItemInit.archaic_tome),
    CURSED("cursed", ItemInit.cursed_tome),
    LIVING("living", ItemInit.living_tome);

    private final String string;
    private final Item item;

    TomeIndex(String string, Item item) {
        this.string = string;
        this.item = item;
    }

    @Override
    public String getString() {
        return this.string;
    }

    @Nullable
    public static TomeIndex getIndexByItem(TomeItem item) {
        for (TomeIndex tome : TomeIndex.values()) {
            if (item == tome.item) {
                return tome;
            }
        }
        return null;
    }

    public Item getItem() {
        return item;
    }
}

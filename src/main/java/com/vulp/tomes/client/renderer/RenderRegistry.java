package com.vulp.tomes.client.renderer;

import com.vulp.tomes.Tomes;
import com.vulp.tomes.init.ItemInit;
import com.vulp.tomes.items.TomeIndex;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class RenderRegistry {

    public static void renderSetup() {

        for (TomeIndex tome : TomeIndex.values()) {
            ItemModelsProperties.registerProperty(tome.getItem(), new ResourceLocation(Tomes.MODID, "cooldown"), (itemStack, world, livingEntity) -> {
                CompoundNBT nbt = itemStack.getOrCreateTag();
                if (nbt.contains("pixels")) {
                    int pixels = nbt.getInt("pixels");
                    if (pixels > -1) {
                        return pixels;
                    }
                }
                return 0;
            });
        }

    }

}

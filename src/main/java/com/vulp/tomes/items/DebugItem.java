package com.vulp.tomes.items;

import com.vulp.tomes.Tomes;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffers;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class DebugItem extends HiddenDescriptorItem {

    public DebugItem(Properties properties) {
        super(properties);
    }

    public void debugWitchNBT(World world, Entity entity, ItemStack stack) {
        if (world.isRemote) {
            Tomes.LOGGER.debug("CLIENT: " + entity.getPersistentData().get("Offers"));
        } else {
            Tomes.LOGGER.debug("SERVER: " + new MerchantOffers(entity.getPersistentData().getCompound("Offers")).size());
        }
    }

}

package com.vulp.tomes.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.vulp.tomes.inventory.container.WitchMerchantContainer;
import net.minecraft.client.gui.screen.inventory.MerchantScreen;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.MerchantContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

public class WitchMerchantScreen extends MerchantScreen {

    public WitchMerchantScreen(WitchMerchantContainer container, PlayerInventory playerInventory, ITextComponent displayName) {
        super(container, playerInventory, displayName);
    }

}

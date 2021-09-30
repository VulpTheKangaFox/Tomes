package com.vulp.tomes.inventory;

import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.MerchantInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundEvents;

import javax.annotation.Nullable;

public class WitchMerchantInventory extends MerchantInventory {

    private final WitchEntity witch;
    private int offerNumber;

    public WitchMerchantInventory(WitchEntity witch) {
        super(null);
        this.witch = witch;
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        CompoundNBT nbt = this.witch.getPersistentData();
        if (nbt.hasUniqueId("Customer")) {
            return this.witch.world.getPlayerByUuid(nbt.getUniqueId("Customer")) == player;
        }
        return false;
    }

    public void resetRecipeAndSlots() {
        this.offerNumber = -1;
        ItemStack itemstack;
        ItemStack itemstack1;
        if (this.slots.get(0).isEmpty()) {
            itemstack = this.slots.get(1);
            itemstack1 = ItemStack.EMPTY;
        } else {
            itemstack = this.slots.get(0);
            itemstack1 = this.slots.get(1);
        }

        if (itemstack.isEmpty()) {
            this.setInventorySlotContents(2, ItemStack.EMPTY);
        } else {

            MerchantOffers merchantoffers = new MerchantOffers();
            CompoundNBT nbtOffers = this.witch.getPersistentData().getCompound("Offers");
            if (!nbtOffers.isEmpty()) {
                merchantoffers = new MerchantOffers(nbtOffers);
            }
            if (!merchantoffers.isEmpty()) {
                MerchantOffer merchantoffer = merchantoffers.func_222197_a(itemstack, itemstack1, this.currentRecipeIndex);
                if (merchantoffer == null || merchantoffer.hasNoUsesLeft()) {
                    this.offerNumber = getOfferNumber(merchantoffers, merchantoffer);
                    merchantoffer = merchantoffers.func_222197_a(itemstack1, itemstack, this.currentRecipeIndex);
                }

                if (merchantoffer != null && !merchantoffer.hasNoUsesLeft()) {
                    this.offerNumber = getOfferNumber(merchantoffers, merchantoffer);
                    this.setInventorySlotContents(2, merchantoffer.getCopyOfSellingStack());
                } else {
                    this.setInventorySlotContents(2, ItemStack.EMPTY);
                }
            }
            this.verifySellingItem();
        }
    }

    protected static int getOfferNumber(MerchantOffers list, MerchantOffer offer) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == offer) {
                return i;
            }
        }
        return -1;
    }

    public int getOfferNumber() {
        return this.offerNumber;
    }

    public void verifySellingItem() {
        if (!witch.world.isRemote && witch.livingSoundTime > -witch.getTalkInterval() + 20) {
            witch.livingSoundTime = -witch.getTalkInterval();
            witch.playSound(SoundEvents.ENTITY_WITCH_AMBIENT, 1.0F, 1.0F);
        }

    }

}

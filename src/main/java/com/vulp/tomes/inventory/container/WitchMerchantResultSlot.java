package com.vulp.tomes.inventory.container;

import com.vulp.tomes.inventory.WitchMerchantInventory;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;

import java.util.Random;

public class WitchMerchantResultSlot extends Slot {


    private final WitchMerchantInventory witchInventory;
    private final PlayerEntity player;
    private int removeCount;
    private final WitchEntity witch;
    private static final Random rand = new Random();

    public WitchMerchantResultSlot(PlayerEntity player, WitchEntity witch, WitchMerchantInventory witchInventory, int slotIndex, int xPosition, int yPosition) {
        super(witchInventory, slotIndex, xPosition, yPosition);
        this.player = player;
        this.witch = witch;
        this.witchInventory = witchInventory;
    }

    /**
     * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
     */
    public boolean isItemValid(ItemStack stack) {
        return false;
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new stack.
     */
    public ItemStack decrStackSize(int amount) {
        if (this.getHasStack()) {
            this.removeCount += Math.min(amount, this.getStack().getCount());
        }

        return super.decrStackSize(amount);
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood. Typically increases an
     * internal count then calls onCrafting(item).
     */
    protected void onCrafting(ItemStack stack, int amount) {
        this.removeCount += amount;
        this.onCrafting(stack);
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood.
     */
    protected void onCrafting(ItemStack stack) {
        stack.onCrafting(this.player.world, this.player, this.removeCount);
        this.removeCount = 0;
    }

    public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
        this.onCrafting(stack);
        CompoundNBT nbt = this.witch.getPersistentData();
        MerchantOffers merchantoffers = new MerchantOffers(nbt.getCompound("Offers"));
        MerchantOffer offer = merchantoffers.get(this.witchInventory.getOfferNumber());
        if (offer != null) {
            ItemStack itemstack = this.witchInventory.getStackInSlot(0);
            ItemStack itemstack1 = this.witchInventory.getStackInSlot(1);
            if (offer.doTransaction(itemstack, itemstack1) || offer.doTransaction(itemstack1, itemstack)) {
                this.onTrade(offer);
                thePlayer.addStat(Stats.TRADED_WITH_VILLAGER);
                this.witchInventory.setInventorySlotContents(0, itemstack);
                this.witchInventory.setInventorySlotContents(1, itemstack1);
            }
        }
        nbt.put("Offers", merchantoffers.write());
        return stack;
    }

    public void onTrade(MerchantOffer offer) {
        offer.increaseUses();
        this.witch.livingSoundTime = -this.witch.getTalkInterval();
        int i = 3 + rand.nextInt(4);
        if (offer.getDoesRewardExp()) {
            this.witch.world.addEntity(new ExperienceOrbEntity(this.witch.world, this.witch.getPosX(), this.witch.getPosY() + 0.5D, this.witch.getPosZ(), i));
        }
    }

}

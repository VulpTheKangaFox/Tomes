package com.vulp.tomes.inventory.container;

import com.google.common.collect.Sets;
import com.vulp.tomes.client.renderer.entity.WitchTrades;
import com.vulp.tomes.inventory.WitchMerchantInventory;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.MerchantContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;
import java.util.Set;

public class WitchMerchantContainer extends MerchantContainer {

    private final WitchEntity witch;
    private final WitchMerchantInventory witchInventory;

    public WitchMerchantContainer(int id, PlayerInventory playerInventoryIn) {
        this(id, playerInventoryIn, new WitchEntity(EntityType.WITCH, playerInventoryIn.player.world));
    }

    public WitchMerchantContainer(int id, PlayerInventory playerInventoryIn, WitchEntity witch) {
        super(id, playerInventoryIn, null);
        this.witch = witch;
        this.witchInventory = new WitchMerchantInventory(witch);
        this.setSlot(0, new Slot(this.witchInventory, 0, 136, 37));
        this.setSlot(1, new Slot(this.witchInventory, 1, 162, 37));
        this.setSlot(2, new WitchMerchantResultSlot(playerInventoryIn.player, witch, this.witchInventory, 2, 220, 37));
    }

    protected Slot setSlot(int index, Slot slotIn) {
        slotIn.slotNumber = this.inventorySlots.get(index).slotNumber;
        this.inventorySlots.set(index, slotIn);
        this.inventoryItemStacks.add(ItemStack.EMPTY);
        return slotIn;
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        this.witchInventory.resetRecipeAndSlots();
        super.onCraftMatrixChanged(inventoryIn);
    }

    @Override
    public void setCurrentRecipeIndex(int currentRecipeIndex) {
        this.witchInventory.setCurrentRecipeIndex(currentRecipeIndex);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        CompoundNBT nbt = this.witch.getPersistentData();
        if (nbt.hasUniqueId("Customer")) {
            return this.witch.world.getPlayerByUuid(nbt.getUniqueId("Customer")) == playerIn;
        }
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getXp() {
        return 0;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getPendingExp() {
        return 0;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void setXp(int xp) {
    }

    @Override
    public void playMerchantYesSound() {
        if (!this.witch.world.isRemote) {
            Entity entity = this.witch;
            entity.world.playSound(entity.getPosX(), entity.getPosY(), entity.getPosZ(), SoundEvents.ENTITY_WITCH_AMBIENT, SoundCategory.NEUTRAL, 1.0F, 1.0F, false);
        }
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        PlayerInventory playerinventory = playerIn.inventory;
        if (!playerinventory.getItemStack().isEmpty()) {
            playerIn.dropItem(playerinventory.getItemStack(), false);
            playerinventory.setItemStack(ItemStack.EMPTY);
        }
        this.witch.getPersistentData().remove("Customer");
        if (!this.witch.world.isRemote) {
            if (!playerIn.isAlive() || playerIn instanceof ServerPlayerEntity && ((ServerPlayerEntity)playerIn).hasDisconnected()) {
                ItemStack itemstack = this.witchInventory.removeStackFromSlot(0);
                if (!itemstack.isEmpty()) {
                    playerIn.dropItem(itemstack, false);
                }

                itemstack = this.witchInventory.removeStackFromSlot(1);
                if (!itemstack.isEmpty()) {
                    playerIn.dropItem(itemstack, false);
                }
            } else {
                playerIn.inventory.placeItemBackInInventory(playerIn.world, this.witchInventory.removeStackFromSlot(0));
                playerIn.inventory.placeItemBackInInventory(playerIn.world, this.witchInventory.removeStackFromSlot(1));
            }
        }
    }

    public void func_217046_g(int p_217046_1_) {
        if (this.getOffers().size() > p_217046_1_) {
            ItemStack itemstack = this.witchInventory.getStackInSlot(0);
            if (!itemstack.isEmpty()) {
                if (!this.mergeItemStack(itemstack, 3, 39, true)) {
                    return;
                }

                this.witchInventory.setInventorySlotContents(0, itemstack);
            }

            ItemStack itemstack1 = this.witchInventory.getStackInSlot(1);
            if (!itemstack1.isEmpty()) {
                if (!this.mergeItemStack(itemstack1, 3, 39, true)) {
                    return;
                }

                this.witchInventory.setInventorySlotContents(1, itemstack1);
            }

            if (this.witchInventory.getStackInSlot(0).isEmpty() && this.witchInventory.getStackInSlot(1).isEmpty()) {
                ItemStack itemstack2 = this.getOffers().get(p_217046_1_).getDiscountedBuyingStackFirst();
                this.func_217053_c(0, itemstack2);
                ItemStack itemstack3 = this.getOffers().get(p_217046_1_).getBuyingStackSecond();
                this.func_217053_c(1, itemstack3);
            }
        }
    }

    private boolean areItemStacksEqual(ItemStack stack1, ItemStack stack2) {
        return stack1.getItem() == stack2.getItem() && ItemStack.areItemStackTagsEqual(stack1, stack2);
    }

    private void func_217053_c(int p_217053_1_, ItemStack p_217053_2_) {
        if (!p_217053_2_.isEmpty()) {
            for(int i = 3; i < 39; ++i) {
                ItemStack itemstack = this.inventorySlots.get(i).getStack();
                if (!itemstack.isEmpty() && this.areItemStacksEqual(p_217053_2_, itemstack)) {
                    ItemStack itemstack1 = this.witchInventory.getStackInSlot(p_217053_1_);
                    int j = itemstack1.isEmpty() ? 0 : itemstack1.getCount();
                    int k = Math.min(p_217053_2_.getMaxStackSize() - j, itemstack.getCount());
                    ItemStack itemstack2 = itemstack.copy();
                    int l = j + k;
                    itemstack.shrink(k);
                    itemstack2.setCount(l);
                    this.witchInventory.setInventorySlotContents(p_217053_1_, itemstack2);
                    if (l >= p_217053_2_.getMaxStackSize()) {
                        break;
                    }
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void setClientSideOffers(MerchantOffers offers) {
    }

    public MerchantOffers getOffers() {
        CompoundNBT nbt = this.witch.getPersistentData();
        if (!nbt.contains("Offers")) {
            nbt.put("Offers", new CompoundNBT());
            this.populateTradeData();
        }
        return new MerchantOffers(nbt.getCompound("Offers"));
    }

    // TODO: THIS!
    protected void populateTradeData() {
        Int2ObjectMap<VillagerTrades.ITrade[]> int2objectmap = WitchTrades.WITCH_TRADES;
        if (!int2objectmap.isEmpty()) {
            for (int i = 0; i < 3; i++) {
                VillagerTrades.ITrade[] avillagertrades$itrade = int2objectmap.get(i + 1);
                if (avillagertrades$itrade != null) {
                    MerchantOffers merchantoffers = this.getOffers();
                    this.setTrades(i, merchantoffers, avillagertrades$itrade);
                }
            }
            if (new Random().nextBoolean()) {
                VillagerTrades.ITrade[] avillagertrades$itrade = int2objectmap.get(4);
                if (avillagertrades$itrade != null) {
                    MerchantOffers merchantoffers = this.getOffers();
                    this.setTrades(3, merchantoffers, avillagertrades$itrade);
                }
            }
        }
    }

    protected void setTrades(int index, MerchantOffers givenMerchantOffers, VillagerTrades.ITrade[] newTrades) {
        Random rand = new Random();
        Set<Integer> set = Sets.newHashSet();
        set.add(rand.nextInt(newTrades.length));
        for(Integer integer : set) {
            VillagerTrades.ITrade villagertrades$itrade = newTrades[integer];
            MerchantOffer merchantoffer = villagertrades$itrade.getOffer(this.witch, rand);
            if (merchantoffer != null) {
                if (givenMerchantOffers.size() < index || givenMerchantOffers.size() == 0) {
                    givenMerchantOffers.add(merchantoffer);
                } else {
                    givenMerchantOffers.set(index, merchantoffer);
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public boolean func_217042_i() {
        return false;
    }

}

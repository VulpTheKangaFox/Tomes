package com.vulp.tomes.mixin;

import com.mojang.datafixers.util.Pair;
import com.vulp.tomes.enchantments.EnchantClueHolder;
import com.vulp.tomes.network.TomesPacketHandler;
import com.vulp.tomes.network.messages.ServerEnchantmentClueMessage;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.EnchantmentContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.network.PacketDistributor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Mixin(EnchantmentContainer.class)
public abstract class EnchantmentContainerMixin extends Container {

    @Shadow @Final public int[] enchantLevels;

    @Shadow @Final public IInventory tableInventory;

    @Shadow @Final public IWorldPosCallable worldPosCallable;

    @Shadow public abstract List<EnchantmentData> getEnchantmentList(ItemStack stack, int enchantSlot, int level);

    @Shadow public int[] enchantClue;

    @Shadow @Final public int[] worldClue;

    @Shadow @Final public Random rand;

    protected EnchantmentContainerMixin(@Nullable ContainerType<?> type, int id) {
        super(type, id);
    }

    @Inject(at = @At("TAIL"), method = "onCraftMatrixChanged", cancellable = true)
    public void onCraftMatrixChanged(IInventory inventoryIn, CallbackInfo ci) {
        if (inventoryIn == this.tableInventory) {
            ItemStack itemstack = inventoryIn.getStackInSlot(0);
            if (!itemstack.isEmpty() && itemstack.isEnchantable()) {
                this.worldPosCallable.consume((p_217002_2_, p_217002_3_) -> {
                    EnchantClueHolder holder = new EnchantClueHolder();
                    for(int j1 = 0; j1 < 3; ++j1) {
                        if (this.enchantLevels[j1] > 0) {
                            List<EnchantmentData> list = this.getEnchantmentList(this.tableInventory.getStackInSlot(0), j1, this.enchantLevels[j1]);
                            if (!list.isEmpty()) {
                                List<Pair<Enchantment, Integer>> cluedPairingList = new java.util.ArrayList<>(Collections.emptyList());

                                for (EnchantmentData enchantmentData : list) {
                                    cluedPairingList.add(new Pair<>(enchantmentData.enchantment, enchantmentData.enchantmentLevel));
                                }

                                holder.setData(j1, cluedPairingList);

                                EnchantmentData enchantmentdata = list.get(this.rand.nextInt(list.size()));
                                this.enchantClue[j1] = Registry.ENCHANTMENT.getId(enchantmentdata.enchantment);
                                this.worldClue[j1] = enchantmentdata.enchantmentLevel;
                            }
                        }
                    }
                    this.enchantClue = EnchantClueHolder.encodeClues(holder, this.enchantClue);
                    TomesPacketHandler.instance.send(PacketDistributor.ALL.noArg(), new ServerEnchantmentClueMessage(this.windowId, this.enchantClue));
                });
            }
        }
    }

}
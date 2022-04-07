package com.vulp.tomes.mixin;

import com.mojang.datafixers.util.Pair;
import com.vulp.tomes.enchantments.EnchantClueHolder;
import com.vulp.tomes.network.TomesPacketHandler;
import com.vulp.tomes.network.messages.ServerEnchantmentClueMessage;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.EnchantmentContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.List;

@Mixin(Container.class)
public abstract class ContainerMixin {

    // Causes incorrect info with Apotheosis because stuff is calculated different. Can't piggyback an invoker off of craftingMatrixChanged though because it doesn't trigger when method is already overridden. Or so I think.
    @Inject(at = @At("TAIL"), method = "detectAndSendChanges", cancellable = true)
    public void onCraftMatrixChanged(CallbackInfo ci) {
        if (getThis() instanceof EnchantmentContainer) {
            EnchantmentContainer container = ((EnchantmentContainer)(Object)this);
            ItemStack itemstack = container.tableInventory.getStackInSlot(0);
            if (!itemstack.isEmpty() && itemstack.isEnchantable()) {
                container.worldPosCallable.consume((p_217002_2_, p_217002_3_) -> {
                    EnchantClueHolder holder = new EnchantClueHolder();
                    for(int j1 = 0; j1 < 3; ++j1) {
                        if (container.enchantLevels[j1] > 0) {
                            List<EnchantmentData> list = container.getEnchantmentList(container.tableInventory.getStackInSlot(0), j1, container.enchantLevels[j1]);
                            if (!list.isEmpty()) {
                                List<Pair<Enchantment, Integer>> cluedPairingList = new java.util.ArrayList<>(Collections.emptyList());
                                for (EnchantmentData enchantmentData : list) {
                                    cluedPairingList.add(new Pair<>(enchantmentData.enchantment, enchantmentData.enchantmentLevel));
                                }
                                holder.setData(j1, cluedPairingList);
                            }
                        }
                    }
                    container.enchantClue = EnchantClueHolder.encodeClues(holder, container.enchantClue);
                    TomesPacketHandler.instance.send(PacketDistributor.ALL.noArg(), new ServerEnchantmentClueMessage(container.windowId, container.enchantClue));
                });
            }
        }
    }

    // Allows us to reference the current instance of the class the mixin is injecting.
    private Container getThis() {
        return ((Container)(Object)this);
    }

}
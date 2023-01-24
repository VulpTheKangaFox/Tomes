package com.vulp.tomes.mixin;

import com.mojang.datafixers.util.Pair;
import com.vulp.tomes.Tomes;
import com.vulp.tomes.enchantments.EnchantClueHolder;
import com.vulp.tomes.network.TomesPacketHandler;
import com.vulp.tomes.network.messages.ServerEnchantmentClueMessage;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.EnchantmentContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Signed;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Mixin(EnchantmentContainer.class)
public abstract class EnchantmentContainerMixin {

    @Shadow
    protected abstract List<EnchantmentData> getEnchantmentList(ItemStack stack, int enchantSlot, int level);

    @Shadow @Final public int[] enchantLevels;

    @Shadow public int[] enchantClue;

    @Inject(method = "lambda$onCraftMatrixChanged$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/container/EnchantmentContainer;detectAndSendChanges()V", shift = At.Shift.BY, by = -2))
    public void onCraftMatrixChangedLambda(ItemStack itemstack, World world, BlockPos pos, CallbackInfo ci) {
        EnchantClueHolder holder = new EnchantClueHolder();
        for (int buttonTracker = 0; buttonTracker < 3; ++buttonTracker) {
            if (this.enchantLevels[buttonTracker] > 0) {
                List<EnchantmentData> list = this.getEnchantmentList(itemstack, buttonTracker, this.enchantLevels[buttonTracker]);
                if (list != null && !list.isEmpty()) {
                    List<Pair<Enchantment, Integer>> cluedPairingList = new java.util.ArrayList<>(Collections.emptyList());
                    for (EnchantmentData enchantmentData : list) {
                        cluedPairingList.add(new Pair<>(enchantmentData.enchantment, enchantmentData.enchantmentLevel));
                    }
                    holder.setData(buttonTracker, cluedPairingList);
                }
            }
        }
        TomesPacketHandler.instance.send(PacketDistributor.ALL.noArg(), new ServerEnchantmentClueMessage(getThis().windowId, EnchantClueHolder.encodeClues(holder, enchantClue)));
    }

    // Allows us to reference the current instance of the class the mixin is injecting.
    private Container getThis() {
        return ((Container)(Object)this);
    }

}
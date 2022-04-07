package com.vulp.tomes.mixin;

import com.mojang.datafixers.util.Pair;
import com.vulp.tomes.Tomes;
import com.vulp.tomes.enchantments.EnchantClueHolder;
import com.vulp.tomes.network.TomesPacketHandler;
import com.vulp.tomes.network.messages.ServerEnchantmentClueMessage;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.EnchantmentContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.network.PacketDistributor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.List;
import java.util.Random;

@Mixin(EnchantmentContainer.class)
public class EnchantmentContainerMixin {

    @Shadow public int[] enchantClue;
    @Shadow @Final public Random rand;
    @Shadow @Final public int[] worldClue;

    private static EnchantClueHolder HOLDER = new EnchantClueHolder();
    private static boolean ENCHANT_LATCH = true;

    // NOTE: This method is not actually called in Apotheosis, it is a near identical method set up by Shadow instead. Ergo, this method is never called when Apotheosis is installed.
    // To fix, maybe hope that random is consistent, and either test for his mod and then reflect the info you need to calculate the data for yourself, or find a way to alter his code. Communicate with him.
    @Inject(method = "getEnchantmentList", at = @At("RETURN"))
    public void getEnchantmentList(ItemStack stack, int enchantSlot, int level, CallbackInfoReturnable<List<EnchantmentData>> cir) {
        if (ENCHANT_LATCH) {
            List<EnchantmentData> list = cir.getReturnValue();
            if (!list.isEmpty()) {
                List<Pair<Enchantment, Integer>> cluedPairingList = new java.util.ArrayList<>(Collections.emptyList());
                for (EnchantmentData enchantmentData : list) {
                    cluedPairingList.add(new Pair<>(enchantmentData.enchantment, enchantmentData.enchantmentLevel));
                }
                HOLDER.setData(enchantSlot, cluedPairingList);
                EnchantmentData data = list.get(this.rand.nextInt(list.size()));
                this.enchantClue[enchantSlot] = Registry.ENCHANTMENT.getId(data.enchantment);
                this.worldClue[enchantSlot] = data.enchantmentLevel;
            }
            if (enchantSlot == 2) {
                this.enchantClue = EnchantClueHolder.encodeClues(HOLDER, this.enchantClue);
                TomesPacketHandler.instance.send(PacketDistributor.ALL.noArg(), new ServerEnchantmentClueMessage(getThis().windowId, this.enchantClue));
                HOLDER = new EnchantClueHolder();
            }
        } else {
            ENCHANT_LATCH = true;
        }
    }

    @Inject(at = @At(value = "TAIL", target = "Lnet/minecraft/inventory/container/EnchantmentContainer;getEnchantmentList(Lnet/minecraft/item/ItemStack;II)Ljava/util/List;"), method = "enchantItem")
    public void enchantItem(PlayerEntity playerIn, int id, CallbackInfoReturnable<Boolean> cir) {
        ENCHANT_LATCH = false;
    }

    // Allows us to reference the current instance of the class the mixin is injecting.
    private EnchantmentContainer getThis() {
        return ((EnchantmentContainer)(Object)this);
    }

}

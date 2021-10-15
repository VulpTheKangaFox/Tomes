package com.vulp.tomes.mixin;

import com.vulp.tomes.enchantments.EnchantmentTypes;
import com.vulp.tomes.enchantments.TomeEnchantment;
import com.vulp.tomes.init.EnchantmentInit;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Set;

@Mixin(EnchantedBookItem.class)
public class EnchantedBookItemMixin {

    @OnlyIn(Dist.CLIENT)
    @Inject(at = @At("TAIL"), method = "addInformation")
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn, CallbackInfo ci) {
        Set<Enchantment> enchSet = EnchantmentHelper.getEnchantments(stack).keySet();
        if (enchSet.size() == 1) {
            Enchantment enchant = (Enchantment) enchSet.toArray()[0];
            if (enchant instanceof TomeEnchantment) {
                TextFormatting formatting;
                ITextComponent spell;
                if (enchant.type == EnchantmentTypes.ARCHAIC_TOME) {
                    formatting = TextFormatting.BLUE;
                    spell = new TranslationTextComponent("enchantment.tomes.archaic_spell");
                } else if (enchant.type == EnchantmentTypes.LIVING_TOME) {
                    formatting = TextFormatting.DARK_GREEN;
                    spell = new TranslationTextComponent("enchantment.tomes.living_spell");
                } else if (enchant.type == EnchantmentTypes.CURSED_TOME) {
                    formatting = TextFormatting.DARK_RED;
                    spell = new TranslationTextComponent("enchantment.tomes.cursed_spell");
                } else {
                    formatting = TextFormatting.GRAY;
                    spell = new StringTextComponent("");
                }
                TranslationTextComponent active = (((TomeEnchantment) enchant).isActive() ? new TranslationTextComponent("enchantment.tomes.active") : new TranslationTextComponent("enchantment.tomes.passive"));
                    tooltip.add(new StringTextComponent(spell.getString() + active.getString()).mergeStyle(formatting, TextFormatting.BOLD));
                    if (!Screen.hasShiftDown()) {
                        tooltip.add(new TranslationTextComponent("item.tomes.hold_shift").mergeStyle(formatting, TextFormatting.ITALIC));
                    } else {
                        tooltip.add(EnchantmentInit.TOME_DESCRIPTIONS.get(enchant).mergeStyle(formatting));
                }
            }
        }
    }

}

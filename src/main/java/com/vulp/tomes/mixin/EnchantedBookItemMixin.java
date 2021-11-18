package com.vulp.tomes.mixin;

import com.mojang.datafixers.util.Pair;
import com.vulp.tomes.enchantments.EnchantmentTypes;
import com.vulp.tomes.enchantments.TomeEnchantment;
import com.vulp.tomes.init.EnchantmentInit;
import com.vulp.tomes.items.TomeItem;
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

import javax.xml.soap.Text;
import java.util.List;
import java.util.Set;

@Mixin(EnchantedBookItem.class)
public class EnchantedBookItemMixin {

    @OnlyIn(Dist.CLIENT)
    @Inject(at = @At("TAIL"), method = "addInformation")
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn, CallbackInfo ci) {
        Set<Enchantment> enchSet = EnchantmentHelper.getEnchantments(stack).keySet();
        enchSet.removeIf(enchantment -> !(enchantment instanceof TomeEnchantment));
        if (enchSet.size() > 0) {
            if (enchSet.size() == 1 && enchSet.stream().anyMatch(enchantment -> ((TomeEnchantment)enchantment).getSpellIndex().getSpell().isDisabled())) {
                Enchantment[] enchantments = enchSet.toArray(new Enchantment[]{});
                tooltip.add(new StringTextComponent(new TranslationTextComponent(enchantments[0].getName()).getString() + " - DISABLED").mergeStyle(TextFormatting.RED, TextFormatting.BOLD, TextFormatting.ITALIC));
            } else if (Screen.hasShiftDown()) {
                for (Enchantment enchant : enchSet) {
                    if (enchant instanceof TomeEnchantment) {
                        if (!((TomeEnchantment) enchant).getSpellIndex().getSpell().isDisabled()) {
                            Pair<TextFormatting, TextFormatting> formatting;
                            ITextComponent spell;
                            if (enchant.type == EnchantmentTypes.ARCHAIC_TOME) {
                                formatting = new Pair<>(TextFormatting.AQUA, TextFormatting.BLUE);
                                spell = new TranslationTextComponent("enchantment.tomes.archaic_spell");
                            } else if (enchant.type == EnchantmentTypes.LIVING_TOME) {
                                formatting = new Pair<>(TextFormatting.GREEN, TextFormatting.DARK_GREEN);
                                spell = new TranslationTextComponent("enchantment.tomes.living_spell");
                            } else if (enchant.type == EnchantmentTypes.CURSED_TOME) {
                                formatting = new Pair<>(TextFormatting.RED, TextFormatting.DARK_RED);
                                spell = new TranslationTextComponent("enchantment.tomes.cursed_spell");
                            } else {
                                formatting = new Pair<>(TextFormatting.GRAY, TextFormatting.DARK_GRAY);
                                spell = new StringTextComponent("");
                            }
                            TranslationTextComponent active = (((TomeEnchantment) enchant).isActive() ? new TranslationTextComponent("enchantment.tomes.active") : new TranslationTextComponent("enchantment.tomes.passive"));
                            tooltip.add(new TranslationTextComponent(enchant.getName()).mergeStyle(formatting.getFirst(), TextFormatting.BOLD));
                            tooltip.add(new StringTextComponent("[ " + spell.getString() + active.getString() + " ]").mergeStyle(formatting.getFirst(), TextFormatting.ITALIC));
                            tooltip.add(EnchantmentInit.TOME_DESCRIPTIONS.get(enchant).mergeStyle(formatting.getSecond()));
                        } else {
                            tooltip.add(new StringTextComponent(new TranslationTextComponent(enchant.getName()).getString() + " - DISABLED").mergeStyle(TextFormatting.RED, TextFormatting.BOLD, TextFormatting.ITALIC));
                        }
                    }
                }
            } else {
                tooltip.add(new TranslationTextComponent("item.tomes.hold_shift").mergeStyle(TextFormatting.DARK_GRAY, TextFormatting.ITALIC));
            }
        }
    }

}

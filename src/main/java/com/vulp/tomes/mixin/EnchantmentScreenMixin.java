package com.vulp.tomes.mixin;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import com.vulp.tomes.enchantments.EnchantClueHolder;
import com.vulp.tomes.init.EnchantmentInit;
import com.vulp.tomes.items.TomeItem;
import net.minecraft.client.gui.screen.EnchantmentScreen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.EnchantmentContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;

@OnlyIn(Dist.CLIENT)
@Mixin(EnchantmentScreen.class)
public abstract class EnchantmentScreenMixin extends ContainerScreen<EnchantmentContainer> {

    public EnchantmentScreenMixin(EnchantmentContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    private boolean isItemEnchantable(ItemStack itemStack) {
        return !itemStack.isEnchanted() && itemStack.isEnchantable() && !itemStack.isEmpty();
    }

    private boolean hasTome() {
        PlayerInventory inventory = this.playerInventory;
        return inventory != null && Arrays.stream(new ItemStack[]{inventory.getCurrentItem(), inventory.offHandInventory.get(0)}).anyMatch(item -> item.getItem() instanceof TomeItem && EnchantmentHelper.getEnchantments(item).containsKey(EnchantmentInit.linguist));
    }

    /**
     * @author VulpTheHorseDog
     */
    @Inject(at = @At("HEAD"), method = "render", cancellable = true)
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (this.hasTome()) {
            partialTicks = this.minecraft.getRenderPartialTicks();
            this.renderBackground(matrixStack);
            super.render(matrixStack, mouseX, mouseY, partialTicks);
            this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
            boolean flag = this.minecraft.player.abilities.isCreativeMode;
            int i = this.container.getLapisAmount();
            EnchantClueHolder clueHolder = EnchantClueHolder.decodeClues(this.container.enchantClue);
            for (int j = 0; j < 3; ++j) {
                List<Pair<Enchantment, Integer>> clueList = clueHolder.getData(j);
                // Stuff below here is all used in strings and positioning.
                List<ITextComponent> list = Lists.newArrayList();
                Enchantment enchantment = Enchantment.getEnchantmentByID((this.container).enchantClue[j]);
                int l = (this.container).worldClue[j];
                int k = 0;
                int i1 = j + 1;
                // Below here is actual string and positioning code.
                if (this.isPointInRegion(60, 14 + 19 * j, 108, 17, (double) mouseX, (double) mouseY) && clueList.size() > 0 && isItemEnchantable(this.container.tableInventory.getStackInSlot(0))) {
                    // Trying to run a for loop to create a list of all enchantments.
                    if (!this.hasTome()) {
                        list.add((new TranslationTextComponent("container.enchant.clue", enchantment == null ? "" : enchantment.getDisplayName(l))).mergeStyle(TextFormatting.WHITE));
                    } else {
                        for (Pair<Enchantment, Integer> clue : clueList) {
                            if (clue.getFirst() != null) {
                                list.add(getDisplayName(clue.getFirst(), clue.getSecond()));
                            } else {
                                list.add(new StringTextComponent(". . .").mergeStyle(TextFormatting.AQUA));
                            }
                        }
                    }
                    if (clueList.isEmpty()) {
                        list.add(new StringTextComponent(""));
                        list.add(new TranslationTextComponent("forge.container.enchant.limitedEnchantability").mergeStyle(TextFormatting.RED));
                    } else if (!flag) {
                        list.add(StringTextComponent.EMPTY);
                        if (this.minecraft.player.experienceLevel < k) {
                            list.add((new TranslationTextComponent("container.enchant.level.requirement", (this.container).enchantLevels[j])).mergeStyle(TextFormatting.RED));
                        } else {
                            IFormattableTextComponent iformattabletextcomponent;
                            if (i1 == 1) {
                                iformattabletextcomponent = new TranslationTextComponent("container.enchant.lapis.one");
                            } else {
                                iformattabletextcomponent = new TranslationTextComponent("container.enchant.lapis.many", i1);
                            }

                            list.add(iformattabletextcomponent.mergeStyle(i >= i1 ? TextFormatting.GRAY : TextFormatting.RED));
                            IFormattableTextComponent iformattabletextcomponent1;
                            if (i1 == 1) {
                                iformattabletextcomponent1 = new TranslationTextComponent("container.enchant.level.one");
                            } else {
                                iformattabletextcomponent1 = new TranslationTextComponent("container.enchant.level.many", i1);
                            }

                            list.add(iformattabletextcomponent1.mergeStyle(TextFormatting.GRAY));
                        }
                    }

                    this.func_243308_b(matrixStack, list, mouseX, mouseY);
                    break;
                }
            }
            ci.cancel();
        }

    }

    private ITextComponent getDisplayName(Enchantment enchantment, int level) {
        IFormattableTextComponent iformattabletextcomponent = new TranslationTextComponent(enchantment.getName());
        if (enchantment.isCurse()) {
            iformattabletextcomponent.mergeStyle(TextFormatting.RED);
        } else {
            iformattabletextcomponent.mergeStyle(TextFormatting.AQUA);
        }

        if (level != 1 || enchantment.getMaxLevel() != 1) {
            iformattabletextcomponent.appendString(" ").appendSibling(new TranslationTextComponent("enchantment.level." + level));
        }

        return iformattabletextcomponent;
    }

}

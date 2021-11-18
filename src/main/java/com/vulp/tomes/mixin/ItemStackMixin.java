package com.vulp.tomes.mixin;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.gson.JsonParseException;
import com.vulp.tomes.items.TomeItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow public abstract Item getItem();

    @Shadow public abstract boolean isEnchanted();

    @Shadow public abstract ITextComponent getDisplayName();

    @Shadow public abstract Rarity getRarity();

    @Shadow public abstract boolean hasDisplayName();

    @Shadow protected abstract int func_242393_J();

    @Shadow
    private static boolean func_242394_a(int p_242394_0_, ItemStack.TooltipDisplayFlags p_242394_1_) {
        return false;
    }

    @Shadow public abstract ListNBT getEnchantmentTagList();

    @Shadow
    public static void addEnchantmentTooltips(List<ITextComponent> p_222120_0_, ListNBT p_222120_1_) {
    }

    @Shadow public abstract boolean hasTag();

    @Shadow private CompoundNBT tag;

    @Shadow @Final private static Style LORE_STYLE;

    @Shadow public abstract Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot);

    @Shadow
    private static Collection<ITextComponent> getPlacementTooltip(String stateString) {
        return null;
    }

    @Shadow public abstract boolean isDamaged();

    @Shadow public abstract int getMaxDamage();

    @Shadow public abstract int getDamage();

    // NOTE: This entire mixin is set up to solve the simple issue of the enchants showing up BEFORE the item description, which I didn't want.
    @Inject(at = @At(value = "HEAD"), method = "getTooltip", cancellable = true)
    void getTooltip(PlayerEntity playerIn, ITooltipFlag advanced, CallbackInfoReturnable<List<ITextComponent>> cir) {
        Item item = this.getItem();
        if (item instanceof TomeItem && this.isEnchanted()) {
            List<ITextComponent> list = Lists.newArrayList();
            IFormattableTextComponent iformattabletextcomponent = (new StringTextComponent("")).appendSibling(this.getDisplayName()).mergeStyle(this.getRarity().color);
            if (this.hasDisplayName()) {
                iformattabletextcomponent.mergeStyle(TextFormatting.ITALIC);
            }

            list.add(iformattabletextcomponent);
            if (!advanced.isAdvanced() && !this.hasDisplayName() && this.getItem() == Items.FILLED_MAP) {
                list.add((new StringTextComponent("#" + FilledMapItem.getMapId(getThis()))).mergeStyle(TextFormatting.GRAY));
            }

            int i = this.func_242393_J();

            if (this.hasTag()) {
                if (func_242394_a(i, ItemStack.TooltipDisplayFlags.ENCHANTMENTS)) {
                    addEnchantmentTooltips(list, this.getEnchantmentTagList());
                }

                if (this.tag.contains("display", 10)) {
                    CompoundNBT compoundnbt = this.tag.getCompound("display");
                    if (func_242394_a(i, ItemStack.TooltipDisplayFlags.DYE) && compoundnbt.contains("color", 99)) {
                        if (advanced.isAdvanced()) {
                            list.add((new TranslationTextComponent("item.color", String.format("#%06X", compoundnbt.getInt("color")))).mergeStyle(TextFormatting.GRAY));
                        } else {
                            list.add((new TranslationTextComponent("item.dyed")).mergeStyle(TextFormatting.GRAY, TextFormatting.ITALIC));
                        }
                    }

                    if (compoundnbt.getTagId("Lore") == 9) {
                        ListNBT listnbt = compoundnbt.getList("Lore", 8);

                        for(int j = 0; j < listnbt.size(); ++j) {
                            String s = listnbt.getString(j);

                            try {
                                IFormattableTextComponent iformattabletextcomponent1 = ITextComponent.Serializer.getComponentFromJson(s);
                                if (iformattabletextcomponent1 != null) {
                                    list.add(TextComponentUtils.func_240648_a_(iformattabletextcomponent1, LORE_STYLE));
                                }
                            } catch (JsonParseException jsonparseexception) {
                                compoundnbt.remove("Lore");
                            }
                        }
                    }
                }
            }

            if (func_242394_a(i, ItemStack.TooltipDisplayFlags.ADDITIONAL)) {
                this.getItem().addInformation(getThis(), playerIn == null ? null : playerIn.world, list, advanced);
            }

            if (this.hasTag()) {
                if (func_242394_a(i, ItemStack.TooltipDisplayFlags.UNBREAKABLE) && this.tag.getBoolean("Unbreakable")) {
                    list.add((new TranslationTextComponent("item.unbreakable")).mergeStyle(TextFormatting.BLUE));
                }

                if (func_242394_a(i, ItemStack.TooltipDisplayFlags.CAN_DESTROY) && this.tag.contains("CanDestroy", 9)) {
                    ListNBT listnbt1 = this.tag.getList("CanDestroy", 8);
                    if (!listnbt1.isEmpty()) {
                        list.add(StringTextComponent.EMPTY);
                        list.add((new TranslationTextComponent("item.canBreak")).mergeStyle(TextFormatting.GRAY));

                        for(int k = 0; k < listnbt1.size(); ++k) {
                            list.addAll(this.getPlacementTooltip(listnbt1.getString(k)));
                        }
                    }
                }

                if (func_242394_a(i, ItemStack.TooltipDisplayFlags.CAN_PLACE) && this.tag.contains("CanPlaceOn", 9)) {
                    ListNBT listnbt2 = this.tag.getList("CanPlaceOn", 8);
                    if (!listnbt2.isEmpty()) {
                        list.add(StringTextComponent.EMPTY);
                        list.add((new TranslationTextComponent("item.canPlace")).mergeStyle(TextFormatting.GRAY));

                        for(int l = 0; l < listnbt2.size(); ++l) {
                            list.addAll(this.getPlacementTooltip(listnbt2.getString(l)));
                        }
                    }
                }
            }

            if (advanced.isAdvanced()) {
                if (this.isDamaged()) {
                    list.add(new TranslationTextComponent("item.durability", this.getMaxDamage() - this.getDamage(), this.getMaxDamage()));
                }

                list.add((new StringTextComponent(Registry.ITEM.getKey(this.getItem()).toString())).mergeStyle(TextFormatting.DARK_GRAY));
                if (this.hasTag()) {
                    list.add((new TranslationTextComponent("item.nbt_tags", this.tag.keySet().size())).mergeStyle(TextFormatting.DARK_GRAY));
                }
            }

            net.minecraftforge.event.ForgeEventFactory.onItemTooltip(getThis(), playerIn, list, advanced);
            cir.setReturnValue(list);
        }
    }

    // Allows us to reference the current instance of the class the mixin is injecting.
    private ItemStack getThis() {
        return ((ItemStack)(Object)this);
    }


}

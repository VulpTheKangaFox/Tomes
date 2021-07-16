package com.vulp.tomes.items;

import com.vulp.tomes.Tomes;
import com.vulp.tomes.client.renderer.tileentity.TomeTileEntityRenderer;
import com.vulp.tomes.enchantments.TomeEnchantment;
import com.vulp.tomes.spells.SpellIndex;
import com.vulp.tomes.spells.active.ActiveSpell;
import com.vulp.tomes.spells.passive.PassiveSpell;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class TomeItem extends Item {

    public TomeItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getItemEnchantability() {
        return 15;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (enchantment != Enchantments.UNBREAKING) {
            return super.canApplyAtEnchantingTable(stack, enchantment);
        } else return false;
    }

    @Nullable
    private static SpellIndex getActiveSpell(ItemStack itemStack) {
        CompoundNBT nbt = itemStack.getOrCreateTag();
        if (nbt.contains("active")) {
            return SpellIndex.spellIndexFromId(nbt.getInt("active"));
        } else {
            nbt.putInt("active", -1);
            return null;
        }
    }

    @Nullable
    private static SpellIndex[] getPassiveSpells(ItemStack itemStack) {
        CompoundNBT nbt = itemStack.getOrCreateTag();
        if (nbt.contains("passive")) {
            return SpellIndex.spellIndexArrayFromIds(nbt.getIntArray("passive"));
        } else {
            nbt.putIntArray("passive", new int[]{-1});
            return null;
        }
    }

    private static void setActiveSpell(ItemStack itemStack, SpellIndex spellIndex) {
        CompoundNBT nbt = itemStack.getOrCreateTag();
        nbt.putInt("active", SpellIndex.idFromSpellIndex(spellIndex));
    }

    private static void setPassiveSpells(ItemStack itemStack, SpellIndex[] spellIndex) {
        CompoundNBT nbt = itemStack.getOrCreateTag();
        nbt.putIntArray("passive", SpellIndex.idArrayFromSpellIndexes(spellIndex));
    }

    private static int getRemainingDurability(ItemStack stack) {
        return stack.getMaxDamage() - stack.getDamage();
    }

    private boolean canCast(ItemStack stack) {
        SpellIndex activeSpellIndex = getActiveSpell(stack);
        if (activeSpellIndex != null) {
            ActiveSpell spell = (ActiveSpell) activeSpellIndex.getSpell();
            return spell.getSpellCost() < getRemainingDurability(stack);
        } else return false;
    }

    private void castActiveSpell(World worldIn, ItemStack stack, PlayerEntity playerIn, Hand handIn) {
        SpellIndex activeSpellIndex = getActiveSpell(stack);
        if (activeSpellIndex != null) {
            ActiveSpell spell = (ActiveSpell) activeSpellIndex.getSpell();
            spell.onCast(worldIn, playerIn, handIn);
        }
    }

    private static void setCooldown(ItemStack stack, int ticks) {
        CompoundNBT nbt = stack.getOrCreateTag();
        nbt.putInt("cooldown", ticks);
        nbt.putInt("cooldownTotal", ticks);
        nbt.putInt("pixels", 16);
    }

    public static int getCooldown(ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateTag();
        if (nbt.contains("cooldown")) {
            return nbt.getInt("cooldown");
        } else return 0;
    }

    public static int getCooldownMax(ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateTag();
        if (nbt.contains("cooldownTotal")) {
            return nbt.getInt("cooldownTotal");
        } else return 0;
    }

    private static void reduceCooldown(ItemStack stack, int ticks) {
        CompoundNBT nbt = stack.getOrCreateTag();
        if (nbt.contains("cooldown")) {
            if (nbt.getInt("cooldown") > 0) {
                nbt.putInt("cooldown", nbt.getInt("cooldown") - ticks);
            }
        }
        int max = getCooldownMax(stack);
        int i = getCooldown(stack);
        int j = -1;
        if (max > 0) {
            j = (int) (((float) i / (float)max) * 16);
        }
        Tomes.LOGGER.debug(i + " / " + max + " || " + j);
        nbt.putInt("pixels", j);
    }

    private static boolean onCooldown(ItemStack stack) {
        return getCooldown(stack) > 0;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateTag();
        if (nbt.getInt("pixels") > 0) {
            return false;
        }
        return super.hasEffect(stack);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        final ItemStack[] stack = {playerIn.getHeldItem(handIn)};
        SpellIndex activeSpellIndex = getActiveSpell(stack[0]);
        if (activeSpellIndex != null && !onCooldown(stack[0])) {
            ActiveSpell spell = (ActiveSpell) activeSpellIndex.getSpell();
            if (spell != null && this.canCast(stack[0])) {
                this.castActiveSpell(worldIn, stack[0], playerIn, handIn);
                stack[0].damageItem(spell.getSpellCost(), playerIn, playerEntity -> stack[0] = new ItemStack(stack[0].getItem()));
                setCooldown(stack[0], spell.getCooldown());
                return ActionResult.resultSuccess(stack[0]);
            }
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        SpellIndex[] passiveSpells = getPassiveSpells(stack);
        if (isSelected && passiveSpells != null && passiveSpells.length > 0) {
            for (SpellIndex passiveSpell : passiveSpells) {
                ((PassiveSpell)passiveSpell.getSpell()).tick();
            }
        }

        SpellIndex activeSpellIndex = getActiveSpell(stack);
        ActiveSpell activeSpell = null;
        if (activeSpellIndex != null) {
            activeSpell = (ActiveSpell) activeSpellIndex.getSpell();
        }

        boolean hasActiveSpell = false;
        List<SpellIndex> passiveList = new java.util.ArrayList<>(Collections.emptyList());
        for (Enchantment enchant : EnchantmentHelper.getEnchantments(stack).keySet()) {

            if (enchant instanceof TomeEnchantment) {
                SpellIndex spellIndex = ((TomeEnchantment) enchant).getSpellIndex();
                if (((TomeEnchantment) enchant).isActive()) {
                    hasActiveSpell = true;
                    if (activeSpell == null || spellIndex.getSpell() != activeSpell) {
                        setActiveSpell(stack, spellIndex);
                    }
                } else {
                    passiveList.add(spellIndex);
                }
            }
        }
        if (!passiveList.isEmpty()) {
            SpellIndex[] passiveIndex = new SpellIndex[passiveList.size()];
            passiveList.toArray(passiveIndex);
            setPassiveSpells(stack, passiveIndex);
        } else {
            if (!hasActiveSpell) {
                setDamage(stack, 0);
            } else {
                setPassiveSpells(stack, new SpellIndex[]{});
            }
        }

        reduceCooldown(stack, 1);
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

}

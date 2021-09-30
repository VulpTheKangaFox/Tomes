package com.vulp.tomes.items;

import com.mojang.datafixers.util.Pair;
import com.vulp.tomes.enchantments.TomeEnchantment;
import com.vulp.tomes.init.ItemInit;
import com.vulp.tomes.network.TomesPacketHandler;
import com.vulp.tomes.network.messages.ServerActiveSpellMessage;
import com.vulp.tomes.spells.SpellIndex;
import com.vulp.tomes.spells.active.ActiveSpell;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class TomeItem extends Item {

    public TomeItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getItemEnchantability() {
        return 40;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        EnchantmentType type = enchantment.type;
        if (type != EnchantmentType.BREAKABLE) {
            return super.canApplyAtEnchantingTable(stack, enchantment);
        }
        return false;
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

    private static boolean canCast(ItemStack stack) {
        SpellIndex activeSpellIndex = getActiveSpell(stack);
        if (activeSpellIndex != null) {
            ActiveSpell spell = (ActiveSpell) activeSpellIndex.getSpell();
            return spell.getSpellCost() < getRemainingDurability(stack);
        } else return false;
    }

    private static boolean castActiveSpell(World worldIn, ItemStack stack, PlayerEntity playerIn, Hand handIn) {
        SpellIndex activeSpellIndex = getActiveSpell(stack);
        if (activeSpellIndex != null) {
            ActiveSpell spell = (ActiveSpell) activeSpellIndex.getSpell();
            return spell.onCast(worldIn, playerIn, handIn);
        } else return false;
    }

    private static void setCooldown(ItemStack stack, int ticks) {
        CompoundNBT nbt = stack.getOrCreateTag();
        nbt.putInt("cooldown", ticks);
        nbt.putInt("cooldownTotal", ticks);
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
    }

    private static boolean onCooldown(ItemStack stack) {
        return getCooldown(stack) > 0;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand handIn) {
        SpellIndex activeSpellIndex = getActiveSpell(stack);
        if (activeSpellIndex != null) {
            ActiveSpell spell = (ActiveSpell) activeSpellIndex.getSpell();
            spell.setTarget(target);
        }
        return super.itemInteractionForEntity(stack, playerIn, target, handIn);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        final ItemStack[] stack = {playerIn.getHeldItem(handIn)};
        SpellIndex activeSpellIndex = getActiveSpell(stack[0]);
        if (activeSpellIndex != null && !onCooldown(stack[0])) {
            ActiveSpell spell = (ActiveSpell) activeSpellIndex.getSpell();
            if (!worldIn.isRemote && spell != null && canCast(stack[0])) {
                boolean flag = false;
                if (castActiveSpell(worldIn, stack[0], playerIn, handIn)) {
                    stack[0].damageItem(spell.getSpellCost(), playerIn, playerEntity -> stack[0] = new ItemStack(stack[0].getItem()));
                    setCooldown(stack[0], spell.getCooldown());
                    flag = true;
                }
                TomesPacketHandler.instance.send(PacketDistributor.ALL.noArg(), new ServerActiveSpellMessage(stack[0], handIn == Hand.MAIN_HAND));
                if (flag) {
                    return ActionResult.resultSuccess(stack[0]);
                }
            }
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    public static void clientCastActiveSpell(World worldIn, PlayerEntity playerIn, ItemStack[] stack, Hand handIn) {
        SpellIndex activeSpellIndex = getActiveSpell(stack[0]);
        if (activeSpellIndex != null) {
            ActiveSpell spell = (ActiveSpell) activeSpellIndex.getSpell();
            if (spell != null) {
                if (castActiveSpell(worldIn, stack[0], playerIn, handIn)) {
                    stack[0].damageItem(spell.getSpellCost(), playerIn, playerEntity -> stack[0] = new ItemStack(stack[0].getItem()));
                    setCooldown(stack[0], spell.getCooldown());
                }
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        SpellIndex[] passiveSpells = getPassiveSpells(stack);
        if (isSelected && passiveSpells != null && passiveSpells.length > 0) {
            for (SpellIndex passiveSpell : passiveSpells) {
                passiveSpell.getSpell().tickEvent(worldIn, entityIn);
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
            setPassiveSpells(stack, new SpellIndex[]{});
        }
        if (hasActiveSpell && activeSpell != null) {
            activeSpell.tickEvent(worldIn, entityIn);
        } else {
            if (passiveList.isEmpty()) {
                setDamage(stack, 0);
                setPassiveSpells(stack, new SpellIndex[]{});
            }
        }

        reduceCooldown(stack, 1);
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

}

package com.vulp.tomes.items;

import com.mojang.datafixers.util.Pair;
import com.vulp.tomes.enchantments.EnchantmentTypes;
import com.vulp.tomes.enchantments.TomeEnchantment;
import com.vulp.tomes.init.EnchantmentInit;
import com.vulp.tomes.init.ItemInit;
import com.vulp.tomes.network.TomesPacketHandler;
import com.vulp.tomes.network.messages.ServerActiveSpellMessage;
import com.vulp.tomes.spells.SpellIndex;
import com.vulp.tomes.spells.active.ActiveSpell;
import com.vulp.tomes.spells.passive.CovensRuleSpell;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.curios.api.CuriosApi;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class TomeItem extends HiddenDescriptorItem {

    private static final Predicate<ItemStack> AMULET_PREDICATE = stack -> stack.getItem() == ItemInit.amulet_of_cogency;

    public TomeItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getItemEnchantability() {
        return 30;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        EnchantmentType type = enchantment.type;
        if (type != EnchantmentType.BREAKABLE) {
            return super.canApplyAtEnchantingTable(stack, enchantment);
        }
        return false;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return (toRepair.getItem() == ItemInit.archaic_tome && repair.getItem() == ItemInit.ancient_heart) || (toRepair.getItem() == ItemInit.living_tome && repair.getItem() == ItemInit.beating_heart) || (toRepair.getItem() == ItemInit.cursed_tome && repair.getItem() == ItemInit.sweet_heart) || super.getIsRepairable(toRepair, repair);
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

    private static void removeActiveSpell(ItemStack itemStack) {
        CompoundNBT nbt = itemStack.getOrCreateTag();
        nbt.remove("active");
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
            return !spell.isDisabled() && spell.getSpellCost() < getRemainingDurability(stack);
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
        if (activeSpellIndex != null && !activeSpellIndex.getSpell().isDisabled()) {
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
            if (spell != null) {
                if (!worldIn.isRemote && canCast(stack[0])) {
                    SpellIndex[] index = getPassiveSpells(stack[0]);
                    if (index != null && Arrays.stream(index).anyMatch(s -> s.getSpell() instanceof CovensRuleSpell)) {
                        if (spell.getTarget() instanceof WitchEntity) {
                            return ActionResult.resultFail(stack[0]);
                        }
                    }
                    boolean flag = false;
                    if (castActiveSpell(worldIn, stack[0], playerIn, handIn)) {
                        stack[0].damageItem(spell.getSpellCost(), playerIn, playerEntity -> stack[0] = new ItemStack(stack[0].getItem()));
                        setCooldown(stack[0], spell.getCooldown() / (hasAmulet(playerIn) ? 2 : 1));
                        flag = true;
                    }
                    TomesPacketHandler.instance.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) playerIn), new ServerActiveSpellMessage(stack[0], handIn == Hand.MAIN_HAND));
                    if (flag) {
                        return ActionResult.resultSuccess(stack[0]);
                    }
                }
                spell.setTarget(null);
            }
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    private static boolean hasAmulet(PlayerEntity player) {
        return CuriosApi.getCuriosHelper().findEquippedCurio(AMULET_PREDICATE, player).isPresent() || player.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() == ItemInit.amulet_of_cogency;
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
        SpellIndex[] passiveSpells;
        for (ItemStack itemStack : entityIn.getHeldEquipment()) {
            passiveSpells = getPassiveSpells(stack);
            if (itemStack == stack && passiveSpells != null && passiveSpells.length > 0) {
                for (SpellIndex passiveSpell : passiveSpells) {
                    if (!passiveSpell.getSpell().isDisabled()) {
                        passiveSpell.getSpell().tickEvent(worldIn, entityIn);
                    }
                }
                break;
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
            CompoundNBT nbt = stack.getOrCreateTag();
            nbt.remove("passive");
        }
        if (hasActiveSpell) {
            if (activeSpell != null && !activeSpell.isDisabled()) {
                activeSpell.tickEvent(worldIn, entityIn);
            }
        } else {
            setDamage(stack, 0);
            removeActiveSpell(stack);
        }

        reduceCooldown(stack, 1);
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        Set<Enchantment> enchSet = EnchantmentHelper.getEnchantments(stack).keySet();
        enchSet.removeIf(enchantment -> !(enchantment instanceof TomeEnchantment));
        if (enchSet.size() > 0) {
            if (Screen.hasShiftDown()) {
                for (Enchantment enchant : enchSet) {
                    if (enchant instanceof TomeEnchantment) {
                        if (!((TomeEnchantment) enchant).getSpellIndex().getSpell().isDisabled()) {
                            Pair<TextFormatting, TextFormatting> formatting;
                            if (enchant.type == EnchantmentTypes.ARCHAIC_TOME) {
                                formatting = new Pair<>(TextFormatting.AQUA, TextFormatting.BLUE);
                            } else if (enchant.type == EnchantmentTypes.LIVING_TOME) {
                                formatting = new Pair<>(TextFormatting.GREEN, TextFormatting.DARK_GREEN);
                            } else if (enchant.type == EnchantmentTypes.CURSED_TOME) {
                                formatting = new Pair<>(TextFormatting.RED, TextFormatting.DARK_RED);
                            } else {
                                formatting = new Pair<>(TextFormatting.GRAY, TextFormatting.DARK_GRAY);
                            }
                            TranslationTextComponent active = (((TomeEnchantment) enchant).isActive() ? new TranslationTextComponent("enchantment.tomes.active") : new TranslationTextComponent("enchantment.tomes.passive"));
                            tooltip.add(new StringTextComponent(new TranslationTextComponent(enchant.getName()).getString() + " - " + active.getString()).mergeStyle(formatting.getFirst(), TextFormatting.BOLD));
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

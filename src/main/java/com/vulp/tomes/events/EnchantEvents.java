package com.vulp.tomes.events;

import com.mojang.datafixers.util.Pair;
import com.vulp.tomes.Tomes;
import com.vulp.tomes.enchantments.EnchantClueHolder;
import com.vulp.tomes.network.TomesPacketHandler;
import com.vulp.tomes.network.messages.ServerEnchantmentClueMessage;
import net.minecraft.block.BlockState;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.EnchantmentContainer;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.EnchantingTableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber(modid= Tomes.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class EnchantEvents {

    /*@SubscribeEvent
    public static void onEnchantLevelSetEvent(EnchantmentLevelSetEvent event) {
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        BlockState state = world.getBlockState(pos);
        Container container1 = state.getBlock().getContainer(state, world, pos).createMenu();
        if (state.getBlock() instanceof EnchantingTableBlock && state.getBlock().getContainer(state, world, pos) instanceof EnchantmentContainer) {
            EnchantmentContainer container = (EnchantmentContainer) container1;
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

                                EnchantmentData enchantmentdata = list.get(container.rand.nextInt(list.size()));
                                container.enchantClue[j1] = Registry.ENCHANTMENT.getId(enchantmentdata.enchantment);
                                container.worldClue[j1] = enchantmentdata.enchantmentLevel;
                            }
                        }
                    }
                    container.enchantClue = EnchantClueHolder.encodeClues(holder, container.enchantClue);
                    TomesPacketHandler.instance.send(PacketDistributor.ALL.noArg(), new ServerEnchantmentClueMessage(container.windowId, container.enchantClue));
                });
            }
        }

    }*/

}

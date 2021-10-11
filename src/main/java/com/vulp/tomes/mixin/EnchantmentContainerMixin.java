package com.vulp.tomes.mixin;

import com.mojang.datafixers.util.Pair;
import com.vulp.tomes.enchantments.EnchantClueHolder;
import com.vulp.tomes.network.TomesPacketHandler;
import com.vulp.tomes.network.messages.ServerEnchantmentClueMessage;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.EnchantmentContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.network.PacketDistributor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Mixin(EnchantmentContainer.class)
public abstract class EnchantmentContainerMixin extends Container {

    @Final
    @Shadow
    public IInventory tableInventory;
    @Final
    @Shadow
    public IWorldPosCallable worldPosCallable;
    @Final
    @Shadow
    private Random rand;
    @Final
    @Shadow
    public IntReferenceHolder xpSeed;
    @Final
    @Shadow
    public int[] enchantLevels;
    @Shadow
    public int[] enchantClue;
    @Final
    @Shadow
    public int[] worldClue;

    protected EnchantmentContainerMixin(@Nullable ContainerType<?> type, int id) {
        super(type, id);
    }

    /**
     * @author VulpTheHorseDog
     */
    @Overwrite
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        if (inventoryIn == this.tableInventory) {
            ItemStack itemstack = inventoryIn.getStackInSlot(0);
            if (!itemstack.isEmpty() && itemstack.isEnchantable()) {
                this.worldPosCallable.consume((p_217002_2_, p_217002_3_) -> {
                    int power = 0;

                    for(int k = -1; k <= 1; ++k) {
                        for(int l = -1; l <= 1; ++l) {
                            if ((k != 0 || l != 0) && p_217002_2_.isAirBlock(p_217002_3_.add(l, 0, k)) && p_217002_2_.isAirBlock(p_217002_3_.add(l, 1, k))) {
                                power += getPower(p_217002_2_, p_217002_3_.add(l * 2, 0, k * 2));
                                power += getPower(p_217002_2_, p_217002_3_.add(l * 2, 1, k * 2));

                                if (l != 0 && k != 0) {
                                    power += getPower(p_217002_2_, p_217002_3_.add(l * 2, 0, k));
                                    power += getPower(p_217002_2_, p_217002_3_.add(l * 2, 1, k));
                                    power += getPower(p_217002_2_, p_217002_3_.add(l, 0, k * 2));
                                    power += getPower(p_217002_2_, p_217002_3_.add(l, 1, k * 2));
                                }
                            }
                        }
                    }

                    this.rand.setSeed(this.xpSeed.get());

                    for(int i1 = 0; i1 < 3; ++i1) {
                        this.enchantLevels[i1] = EnchantmentHelper.calcItemStackEnchantability(this.rand, i1, (int)power, itemstack);
                        this.enchantClue[i1] = -1;
                        this.worldClue[i1] = -1;
                        if (this.enchantLevels[i1] < i1 + 1) {
                            this.enchantLevels[i1] = 0;
                        }
                        this.enchantLevels[i1] = net.minecraftforge.event.ForgeEventFactory.onEnchantmentLevelSet(p_217002_2_, p_217002_3_, i1, (int)power, itemstack, enchantLevels[i1]);
                    }

                    EnchantClueHolder holder = new EnchantClueHolder();
                    for(int j1 = 0; j1 < 3; ++j1) {
                        if (this.enchantLevels[j1] > 0) {
                            List<EnchantmentData> list = this.getEnchantmentList(itemstack, j1, this.enchantLevels[j1]);
                            if (list != null && !list.isEmpty()) {
                                List<Pair<Enchantment, Integer>> cluedPairingList = new java.util.ArrayList<>(Collections.emptyList());

                                for (EnchantmentData enchantmentData : list) {
                                    cluedPairingList.add(new Pair<>(enchantmentData.enchantment, enchantmentData.enchantmentLevel));
                                }

                                holder.setData(j1, cluedPairingList);

                                EnchantmentData enchantmentdata = list.get(this.rand.nextInt(list.size()));
                                this.enchantClue[j1] = Registry.ENCHANTMENT.getId(enchantmentdata.enchantment);
                                this.worldClue[j1] = enchantmentdata.enchantmentLevel;
                            }
                        }
                    }
                    this.enchantClue = EnchantClueHolder.encodeClues(holder, this.enchantClue);
                    TomesPacketHandler.instance.send(PacketDistributor.ALL.noArg(), new ServerEnchantmentClueMessage(this.windowId, this.enchantClue));
                    this.detectAndSendChanges();
                });
            } else {
                for(int i = 0; i < 3; ++i) {
                    this.enchantLevels[i] = 0;
                    this.enchantClue[i] = -1;
                    this.worldClue[i] = -1;
                }
            }
        }

    }

    @Shadow
    protected abstract float getPower(net.minecraft.world.World world, net.minecraft.util.math.BlockPos pos);

    @Shadow
    public abstract List<EnchantmentData> getEnchantmentList(ItemStack stack, int enchantSlot, int level);

}
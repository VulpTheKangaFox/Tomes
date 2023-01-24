package com.vulp.tomes.blocks.tile;

import com.google.common.base.Preconditions;
import com.vulp.tomes.init.RecipeInit;
import com.vulp.tomes.init.TileInit;
import com.vulp.tomes.items.crafting.GobletOfHeartsRecipe;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class GobletOfHeartsTileEntity extends TileEntity implements IClearable {

    private final NonNullList<ItemStack> inventory = NonNullList.withSize(5, ItemStack.EMPTY);
    private GobletOfHeartsRecipe recipe;
    private boolean ready = false;

    public GobletOfHeartsTileEntity() {
        this(TileInit.goblet_of_hearts);
    }

    public GobletOfHeartsTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public void tick() {
        /*boolean flag = this.getBlockState().get(CampfireBlock.LIT);
        boolean flag1 = this.world.isRemote;
        if (flag1) {
            if (flag) {
                this.addParticles();
            }

        } else {
            if (flag) {
                this.cookAndDrop();
            } else {
                for(int i = 0; i < this.inventory.size(); ++i) {
                    if (this.cookingTimes[i] > 0) {
                        this.cookingTimes[i] = MathHelper.clamp(this.cookingTimes[i] - 2, 0, this.cookingTotalTimes[i]);
                    }
                }
            }

        }*/
    }

    private void addParticles() {
        /*World world = this.getWorld();
        if (world != null) {
            BlockPos blockpos = this.getPos();
            Random random = world.rand;
            if (random.nextFloat() < 0.11F) {
                for(int i = 0; i < random.nextInt(2) + 2; ++i) {
                    CampfireBlock.spawnSmokeParticles(world, blockpos, this.getBlockState().get(CampfireBlock.SIGNAL_FIRE), false);
                }
            }

            int l = this.getBlockState().get(CampfireBlock.FACING).getHorizontalIndex();

            for(int j = 0; j < this.inventory.size(); ++j) {
                if (!this.inventory.get(j).isEmpty() && random.nextFloat() < 0.2F) {
                    Direction direction = Direction.byHorizontalIndex(Math.floorMod(j + l, 4));
                    float f = 0.3125F;
                    double d0 = (double)blockpos.getX() + 0.5D - (double)((float)direction.getXOffset() * 0.3125F) + (double)((float)direction.rotateY().getXOffset() * 0.3125F);
                    double d1 = (double)blockpos.getY() + 0.5D;
                    double d2 = (double)blockpos.getZ() + 0.5D - (double)((float)direction.getZOffset() * 0.3125F) + (double)((float)direction.rotateY().getZOffset() * 0.3125F);

                    for(int k = 0; k < 4; ++k) {
                        world.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 5.0E-4D, 0.0D);
                    }
                }
            }

        }*/
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(pos.add(-1, 0, -1), pos.add(1, 1, 1));
    }

    /**
     * Returns a NonNullList<ItemStack> of items currently held in the campfire.
     */
    public NonNullList<ItemStack> getInventory() {
        return this.inventory;
    }

    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        this.ready = nbt.getBoolean("Ready");
        this.inventory.clear();
        ItemStackHelper.loadAllItems(nbt, this.inventory);
    }

    public CompoundNBT write(CompoundNBT compound) {
        compound.putBoolean("Ready", this.ready);
        this.writeItems(compound);
        return compound;
    }

    private CompoundNBT writeItems(CompoundNBT compound) {
        super.write(compound);
        compound.remove("Items");
        ItemStackHelper.saveAllItems(compound, this.inventory, true);
        return compound;
    }

    @Nullable
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putBoolean("Ready", this.ready);
        ItemStackHelper.saveAllItems(nbt, this.inventory);
        return new SUpdateTileEntityPacket(this.pos, 13, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        super.onDataPacket(net, pkt);
        CompoundNBT nbt = pkt.getNbtCompound();
        this.ready = nbt.getBoolean("Ready");
        NonNullList<ItemStack> temp = NonNullList.withSize(inventory.size(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(nbt, temp);
        Preconditions.checkArgument(temp.size() == inventory.size());
        for (int i = 0; i < temp.size(); i++) {
            inventory.set(i, temp.get(i));
        }
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        ItemStackHelper.loadAllItems(tag, this.inventory);
        this.ready = tag.getBoolean("Ready");
        super.handleUpdateTag(state, tag);
    }

    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    public boolean addItem(ItemStack itemStackIn) {
        for (int i = 0; i < this.inventory.size(); ++i) {
            ItemStack itemstack = this.inventory.get(i);
            if (itemstack.isEmpty()) {
                this.inventory.set(i, itemStackIn.split(1));
                this.inventoryChanged();
                return true;
            }
        }
        return false;
    }

    public boolean removeLastItem() {
        for (int i = this.inventory.size() - 1; i > -1; --i) {
            ItemStack itemstack = this.inventory.get(i);
            if (!itemstack.isEmpty()) {
                BlockPos blockpos = this.getPos();
                Random rand = new Random();
                double d0 = EntityType.ITEM.getWidth();
                double d1 = 1.0D - d0;
                double d2 = d0 / 2.0D;
                double d3 = Math.floor(blockpos.getX()) + rand.nextDouble() * d1 + d2;
                double d4 = blockpos.getY() + d1;
                double d5 = Math.floor(blockpos.getZ()) + rand.nextDouble() * d1 + d2;
                ItemEntity itementity = new ItemEntity(this.world, d3, d4, d5, itemstack.split(rand.nextInt(21) + 10));
                itementity.setMotion(rand.nextGaussian() * (double)0.02F, rand.nextGaussian() * (double)0.05F + (double)0.2F, rand.nextGaussian() * (double)0.02F);
                this.world.addEntity(itementity);

                this.inventory.set(i, ItemStack.EMPTY);
                this.inventoryChanged();
                return true;
            }
        }

        return false;
    }

    public boolean checkCanCraft() {
        Optional<GobletOfHeartsRecipe> optional = this.world.getRecipeManager().getRecipe(RecipeInit.goblet_crafting, new Inventory(this.calcInvArray()), this.world);
        boolean flag = this.calcInvArray().length > 0 && optional.isPresent(); // meant to be >0
        this.ready = flag;
        if (flag) {
            this.recipe = optional.get();
        }
        if (this.getWorld() instanceof ServerWorld) {
            SUpdateTileEntityPacket packet = this.getUpdatePacket();
            if (packet != null) {
                BlockPos pos = this.getPos();
                ((ServerChunkProvider) this.getWorld().getChunkProvider()).chunkManager.getTrackingPlayers(new ChunkPos(pos), false).forEach(e -> e.connection.sendPacket(packet));
            }
        }
        return flag;
    }

    public boolean canCraft() {
        return this.ready;
    }

    public void craftRecipe(PlayerEntity player) {
        if (this.ready) {
            this.recipe.trigger(this.world, this.pos, player); // blah blah blah
            this.clear();
            this.inventoryChanged();
            checkCanCraft();
        }
    }

    private ItemStack[] calcInvArray() {
        AtomicInteger size = new AtomicInteger(this.inventory.size());
        this.inventory.forEach(stack -> {
            if (stack.isEmpty()) {
                size.getAndDecrement();
            }
        });
        ItemStack[] stacks = new ItemStack[size.get()];
        int ticker = 0;
        for (ItemStack itemStack : this.inventory) {
            if (!itemStack.isEmpty()) {
                stacks[ticker] = itemStack;
                ticker++;
            }
        }
        return stacks;
    }

    private void inventoryChanged() {
        if (this.getWorld() instanceof ServerWorld) {
            SUpdateTileEntityPacket packet = this.getUpdatePacket();
            if (packet != null) {
                BlockPos pos = this.getPos();
                ((ServerChunkProvider) this.getWorld().getChunkProvider()).chunkManager.getTrackingPlayers(new ChunkPos(pos), false).forEach(e -> e.connection.sendPacket(packet));
            }
        }
        this.markDirty();
        this.getWorld().notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    public void clear() {
        this.inventory.clear();
    }

    public void dropAllItems() {
        if (this.world != null) {
            if (!this.world.isRemote) {
                InventoryHelper.dropItems(this.world, this.getPos(), this.getInventory());
            }

            this.inventoryChanged();
        }

    }

}

package com.vulp.tomes.blocks;

import com.vulp.tomes.blocks.tile.GobletOfHeartsTileEntity;
import com.vulp.tomes.network.TomesPacketHandler;
import com.vulp.tomes.network.messages.ServerGobletCraftMessage;
import com.vulp.tomes.network.messages.ServerProjDeflectMessage;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class GobletOfHeartsBlock extends ContainerBlock {

    protected static final VoxelShape SHAPE = VoxelShapes.or(VoxelShapes.or(Block.makeCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 2.0D, 11.0D), Block.makeCuboidShape(7.0D, 2.0D, 7.0D, 9.0D, 6.0D, 9.0D)), VoxelShapes.or(Block.makeCuboidShape(5.0D, 6.0D, 5.0D, 11.0D, 11.0D, 11.0D), Block.makeCuboidShape(4.4D, 11.0D, 4.4D, 11.6D, 13.0D, 11.6D)));

    public GobletOfHeartsBlock(Properties builder) {
        super(builder);
    }

    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof GobletOfHeartsTileEntity) {
            GobletOfHeartsTileEntity gobletTileEntity = (GobletOfHeartsTileEntity)tileentity;
            ItemStack itemstack = player.getHeldItem(handIn);
            if (!worldIn.isRemote) {
                if (gobletTileEntity.checkCanCraft() && !player.isSneaking()) {
                    gobletTileEntity.craftRecipe(player);
                    TomesPacketHandler.instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> (Chunk) worldIn.getChunk(pos)), new ServerGobletCraftMessage(pos));
                    return ActionResultType.SUCCESS;
                } else {
                    if (itemstack.isEmpty() && gobletTileEntity.removeLastItem()) {
                        gobletTileEntity.checkCanCraft();
                        return ActionResultType.SUCCESS;
                    } else if (gobletTileEntity.addItem(player.abilities.isCreativeMode ? itemstack.copy() : itemstack)) {
                        gobletTileEntity.checkCanCraft();
                        return ActionResultType.SUCCESS;
                    }
                }
            } else {
                return ActionResultType.CONSUME;
            }
        }

        return ActionResultType.PASS;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new GobletOfHeartsTileEntity();
    }

    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.matchesBlock(newState.getBlock())) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof GobletOfHeartsTileEntity) {
                InventoryHelper.dropItems(worldIn, pos, ((GobletOfHeartsTileEntity)tileentity).getInventory());
            }

            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}

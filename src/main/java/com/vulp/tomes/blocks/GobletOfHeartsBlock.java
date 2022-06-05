package com.vulp.tomes.blocks;

import com.vulp.tomes.blocks.tile.GobletOfHeartsTileEntity;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class GobletOfHeartsBlock extends ContainerBlock {

    protected static final VoxelShape SHAPE = VoxelShapes.or(VoxelShapes.or(Block.makeCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 2.0D, 11.0D), Block.makeCuboidShape(7.0D, 2.0D, 7.0D, 9.0D, 6.0D, 9.0D)), VoxelShapes.or(Block.makeCuboidShape(5.0D, 6.0D, 5.0D, 11.0D, 11.0D, 11.0D), Block.makeCuboidShape(4.4D, 11.0D, 4.4D, 11.6D, 13.0D, 11.6D)));

    public GobletOfHeartsBlock(Properties builder) {
        super(builder);
    }

    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof GobletOfHeartsTileEntity) {
            GobletOfHeartsTileEntity campfiretileentity = (GobletOfHeartsTileEntity)tileentity;
            ItemStack itemstack = player.getHeldItem(handIn);
            if (!worldIn.isRemote) {
                if (campfiretileentity.checkCanCraft() && !player.isSneaking()) {
                    campfiretileentity.craftRecipe(player);
                    return ActionResultType.SUCCESS;
                } else {
                    if (itemstack.isEmpty() && campfiretileentity.removeLastItem()) {
                        campfiretileentity.checkCanCraft();
                        return ActionResultType.SUCCESS;
                    } else if (campfiretileentity.addItem(player.abilities.isCreativeMode ? itemstack.copy() : itemstack)) {
                        campfiretileentity.checkCanCraft();
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

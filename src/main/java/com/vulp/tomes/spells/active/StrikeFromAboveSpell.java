package com.vulp.tomes.spells.active;

import com.vulp.tomes.config.TomesConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;

public class StrikeFromAboveSpell extends ActiveSpell {

    public StrikeFromAboveSpell(Enchantment.Rarity rarity, boolean isActive, boolean isRare, ForgeConfigSpec.ConfigValue<Boolean> enabled) {
        super(rarity, isActive, isRare, enabled);
    }

    @Override
    public int getSpellCost() {
        return TomesConfig.strike_from_above_cost.get();
    }

    @Override
    public boolean onCast(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (!worldIn.isRemote) {

            Vector3d startPoint = playerIn.getEyePosition(0);
            RayTraceContext raytraceContext = new RayTraceContext(startPoint, startPoint.add(playerIn.getLookVec().scale(100.0D)), RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.ANY, null);
            BlockRayTraceResult rayHit = worldIn.rayTraceBlocks(raytraceContext);

            if (rayHit.getType() == RayTraceResult.Type.BLOCK) {
                BlockPos pos = rayHit.getPos();
                EntityType.LIGHTNING_BOLT.spawn(((ServerPlayerEntity)playerIn).getServerWorld(), null, null, playerIn, pos, SpawnReason.MOB_SUMMONED, true, false);
                return true;
            }
        } return false;
    }

    @Override
    public int getCooldown() {
        return TomesConfig.strike_from_above_cooldown.get();
    }

    @Override
    public boolean canTick() {
        return false;
    }

    @Override
    public void tick(World world, Entity entity) {

    }
}

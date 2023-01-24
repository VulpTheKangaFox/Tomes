package com.vulp.tomes.capabilities;

import com.vulp.tomes.Tomes;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class StarryFormStorage implements Capability.IStorage<IStarryFormReturn> {

    @Nullable
    @Override
    public CompoundNBT writeNBT(Capability<IStarryFormReturn> capability, IStarryFormReturn instance, Direction side) {
        CompoundNBT nbt = new CompoundNBT();
        StarryFormReturnHolder holder = instance.getHolder();
        if (holder != null) {
            BlockPos pos = holder.getStartPos();
            if (pos != null) {
                nbt.putIntArray("Pos", new int[]{pos.getX(), pos.getY(), pos.getZ()});
            }
            RegistryKey<World> dimension = holder.getStartDim();
            if (dimension != null) {
                World.CODEC.encodeStart(NBTDynamicOps.INSTANCE, dimension).resultOrPartial(Tomes.LOGGER::error).ifPresent((p_234668_1_) -> {
                    nbt.put("Dimension", p_234668_1_);
                });
            }
        }
        return nbt;
    }

    @Override
    public void readNBT(Capability<IStarryFormReturn> capability, IStarryFormReturn instance, Direction side, INBT nbt) {
        CompoundNBT compoundNBT = (CompoundNBT) nbt;
        if (!compoundNBT.isEmpty()) {
            int[] pos = null;
            if (compoundNBT.contains("Pos")) {
                pos = compoundNBT.getIntArray("Pos");
            }
            instance.setHolder(new StarryFormReturnHolder(pos == null ? null : new BlockPos(pos[0], pos[1], pos[2]), compoundNBT.contains("Dimension") ? World.CODEC.parse(NBTDynamicOps.INSTANCE, compoundNBT.get("Dimension")).resultOrPartial(Tomes.LOGGER::error).orElse(World.OVERWORLD) : World.OVERWORLD));
        } else {
            instance.setHolder(new StarryFormReturnHolder());
        }
    }

}

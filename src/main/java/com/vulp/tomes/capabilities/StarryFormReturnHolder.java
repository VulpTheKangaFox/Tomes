package com.vulp.tomes.capabilities;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

// This class just acts as a container for all the info passed around.
public class StarryFormReturnHolder {

    private final BlockPos startPos;
    private final RegistryKey<World> startDim;

    public StarryFormReturnHolder() {
        this(null, World.OVERWORLD);
    }

    public StarryFormReturnHolder(BlockPos startPos, RegistryKey<World> startDim) {
        this.startPos = startPos;
        this.startDim = startDim;
    }

    public BlockPos getStartPos() {
        return this.startPos;
    }

    public RegistryKey<World> getStartDim() {
        return this.startDim;
    }

}

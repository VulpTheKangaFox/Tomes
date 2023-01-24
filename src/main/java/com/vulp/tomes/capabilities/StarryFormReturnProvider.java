package com.vulp.tomes.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

public class StarryFormReturnProvider implements ICapabilitySerializable<CompoundNBT>
{
    @CapabilityInject(IStarryFormReturn.class)
    public static final Capability<IStarryFormReturn> CAPABILITY = null;
    private StarryFormReturn cache = null;
    private final LazyOptional<StarryFormReturn> INSTANCE = LazyOptional.of(this::getOrCreateInstance);

    @Nonnull
    private StarryFormReturn getOrCreateInstance() {
        if (this.cache == null) {
            this.cache = new StarryFormReturn();
        }
        return this.cache;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction side) {
        return capability == CAPABILITY ? INSTANCE.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        return (CompoundNBT) CAPABILITY.getStorage().writeNBT(CAPABILITY, this.getOrCreateInstance(), null);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        CAPABILITY.getStorage().readNBT(CAPABILITY, this.getOrCreateInstance(), null, nbt);
    }

}

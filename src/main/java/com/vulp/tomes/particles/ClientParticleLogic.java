package com.vulp.tomes.particles;

import net.minecraft.nbt.CompoundNBT;

// Receiver class for ServerSimpleParticlePassthroughMessage
public class ClientParticleLogic {

    public static void receiveRequest(int ordinal, CompoundNBT nbt) {
        switch (ordinal) {
            case 1:
                dothing(nbt);
                return;
            case 2:
                dothing2(nbt);
                return;
        }
    }

    private static void dothing(CompoundNBT nbt) {

    }

    private static void dothing2(CompoundNBT nbt) {

    }

}

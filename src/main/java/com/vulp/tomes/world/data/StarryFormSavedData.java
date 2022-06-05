package com.vulp.tomes.world.data;

import com.vulp.tomes.effects.StarryFormEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

import java.util.Set;
import java.util.UUID;

public class StarryFormSavedData extends WorldSavedData {

    public StarryFormSavedData() {
        super("starryform");
    }

    @Override
    public void read(CompoundNBT nbt) {
        int length = nbt.getInt("length");
        UUID[] uuids = new UUID[length];
        for (int i = 0; i < length; i++) {
            uuids[i] = nbt.getUniqueId("entityUUIDs" + i);
        }
        StarryFormEffect.UUID_CACHE = uuids;
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        Set<LivingEntity> tracker = StarryFormEffect.getTracker();
        UUID[] uuids = new UUID[tracker.size()];
        int ticker = 0;
        for (LivingEntity entity : tracker) {
            if (entity != null) {
                uuids[ticker] = entity.getUniqueID();
                ticker++;
            }
        }
        compound.putInt("length", ticker - 1);
        for (int i = 0; i < uuids.length; i++) {
            UUID uuid = uuids[i];
            if (uuid != null) {
                compound.putUniqueId("entityUUIDs" + i, uuids[i]);
            }
        }
        return compound;
    }

    public static StarryFormSavedData grabData(ServerWorld server) {
        return server.getChunkProvider().getSavedData().getOrCreate(StarryFormSavedData::new, "starryform");
    }

}

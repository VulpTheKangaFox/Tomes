package com.vulp.tomes.init;

import com.vulp.tomes.Tomes;
import com.vulp.tomes.TomesRegistry;
import com.vulp.tomes.blocks.tile.GobletOfHeartsTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class TileInit {

    public static TileEntityType<GobletOfHeartsTileEntity> goblet_of_hearts;

    public static <T extends TileEntity> TileEntityType<T> register(String id, TileEntityType.Builder<T> builder) {
        TileEntityType<T> type = builder.build(null);
        type.setRegistryName(TomesRegistry.location(id));
        return type;
    }

}

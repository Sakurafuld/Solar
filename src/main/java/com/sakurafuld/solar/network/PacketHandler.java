package com.sakurafuld.solar.network;

import com.sakurafuld.solar.Deets;
import com.sakurafuld.solar.network.solar.ClientboundSolarChunkUpdate;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE
            = NetworkRegistry.newSimpleChannel(new ResourceLocation(Deets.SOLAR, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    public static void initialize() {
        int id = 0;
        INSTANCE.registerMessage(id++, ClientboundSolarChunkUpdate.class, ClientboundSolarChunkUpdate::encode, ClientboundSolarChunkUpdate::decode, ClientboundSolarChunkUpdate::handle);
    }
}

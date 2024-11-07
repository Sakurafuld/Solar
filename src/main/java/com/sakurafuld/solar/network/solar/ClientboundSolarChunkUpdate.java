package com.sakurafuld.solar.network.solar;

import com.sakurafuld.solar.api.capability.SolarLevelChunk;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundSolarChunkUpdate {
    private final BlockPos POS;
    private final boolean ACTIVE;

    public ClientboundSolarChunkUpdate(BlockPos pos, boolean active){
        this.POS = pos;
        this.ACTIVE = active;
    }

    public static void encode(ClientboundSolarChunkUpdate msg, FriendlyByteBuf buf){
        buf.writeBlockPos(msg.POS);
        buf.writeBoolean(msg.ACTIVE);
    }
    public static ClientboundSolarChunkUpdate decode(FriendlyByteBuf buf){
        return new ClientboundSolarChunkUpdate(buf.readBlockPos(), buf.readBoolean());
    }
    public void handle( Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()-> Minecraft.getInstance().level.getChunkAt(this.POS).getCapability(SolarLevelChunk.CAPABILITY).ifPresent(solar ->
                solar.setActive(this.ACTIVE ? this.POS : null)));
        ctx.get().setPacketHandled(true);
    }
}

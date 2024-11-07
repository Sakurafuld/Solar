package com.sakurafuld.solar.api.capability;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.sakurafuld.solar.Deets.*;

@Mod.EventBusSubscriber(modid = SOLAR, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SolarLevelChunk {
    public static Capability<SolarLevelChunk> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    private BlockPos active = null;

    public BlockPos getActive() {
        return active;
    }
    public boolean setActive(BlockPos active) {
        if(active != null && this.isActive())
            return false;

        this.active = active;
        return true;
    }
    public boolean isActive(){
        return this.active != null;
    }

    @Mod.EventBusSubscriber(modid = SOLAR)
    public static class Provider implements ICapabilityProvider {
        private SolarLevelChunk solar = null;
        private final LazyOptional<SolarLevelChunk> CAPABILITY = LazyOptional.of(this::create);

        private SolarLevelChunk create(){
            if(this.solar == null)
                this.solar = new SolarLevelChunk();
            return this.solar;
        }

        @NotNull
        @Override
        public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            if(cap == SolarLevelChunk.CAPABILITY)
                return this.CAPABILITY.cast();
            return LazyOptional.empty();
        }
        @SubscribeEvent
        public static void attach(AttachCapabilitiesEvent<LevelChunk> event) {
            event.addCapability(identifier(SOLAR, "solar"), new Provider());
        }
    }
    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {
        event.register(SolarLevelChunk.class);
    }
}
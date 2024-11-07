package com.sakurafuld.solar.mixin.solar;

import com.sakurafuld.solar.api.capability.SolarLevelChunk;
import mekanism.api.heat.HeatAPI;
import mekanism.common.capabilities.heat.BasicHeatCapacitor;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.evaporation.EvaporationMultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.sakurafuld.solar.Deets.*;

@Mixin(value = EvaporationMultiblockData.class, remap = false)
public abstract class EvaporationMultiblockDataMixin {
    @Unique
    private boolean active = false;
    @Unique
    private boolean last = false;


    @Shadow private double tempMultiplier;

    @Shadow public BasicHeatCapacitor heatCapacitor;

    @Shadow private double biomeAmbientTemp;

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lmekanism/common/content/evaporation/EvaporationMultiblockData;tempMultiplier:D", shift = At.Shift.AFTER), remap = false)
    private void tempMultiplierSolar(Level world, CallbackInfoReturnable<Boolean> cir) {
        EvaporationMultiblockData self = (EvaporationMultiblockData)((Object) this);
        this.active = false;
        for(BlockPos pos : self.locations) {
            self.getHandlerWorld().getChunkAt(pos).getCapability(SolarLevelChunk.CAPABILITY).ifPresent(solar -> {
                if(solar.isActive())
                    this.active = true;
            });
            if(this.active) {
                if(!this.last) {
                    this.last = true;
                }
                this.tempMultiplier = (EvaporationMultiblockData.MAX_MULTIPLIER_TEMP - HeatAPI.AMBIENT_TEMP) * MekanismConfig.general.evaporationTempMultiplier.get() * Config.SOLAR_MULTIPLIER.get();
                this.heatCapacitor.setHeat(Double.MAX_VALUE);
                return;
            }
        }
        if(this.last) {
            this.heatCapacitor.setHeat(this.heatCapacitor.getHeatCapacity() * this.biomeAmbientTemp);
            this.last = false;
        }
    }
}
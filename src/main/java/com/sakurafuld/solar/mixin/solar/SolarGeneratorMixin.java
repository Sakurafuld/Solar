package com.sakurafuld.solar.mixin.solar;

import com.sakurafuld.solar.api.capability.SolarLevelChunk;
import mekanism.api.math.FloatingLong;
import mekanism.generators.common.config.MekanismGeneratorsConfig;
import mekanism.generators.common.tile.TileEntityAdvancedSolarGenerator;
import mekanism.generators.common.tile.TileEntitySolarGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.sakurafuld.solar.Deets.Config;
import static com.sakurafuld.solar.Deets.MAX_BRIGHTNESS;

@Pseudo
@Mixin(value = TileEntitySolarGenerator.class, remap = false)
public abstract class SolarGeneratorMixin {

    @Inject(method = "checkCanSeeSun()Z", at = @At("HEAD"), remap = false, cancellable = true)
    private void check(CallbackInfoReturnable<Boolean> cl) {
        TileEntitySolarGenerator self = (TileEntitySolarGenerator) ((Object) this);
        if(self.hasLevel()){
            self.getLevel().getChunkAt(self.getBlockPos()).getCapability(SolarLevelChunk.CAPABILITY).ifPresent(solar -> {
                if(solar.isActive())
                    cl.setReturnValue(true);
            });
        }

    }

//    protected float multiplier;
//
//    @Inject(method = "recheckSettings()V", at = @At("HEAD"), remap = false)
//    private void calculate(CallbackInfo ci){
//        TileEntitySolarGenerator self = (TileEntitySolarGenerator) ((Object) this);
//        if(self.hasLevel()){
//            Biome b = self.getLevel().getBiomeManager().getBiome(self.getBlockPos()).value();
//            boolean rain = b.getPrecipitation() != Biome.Precipitation.NONE;
//            float tempEff = 0.3F * (0.8F - b.getBaseTemperature());
//            float humidityEff = rain ? -0.3F * b.getDownfall() : 0.0F;
//            multiplier = 1.0F + tempEff + humidityEff;
//        }
//    }

    @Inject(method = "getProduction()Lmekanism/api/math/FloatingLong;", at = @At("HEAD"), cancellable = true,  remap = false)
    private void production(CallbackInfoReturnable<FloatingLong> cir) {
        TileEntitySolarGenerator self = (TileEntitySolarGenerator) ((Object) this);
        if(self.hasLevel()){
            self.getLevel().getChunkAt(self.getBlockPos()).getCapability(SolarLevelChunk.CAPABILITY).ifPresent(solar -> {
                if(solar.isActive()){
                    double brightness = MAX_BRIGHTNESS * Config.SOLAR_MULTIPLIER.get();
                    cir.setReturnValue(MekanismGeneratorsConfig.generators.solarGeneration.get().multiply(brightness));
                }

            });
        }

    }

    @Inject(method = "getMaxOutput()Lmekanism/api/math/FloatingLong;", at = @At("HEAD"), cancellable = true, remap = false)
    protected void getOutPut(CallbackInfoReturnable<FloatingLong> cir) {
        TileEntitySolarGenerator self = (TileEntitySolarGenerator) ((Object) this);
        if(self.hasLevel()){
            self.getLevel().getChunkAt(self.getBlockPos()).getCapability(SolarLevelChunk.CAPABILITY).ifPresent(solar -> {
                if(solar.isActive()){
                    double brightness = MAX_BRIGHTNESS * Config.SOLAR_MULTIPLIER.get();

                    if(TileEntityAdvancedSolarGenerator.class.getName().equals(this.getClass().getName()))
                        cir.setReturnValue(MekanismGeneratorsConfig.generators.advancedSolarGeneration.get().multiply(brightness));
                    else cir.setReturnValue(MekanismGeneratorsConfig.generators.solarGeneration.get().multiply(brightness));
                }
            });
        }
    }
}

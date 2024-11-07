package com.sakurafuld.solar.mixin.solar;

import com.sakurafuld.solar.api.capability.SolarLevelChunk;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DaylightDetectorBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.INVERTED;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.POWER;

@Mixin(DaylightDetectorBlock.class)
public abstract class DaylightDetectorMixin {

    @Inject(method = "updateSignalStrength(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V", at = @At("HEAD"), cancellable = true)
    private static void sunny(BlockState pState, Level pLevel, BlockPos pPos, CallbackInfo ci) {
        pLevel.getChunkAt(pPos).getCapability(SolarLevelChunk.CAPABILITY).ifPresent(solar -> {
            if(solar.isActive()){
                pLevel.setBlock(pPos, pState.setValue(POWER, (pState.getValue(INVERTED) ? 0:15 )), 3);
                ci.cancel();
            }
        });
    }
}

package com.sakurafuld.solar.api;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.BotaniaForgeCapabilities;
import vazkii.botania.api.BotaniaForgeClientCapabilities;
import vazkii.botania.api.block.IExoflameHeatable;
import vazkii.botania.api.block.IHornHarvestable;
import vazkii.botania.api.block.IWandHUD;
import vazkii.botania.api.block.IWandable;
import vazkii.botania.api.mana.IManaCollisionGhost;
import vazkii.botania.api.mana.IManaReceiver;
import vazkii.botania.api.mana.IManaTrigger;
import vazkii.botania.api.mana.spark.ISparkAttachable;

import java.util.HashMap;
import java.util.Map;

public class BlockEntityBotania extends BlockEntity {
    private final Map<Capability<?>, LazyOptional<?>> CAPABILITIES = new HashMap<>();
    protected BlockEntityBotania(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        if(this instanceof IManaReceiver self) this.CAPABILITIES.put(BotaniaForgeCapabilities.MANA_RECEIVER, LazyOptional.of(()-> self));
        if(this instanceof IManaCollisionGhost self) this.CAPABILITIES.put(BotaniaForgeCapabilities.MANA_GHOST, LazyOptional.of(()-> self));
        if(this instanceof IExoflameHeatable self) this.CAPABILITIES.put(BotaniaForgeCapabilities.EXOFLAME_HEATABLE, LazyOptional.of(()-> self));
        if(this instanceof IHornHarvestable self) this.CAPABILITIES.put(BotaniaForgeCapabilities.HORN_HARVEST, LazyOptional.of(()-> self));
        if(this instanceof IManaTrigger self) this.CAPABILITIES.put(BotaniaForgeCapabilities.MANA_TRIGGER, LazyOptional.of(()-> self));
        if(this instanceof ISparkAttachable self) this.CAPABILITIES.put(BotaniaForgeCapabilities.SPARK_ATTACHABLE, LazyOptional.of(()-> self));
        if(this instanceof IWandable self) this.CAPABILITIES.put(BotaniaForgeCapabilities.WANDABLE, LazyOptional.of(()-> self));
        
        if(this instanceof IWandHUD self) this.CAPABILITIES.put(BotaniaForgeClientCapabilities.WAND_HUD, LazyOptional.of(()-> self));
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return CAPABILITIES.containsKey(cap) ? CAPABILITIES.get(cap).cast() : super.getCapability(cap, side);
    }
}

package com.sakurafuld.solar.content;

import com.sakurafuld.solar.Deets;
import com.sakurafuld.solar.content.solar.SolarBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTRY =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, Deets.SOLAR);


    public static final RegistryObject<BlockEntityType<SolarBlockEntity>> SOLAR;


    static {

        SOLAR = REGISTRY.register("solar", ()-> BlockEntityType.Builder.of(SolarBlockEntity::new, ModBlocks.SOLAR.get()).build(null));

    }
}

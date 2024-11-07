package com.sakurafuld.solar;

import com.sakurafuld.solar.content.ModBlockEntities;
import com.sakurafuld.solar.content.ModBlocks;
import com.sakurafuld.solar.content.ModItems;
import com.sakurafuld.solar.network.PacketHandler;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static com.sakurafuld.solar.Deets.*;

@Mod(SOLAR)
public class Solar {
    public Solar() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(this::commonSetup);
        bus.addListener(this::clientSetup);

        ModBlockEntities.REGISTRY.register(bus);
        ModBlocks.REGISTRY.register(bus);

        ModItems.REGISTRY.register(bus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC, "solar.toml");
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(PacketHandler::initialize);
    }
    public void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {

            ItemBlockRenderTypes.setRenderLayer(ModBlocks.SOLAR.get(), RenderType.translucent());

        });
    }
}

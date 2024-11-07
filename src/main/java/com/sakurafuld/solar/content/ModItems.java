package com.sakurafuld.solar.content;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;

import static com.sakurafuld.solar.Deets.SOLAR;
import static com.sakurafuld.solar.Deets.TAB;

public class ModItems {
    public static final DeferredRegister<Item> REGISTRY =
            DeferredRegister.create(ForgeRegistries.ITEMS, SOLAR);

    public static RegistryObject<Item> register(String name){
        return register(name, new Item.Properties().tab(TAB));
    }
    public static RegistryObject<Item> register(String name, Item.Properties prop){
        return REGISTRY.register(name, ()-> new Item(prop));
    }
    public static RegistryObject<Item> register(String name, Function<Item.Properties, ? extends Item> func){
        return REGISTRY.register(name, ()-> func.apply(new Item.Properties().tab(TAB)));
    }
}

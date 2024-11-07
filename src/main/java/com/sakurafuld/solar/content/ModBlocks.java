package com.sakurafuld.solar.content;

import com.sakurafuld.solar.Deets;
import com.sakurafuld.solar.content.solar.SolarBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> REGISTRY =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Deets.SOLAR);

    public static final RegistryObject<Block> SOLAR;

    static {

        SOLAR = register("solar", ()-> new SolarBlock(BlockBehaviour.Properties.of(Material.GLASS).strength(3f, 10f).noOcclusion().requiresCorrectToolForDrops().lightLevel((state) -> state.getValue(BlockStateProperties.LIT) ? 15 : 0)));
    }

    public static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = REGISTRY.register(name, block);
        ModItems.REGISTRY.register(name, () -> new BlockItem(toReturn.get(), new Item.Properties().tab(Deets.TAB)));

        return toReturn;
    }
    public static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block, BiFunction<T, Item.Properties, ? extends BlockItem> item){
        RegistryObject<T> toReturn = REGISTRY.register(name, block);
        ModItems.REGISTRY.register(name, () -> item.apply(toReturn.get(), new Item.Properties().tab(Deets.TAB)));
        return toReturn;
    }
    //タグ定義のバグ防止.
    public static RegistryObject<Block> dummy(String id) {
        return REGISTRY.register(id, () -> new Block(BlockBehaviour.Properties.of(Material.AIR)));
    }
}

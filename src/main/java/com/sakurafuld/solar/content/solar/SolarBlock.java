package com.sakurafuld.solar.content.solar;

import com.sakurafuld.solar.api.capability.SolarLevelChunk;
import com.sakurafuld.solar.content.ModBlockEntities;
import com.sakurafuld.solar.network.PacketHandler;
import com.sakurafuld.solar.network.solar.ClientboundSolarChunkUpdate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.common.block.tile.mana.TilePool;

import static com.sakurafuld.solar.Deets.Config;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT;

public class SolarBlock extends Block implements EntityBlock {

    public SolarBlock(Properties prop) {
        super(prop);
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(LIT);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
    return this.defaultBlockState().setValue(LIT, false);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {

        return Block.box(3, 3, 3, 13, 13, 13);
    }

    @Override
    public void onRemove(BlockState preState, Level level, BlockPos pos, BlockState replacedState, boolean p_60570_) {
        level.getChunkAt(pos).getCapability(SolarLevelChunk.CAPABILITY).ifPresent(solar ->{
            if(solar.isActive() && solar.getActive().equals(pos)){
                solar.setActive(null);
                PacketHandler.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(()-> level.getChunkAt(pos)), new ClientboundSolarChunkUpdate(pos, false));
            }
        });
        super.onRemove(preState, level, pos, replacedState, p_60570_);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntities.SOLAR.get().create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == ModBlockEntities.SOLAR.get() ? SolarBlockEntity::tick : null;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        return TilePool.calculateComparatorLevel(((SolarBlockEntity) world.getBlockEntity(pos)).getCurrentMana(), Config.SOLAR_MAX.get());
    }
}

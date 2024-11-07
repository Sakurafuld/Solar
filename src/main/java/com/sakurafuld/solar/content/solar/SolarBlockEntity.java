package com.sakurafuld.solar.content.solar;

import com.google.common.base.Predicates;
import com.sakurafuld.solar.api.BlockEntityBotania;
import com.sakurafuld.solar.api.capability.SolarLevelChunk;
import com.sakurafuld.solar.content.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.mana.IManaReceiver;
import vazkii.botania.api.mana.spark.IManaSpark;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.mana.spark.SparkHelper;
import vazkii.botania.api.mana.spark.SparkUpgradeType;

import java.util.List;

import static com.sakurafuld.solar.Deets.Config;
import static com.sakurafuld.solar.Deets.required;

public class SolarBlockEntity extends BlockEntityBotania implements IManaReceiver, ISparkAttachable {
    private boolean active;
    private int mana;
    private boolean packet;

    public SolarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SOLAR.get(), pos, state);
        this.active = false;
        this.mana =  0;
        this.packet = false;
    }

    //setRemovedでは絶対にパケットを送ってはいけない(2敗).
    /*@Override
    public void setRemoved() {
        super.setRemoved();
        this.level.getChunkAt(this.worldPosition).getCapability(SolarLevelChunk.SOLAR).ifPresent(solar-> {
            solar.setActive(null);
            PacketHandler.INSTANCE.send(PacketDistributor.DIMENSION.with(this.level::dimension), new S2CSolarCapabilityUpdate(this.worldPosition, false));
        });
    }*/

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Mana", this.mana);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.mana = tag.getInt("Mana");
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Mana", this.mana);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T tile){
        SolarBlockEntity self = ((SolarBlockEntity) tile);
        required(LogicalSide.SERVER).run(()-> level.getChunkAt(pos).getCapability(SolarLevelChunk.CAPABILITY).ifPresent(solar-> {
            boolean old = self.active;
            self.active = self.mana > Config.SOLAR_RATE.get();
            level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.LIT, self.active));
            if(self.active)
                self.receiveMana(-Config.SOLAR_RATE.get());

            if(old != self.active) {
                if(!solar.setActive(self.active ? pos : null)) {
                    level.destroyBlock(pos, true);
                    level.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, Config.SOLAR_EXPLOSION.get().floatValue(), Explosion.BlockInteraction.BREAK);
                }
                VanillaPacketDispatcher.dispatchTEToNearbyPlayers(self);
                self.packet = false;
            }
            if(self.packet && level.getGameTime() % 5 == 0) {
                VanillaPacketDispatcher.dispatchTEToNearbyPlayers(self);
                self.packet = false;
            }
        }));

        IManaSpark spark = self.getAttachedSpark();
        if (spark != null)
            SparkHelper.getSparksAround(level, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, spark.getNetwork())
                    .filter((otherSpark) -> spark != otherSpark && otherSpark.getAttachedManaReceiver() instanceof IManaPool)
                    .forEach(otherSpark -> {
                        otherSpark.registerTransfer(spark);
                        if(spark.getUpgrade() == SparkUpgradeType.ISOLATED)
                            otherSpark.getTransfers().remove(spark);
                    });
    }


    @Override
    public Level getManaReceiverLevel() {
        return this.level;
    }
    @Override
    public BlockPos getManaReceiverPos() {
        return this.worldPosition;
    }
    @Override
    public int getCurrentMana() {
        return this.mana;
    }
    @Override
    public boolean isFull() {
        return this.mana >= Config.SOLAR_MAX.get();
    }
    @Override//Server
    public void receiveMana(int mana) {
        int old = this.mana;
        this.mana = Math.max(0, Math.min(this.mana + mana, Config.SOLAR_MAX.get()));
        if(old != this.mana){
            this.setChanged();
            this.packet = true;
        }
    }
    @Override
    public boolean canReceiveManaFromBursts() {
        return true;
    }


    @Override
    public boolean canAttachSpark(ItemStack itemStack) {
        return true;
    }

    @Override
    public int getAvailableSpaceForMana() {
        return Config.SOLAR_MAX.get() - this.mana;
    }

    @Override
    public IManaSpark getAttachedSpark() {
        List<Entity> sparks = this.level.getEntitiesOfClass(Entity.class, new AABB(this.worldPosition.above(), this.worldPosition.above().offset(1, 1, 1)), Predicates.instanceOf(IManaSpark.class));
        if (sparks.size() == 1) {
            Entity e = sparks.get(0);
            return (IManaSpark)e;
        } else {
            return null;
        }
    }

    @Override
    public boolean areIncomingTranfersDone() {
        return false;
    }
}

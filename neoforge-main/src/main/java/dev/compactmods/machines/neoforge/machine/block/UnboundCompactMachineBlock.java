package dev.compactmods.machines.neoforge.machine.block;

import dev.compactmods.machines.api.room.RoomApi;
import dev.compactmods.machines.api.room.RoomTemplate;
import dev.compactmods.machines.LoggingUtil;
import dev.compactmods.machines.api.dimension.MissingDimensionException;
import dev.compactmods.machines.neoforge.machine.MachineItemCreator;
import dev.compactmods.machines.api.room.history.RoomEntryPoint;
import dev.compactmods.machines.api.shrinking.PSDTags;
import dev.compactmods.machines.neoforge.machine.Machines;
import dev.compactmods.machines.neoforge.room.RoomHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UnboundCompactMachineBlock extends CompactMachineBlock implements EntityBlock {
    public UnboundCompactMachineBlock(Properties props) {
        super(props);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        if (level.getBlockEntity(pos) instanceof UnboundCompactMachineEntity be) {
            final var id = be.templateId();
            final var temp = be.template().orElse(RoomTemplate.INVALID_TEMPLATE);

            if (id != null && !temp.equals(RoomTemplate.INVALID_TEMPLATE)) {
                var item = MachineItemCreator.forNewRoom(id, temp);
                be.getExistingData(Machines.Attachments.MACHINE_COLOR).ifPresent(color -> {
                    item.set(Machines.DataComponents.MACHINE_COLOR, color);
                });

                return item;
            }
        }

        return MachineItemCreator.unbound();
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new UnboundCompactMachineEntity(pos, state);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand p_316595_, BlockHitResult p_316140_) {

        if (stack.getItem() instanceof DyeItem dye && !level.isClientSide && level instanceof ServerLevel serverLevel) {
            return tryDyingMachine(serverLevel, pos, player, dye, stack);
        }

        MinecraftServer server = level.getServer();
        if (stack.is(PSDTags.ITEM) && player instanceof ServerPlayer sp) {
            level.getBlockEntity(pos, Machines.BlockEntities.UNBOUND_MACHINE.get()).ifPresent(unboundEntity -> {
                RoomTemplate template = unboundEntity.template().orElse(RoomTemplate.INVALID_TEMPLATE);
                if (!template.equals(RoomTemplate.INVALID_TEMPLATE)) {
                    int color = unboundEntity.getData(Machines.Attachments.MACHINE_COLOR);

                    try {
                        // Generate a new machine room
                        final var newRoom = RoomApi.newRoom(server, template, sp.getUUID());

                        // Change into a bound machine block
                        level.setBlock(pos, Machines.Blocks.BOUND_MACHINE.get().defaultBlockState(), Block.UPDATE_ALL);

                        // Set up binding and enter
                        level.getBlockEntity(pos, Machines.BlockEntities.MACHINE.get()).ifPresent(ent -> {
                            ent.setConnectedRoom(newRoom.code());
                            ent.setData(Machines.Attachments.MACHINE_COLOR, color);

                            try {
                                RoomHelper.teleportPlayerIntoRoom(server, sp, newRoom, RoomEntryPoint.playerEnteringMachine(player));
                            } catch (MissingDimensionException e) {
                                throw new RuntimeException(e);
                            }
                        });

                    } catch (MissingDimensionException e) {
                        LoggingUtil.modLog().error("Error occurred while generating new room and machine info for first player entry.", e);
                    }
                } else {
                    LoggingUtil.modLog().fatal("Tried to create and enter an invalidly-registered room. Something went very wrong!");
                }
            });
        }

        return super.useItemOn(stack, state, level, pos, player, p_316595_, p_316140_);
    }
}

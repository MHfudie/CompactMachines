package dev.compactmods.machines.machine.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.compactmods.machines.api.CompactMachines;
import dev.compactmods.machines.api.machine.block.IBoundCompactMachineBlockEntity;
import dev.compactmods.machines.machine.MachineColors;
import dev.compactmods.machines.machine.Machines;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class BoundCompactMachineBlockEntity extends BlockEntity implements IBoundCompactMachineBlockEntity {

   protected UUID owner;
   private String roomCode;

   @Nullable
   private Component customName;

   public BoundCompactMachineBlockEntity(BlockPos pos, BlockState state) {
	  super(Machines.BlockEntities.MACHINE.get(), pos, state);
   }

	@Override
	protected void applyImplicitComponents(DataComponentInput components) {
		super.applyImplicitComponents(components);
		this.roomCode = components.get(Machines.DataComponents.BOUND_ROOM_CODE);

		final var desiredColor = components.get(Machines.DataComponents.MACHINE_COLOR);
		if (desiredColor != null) {
			this.setData(Machines.Attachments.MACHINE_COLOR, desiredColor);
		}
	}

	@Override
	protected void collectImplicitComponents(DataComponentMap.Builder builder) {
		super.collectImplicitComponents(builder);
		builder.set(Machines.DataComponents.BOUND_ROOM_CODE, this.roomCode);
		builder.set(Machines.DataComponents.MACHINE_COLOR, this.getData(Machines.Attachments.MACHINE_COLOR));
	}

	@Override
	public void removeComponentsFromTag(CompoundTag tag) {
		super.removeComponentsFromTag(tag);
		tag.remove(Machines.DataComponents.KEY_ROOM_CODE);
		tag.remove(Machines.DataComponents.KEY_MACHINE_COLOR);
	}

   @Override
   protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider holders) {
	  super.loadAdditional(nbt, holders);
	  if (nbt.contains(NBT_ROOM_CODE)) {
		 this.roomCode = nbt.getString(NBT_ROOM_CODE);
	  }

	  if (nbt.contains(NBT_OWNER)) {
		 owner = nbt.getUUID(NBT_OWNER);
	  } else {
		 owner = null;
	  }
   }

   @Override
   protected void saveAdditional(@NotNull CompoundTag nbt, HolderLookup.Provider holders) {
	  super.saveAdditional(nbt, holders);

	  if (owner != null) {
		 nbt.putUUID(NBT_OWNER, this.owner);
	  }

	  if (roomCode != null)
		 nbt.putString(NBT_ROOM_CODE, roomCode);
   }

   @Override
   public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
	  var data = super.getUpdateTag(provider);
	  saveAdditional(data, provider);

	  if (this.roomCode != null) {
		 // data.putString(ROOM_POS_NBT, room);
		 data.putString(NBT_ROOM_CODE, roomCode);
	  }

	  if (this.owner != null)
		 data.putUUID("owner", this.owner);

	  return data;
   }

   @Override
   public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider provider) {
	  super.handleUpdateTag(tag, provider);

	  if (tag.contains(NBT_ROOM_CODE))
		 this.roomCode = tag.getString(NBT_ROOM_CODE);

	  if (tag.contains("players")) {
		 CompoundTag players = tag.getCompound("players");
		 // playerData = CompactMachinePlayerData.fromNBT(players);

	  }

	  if (tag.contains("owner"))
		 owner = tag.getUUID("owner");
   }

   public Optional<UUID> getOwnerUUID() {
	  return Optional.ofNullable(this.owner);
   }

   public void setOwner(UUID owner) {
	  this.owner = owner;
   }

   public boolean hasPlayersInside() {
	  // TODO
	  return false;
   }

   public GlobalPos getLevelPosition() {
	  return GlobalPos.of(level.dimension(), worldPosition);
   }

   public void setConnectedRoom(String roomCode) {
	  if (level != null && !level.isClientSide()) {
		 this.roomCode = roomCode;

		 CompactMachines.room(roomCode).ifPresentOrElse(inst -> {
				this.setData(Machines.Attachments.MACHINE_COLOR, inst.defaultMachineColor());
			 },
			 () -> {
				this.setData(Machines.Attachments.MACHINE_COLOR, MachineColors.WHITE);
			 });

		 this.setChanged();
	  }
   }

   @NotNull
   public String connectedRoom() {
	  return roomCode;
   }

   public Optional<Component> getCustomName() {
	  return Optional.ofNullable(customName);
   }

   public void setCustomName(Component customName) {
	  this.customName = customName;
	  this.setChanged();
   }
}

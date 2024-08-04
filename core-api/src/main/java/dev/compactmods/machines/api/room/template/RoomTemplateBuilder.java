package dev.compactmods.machines.api.room.template;

import com.google.common.collect.ImmutableList;
import dev.compactmods.machines.api.machine.MachineColor;
import dev.compactmods.machines.api.room.RoomDimensions;
import dev.compactmods.machines.api.room.RoomStructureInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonColors;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Optional;

public class RoomTemplateBuilder {

	private RoomDimensions sizeInternal;
	private MachineColor defaultMachineColor;
	private ImmutableList.Builder<RoomStructureInfo> structures = ImmutableList.builder();

	@Nullable
	private BlockState floor;

	RoomTemplateBuilder() {
		this.sizeInternal = RoomDimensions.cubic(3);
		this.defaultMachineColor = MachineColor.fromARGB(CommonColors.WHITE);
	}

	public RoomTemplateBuilder withInternalSizeCubic(int size) {
		this.sizeInternal = RoomDimensions.cubic(size);
		return this;
	}

	public RoomTemplateBuilder withInternalSize(int width, int depth, int height) {
		this.sizeInternal = new RoomDimensions(width, depth, height);
		return this;
	}

	public RoomTemplateBuilder withInternalSize(RoomDimensions internalSize) {
		this.sizeInternal = internalSize;
		return this;
	}

	public RoomTemplateBuilder defaultMachineColor(MachineColor color) {
		this.defaultMachineColor = color;
		return this;
	}

	public RoomTemplateBuilder addStructure(ResourceLocation template, RoomStructureInfo.RoomStructurePlacement placement) {
		this.structures.add(new RoomStructureInfo(template, placement));
		return this;
	}

	public RoomTemplateBuilder withFloor(BlockState floor) {
		this.floor = floor;
		return this;
	}

	public RoomTemplate build() {
		return new RoomTemplate(sizeInternal, defaultMachineColor, structures.build(), Optional.ofNullable(floor));
	}
}

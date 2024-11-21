package dev.compactmods.machines.datagen.base.tags;

import dev.compactmods.machines.api.CompactMachines;
import dev.compactmods.machines.api.machine.MachineConstants;
import dev.compactmods.machines.dimension.Dimension;
import dev.compactmods.machines.machine.Machines;
import dev.compactmods.machines.room.Rooms;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class BlockTagGenerator extends BlockTagsProvider {

    public BlockTagGenerator(PackOutput packOut, ExistingFileHelper files, CompletableFuture<HolderLookup.Provider> lookup) {
        super(packOut, lookup, CompactMachines.MOD_ID, files);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        final var breakableWall = Rooms.Blocks.BREAKABLE_WALL.get();
        final var solidWall = Rooms.Blocks.SOLID_WALL.get();
        final var boundMachine = Machines.Blocks.BOUND_MACHINE.get();
        final var unboundMachine = Machines.Blocks.UNBOUND_MACHINE.get();
        final var voidAir = Dimension.BLOCK_MACHINE_VOID_AIR.get();

        tag(MachineConstants.MACHINE_BLOCK)
                .add(boundMachine, unboundMachine);

        tag(MachineConstants.UNBOUND_MACHINE_BLOCK)
                .add(unboundMachine);

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(breakableWall)
                .add(boundMachine, unboundMachine);

        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(breakableWall)
                .add(boundMachine, unboundMachine);

        tag(Tags.Blocks.RELOCATION_NOT_SUPPORTED)
                .add(boundMachine)
                .add(solidWall)
                .add(voidAir);
    }
}

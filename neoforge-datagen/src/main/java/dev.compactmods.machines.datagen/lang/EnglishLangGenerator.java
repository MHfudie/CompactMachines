package dev.compactmods.machines.datagen.lang;

import dev.compactmods.machines.api.CompactMachines;
import dev.compactmods.machines.api.Translations;
import dev.compactmods.machines.api.advancement.Advancements;
import dev.compactmods.machines.api.command.CommandTranslations;
import dev.compactmods.machines.api.machine.MachineTranslations;
import dev.compactmods.machines.api.room.RoomTranslations;
import dev.compactmods.machines.client.keybinds.room.RoomExitKeyMapping;
import dev.compactmods.machines.client.creative.CreativeTabs;
import dev.compactmods.machines.datagen.lang.BaseLangGenerator;
import dev.compactmods.machines.room.Rooms;
import dev.compactmods.machines.shrinking.Shrinking;
import net.minecraft.Util;
import net.minecraft.data.DataGenerator;

public class EnglishLangGenerator extends BaseLangGenerator {
    public EnglishLangGenerator(DataGenerator gen) {
        super(gen, "en_us");
    }

    @Override
    protected void addTranslations() {
        super.addTranslations();

        blocksAndItems();

        add(Translations.IDs.HOW_DID_YOU_GET_HERE, "How did you get here?!");
        add(Translations.IDs.TELEPORT_OUT_OF_BOUNDS, "An otherworldly force prevents your teleportation.");

        // Machine Translations
        add(MachineTranslations.IDs.OWNER, "Owner: %s");
        add(MachineTranslations.IDs.SIZE, "Internal Size: %1$s");
        add(MachineTranslations.IDs.BOUND_TO, "Bound to: %1$s");
        add(MachineTranslations.IDs.NEW_MACHINE, "New Machine");

        // Room Translations
        add(RoomTranslations.IDs.ROOM_SPAWNPOINT_SET, "New spawn point set.");
        add(RoomTranslations.IDs.MACHINE_ROOM_INFO, "Machine at %1$s is bound to a %2$s size room at %3$s");
        add(RoomTranslations.IDs.PLAYER_ROOM_INFO, "Player '%1$s' is inside room %2$s.");

        // Room Errors
        add(RoomTranslations.IDs.Errors.CANNOT_ENTER_ROOM, "You fumble with the shrinking device, to no avail. It refuses to work.");
        add(RoomTranslations.IDs.Errors.UNKNOWN_ROOM_BY_CODE, "Room [%s] could not be found.");

        commands();
        advancements();

        add(CompactMachines.MOD_ID + ".direction.side", "Side: %s");
        add(CompactMachines.MOD_ID + ".connected_block", "Connected: %s");

        add(Translations.IDs.UNBREAKABLE_BLOCK, "Warning! Unbreakable for non-creative players!");
        add(Translations.IDs.HINT_HOLD_SHIFT, "Hold shift for details.");

        addCreativeTab(CreativeTabs.MAIN_RL, "Compact Machines");

        add("biome." + CompactMachines.MOD_ID + ".machine", "Compact Machine");

        add("jei.compactmachines.machines", "Machines are used to make pocket dimensions. Craft a machine and place it in world, then use a Personal Shrinking Device to go inside.");
        add("jei.compactmachines.shrinking_device", "Use the Personal Shrinking Device (PSD) on a machine in order to enter a compact space.");
        // add("death.attack." + VoidAirBlock.DAMAGE_SOURCE.msgId, "%1$s failed to enter the void");

        add("curios.identifier.psd", "Personal Shrinking Device");

        // add(MachineRoomUpgrades.WORKBENCH_BLOCK.get(), "Workbench");
        add("entity.minecraft.villager.compactmachines.tinkerer", "Spatial Tinkerer");

        add(RoomExitKeyMapping.I18n.CATEGORY, "Compact Machines");
        add(RoomExitKeyMapping.I18n.NAME, "Quick-Exit Compact Machine");

        addJade();
    }

    private void blocksAndItems() {
        final var machineTranslation = getMachineTranslation();
        add("machine.compactmachines.tiny", "%s (%s)".formatted(machineTranslation, "Tiny"));
        add("machine.compactmachines.small", "%s (%s)".formatted(machineTranslation, "Small"));
        add("machine.compactmachines.normal", "%s (%s)".formatted(machineTranslation, "Normal"));
        add("machine.compactmachines.large", "%s (%s)".formatted(machineTranslation, "Large"));
        add("machine.compactmachines.giant", "%s (%s)".formatted(machineTranslation, "Giant"));
        add("machine.compactmachines.colossal", "%s (%s)".formatted(machineTranslation, "Colossal"));

        addBlock(Rooms.Blocks.BREAKABLE_WALL, "Compact Machine Wall");
        addBlock(Rooms.Blocks.SOLID_WALL, "Solid Compact Machine Wall");

        addItem(Shrinking.PERSONAL_SHRINKING_DEVICE, "Personal Shrinking Device");
        addItem(Shrinking.SHRINKING_MODULE, "Atom Shrinking Module");
        addItem(Shrinking.ENLARGING_MODULE, "Atom Enlarging Module");
        add(Util.makeDescriptionId("block", CompactMachines.modRL("bound_machine_fallback")), machineTranslation);
    }

    protected void advancements() {
        advancement(Advancements.ROOT, "Compact Machines", "");
        advancement(Advancements.FOUNDATIONS, "Foundations", "Obtain a breakable wall block.");
        advancement(Advancements.GOT_SHRINKING_DEVICE, "Personal Shrinking Device", "Obtain a Personal Shrinking Device");
        advancement(Advancements.HOW_DID_YOU_GET_HERE, "How Did You Get Here?!", "Which machine is the player in?!");
        advancement(Advancements.RECURSIVE_ROOMS, "Recursive Rooms", "To understand recursion, you must first understand recursion.");
    }

    private void commands() {
        add(CommandTranslations.IDs.CANNOT_GIVE_MACHINE, "Failed to give a new machine to player.");
        add(CommandTranslations.IDs.MACHINE_GIVEN, "Created a new machine item and gave it to %s.");
        add(CommandTranslations.IDs.ROOM_COUNT, "Number of registered rooms: %s");
        add(CommandTranslations.IDs.SPAWN_CHANGED_SUCCESSFULLY, "Spawn point for room [%s] was changed successfully.");
    }

    private void addJade() {
        add("config.jade.plugin_compactmachines.bound_machine", "Bound Compact Machines");
        add("config.jade.plugin_compactmachines.show_owner", "Show Machine Owners");
    }
}

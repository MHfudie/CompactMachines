package dev.compactmods.machines.i18n;

import dev.compactmods.machines.api.CompactMachines;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.function.Function;
import java.util.function.Supplier;

public interface CommandTranslations {

    Supplier<Component> CANNOT_GIVE_MACHINE = () -> Component.translatableWithFallback(IDs.CANNOT_GIVE_MACHINE, "Cannot give machine.");

    Function<Player, Component> MACHINE_GIVEN = (player) -> Component
            .translatableWithFallback(IDs.MACHINE_GIVEN, "Created a new machine item and gave it to %s.", player.getDisplayName());

    interface IDs {
        String CANNOT_GIVE_MACHINE = Util.makeDescriptionId("commands.machines", CompactMachines.modRL("cannot_give_machine_item"));

        String MACHINE_GIVEN = Util.makeDescriptionId("commands.machines", CompactMachines.modRL("machine_given_successfully"));

        /**
         * Used for displaying the number of registered rooms via summary commands.
         */
        String ROOM_COUNT = Util.makeDescriptionId("commands.rooms", CompactMachines.modRL("room_reg_count"));

        String SPAWN_CHANGED_SUCCESSFULLY = Util.makeDescriptionId("commands.rooms", CompactMachines.modRL("spawn_changed_successfully"));
    }
}

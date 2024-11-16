package dev.compactmods.machines.command.subcommand;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.compactmods.machines.LoggingUtil;
import dev.compactmods.machines.api.room.upgrade.RoomUpgradeType;
import dev.compactmods.machines.api.room.upgrade.components.RoomUpgradeList;
import dev.compactmods.machines.command.argument.Suggestors;
import dev.compactmods.machines.feature.CMFeatureFlags;
import dev.compactmods.machines.room.upgrade.RoomUpgrades;
import dev.compactmods.machines.server.ServerConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RoomUpgradesSubcommand {

    private static final Logger LOGGER = LoggingUtil.modLog();

    public static LiteralArgumentBuilder<CommandSourceStack> make() {
        final var subRoot = Commands.literal("upgrades")
                .requires(cs -> CMFeatureFlags.ROOM_UPGRADES.isSubsetOf(cs.enabledFeatures()) && cs.hasPermission(ServerConfig.giveMachineLevel()));

        // /cm upgrades add [id]
        subRoot.then(Commands.literal("add")
                .then(Commands.argument("upgrade", ResourceLocationArgument.id())
                        .suggests(Suggestors.ROOM_UPGRADE_TYPES)
                        .executes(RoomUpgradesSubcommand::applyUpgrade)));

        // /cm upgrades remove [id]
        subRoot.then(Commands.literal("remove")
                .then(Commands.argument("room", StringArgumentType.string())
                        .suggests(Suggestors.ROOM_UPGRADE_TYPES)
                        .executes(RoomUpgradesSubcommand::removeUpgrade)));

        return subRoot;
    }

    private static @Nullable RoomUpgradeType<?> getTargetedUpgradeType(CommandContext<CommandSourceStack> ctx) {
        final var src = ctx.getSource();

        final var upgradeType = ResourceLocationArgument.getId(ctx, "upgrade");
        return src.getServer()
                .registryAccess()
                .registryOrThrow(RoomUpgradeType.REGISTRY_KEY)
                .get(upgradeType);
    }

    private static int applyUpgrade(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        final var player = ctx.getSource().getPlayerOrException();
        var realUpgradeType = getTargetedUpgradeType(ctx);

        if(realUpgradeType == null) return 0;

        var heldItem = player.getMainHandItem();
        var currentUpgrades = heldItem.get(RoomUpgrades.UPGRADE_LIST_COMPONENT);

        if(!realUpgradeType.canApplyTo(heldItem)) {
            ctx.getSource().sendFailure(Component.literal("That upgrade cannot be applied to the held item."));
            return 0;
        }

        if (currentUpgrades != null) {
            var addedList = new ArrayList<>(currentUpgrades.upgrades());

            // TODO: Room Upgrade context (level, itemstack, etc)
            addedList.add(realUpgradeType.constructor().get());

            var newList = new RoomUpgradeList(addedList);
            heldItem.set(RoomUpgrades.UPGRADE_LIST_COMPONENT, newList);
        } else {
            // TODO: Room Upgrade context (level, itemstack, etc)
            var newList = new RoomUpgradeList(List.of(realUpgradeType.constructor().get()));
            heldItem.set(RoomUpgrades.UPGRADE_LIST_COMPONENT, newList);
        }

        return 0;
    }

    private static int removeUpgrade(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        final var player = ctx.getSource().getPlayerOrException();
        var realUpgradeType = getTargetedUpgradeType(ctx);

        var heldItem = player.getMainHandItem();
        var currentUpgrades = heldItem.get(RoomUpgrades.UPGRADE_LIST_COMPONENT);

        if (currentUpgrades != null) {
            var newList = new RoomUpgradeList(currentUpgrades.upgrades());
            newList.upgrades().removeIf(ru -> ru.getType().equals(realUpgradeType));

            heldItem.set(RoomUpgrades.UPGRADE_LIST_COMPONENT, newList);
        }

        return 0;
    }
}


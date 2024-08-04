package dev.compactmods.machines.api.room.template;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.compactmods.machines.api.CompactMachines;
import dev.compactmods.machines.api.machine.MachineColor;
import dev.compactmods.machines.api.machine.MachineTranslations;
import dev.compactmods.machines.api.room.RoomDimensions;
import dev.compactmods.machines.api.room.RoomStructureInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Template structure for creating a new Compact Machine room. These can be added and removed from the registry
 * at any point, so persistent data must be stored outside these instances.
 *
 * @param internalDimensions    The internal dimensions of the room when it is created.
 * @param defaultMachineColor                 The color of the machine blocks created for this template.
 * @param structures            Information used to fill a newly-created room with structures.
 */
public record RoomTemplate(RoomDimensions internalDimensions, MachineColor defaultMachineColor, List<RoomStructureInfo> structures, Optional<BlockState> optionalFloor)
    implements TooltipProvider {

    public static final ResourceKey<Registry<RoomTemplate>> REGISTRY_KEY = ResourceKey.createRegistryKey(CompactMachines.modRL("room_templates"));

    public static final ResourceLocation NO_TEMPLATE = CompactMachines.modRL("empty");

    public static final RoomTemplate INVALID_TEMPLATE = new RoomTemplate(0, 0);

    public static Codec<RoomTemplate> CODEC = RecordCodecBuilder.create(i -> i.group(
            RoomDimensions.CODEC.fieldOf("dimensions").forGetter(RoomTemplate::internalDimensions),
            MachineColor.CODEC.fieldOf("color").forGetter(RoomTemplate::defaultMachineColor),
            RoomStructureInfo.CODEC.listOf().optionalFieldOf("structures", Collections.emptyList())
                    .forGetter(RoomTemplate::structures),
            BlockState.CODEC.optionalFieldOf("floor").forGetter(RoomTemplate::optionalFloor)
    ).apply(i, RoomTemplate::new));

    public static final StreamCodec<? super RegistryFriendlyByteBuf, RoomTemplate> STREAM_CODEC = StreamCodec.composite(
            RoomDimensions.STREAM_CODEC, RoomTemplate::internalDimensions,
            MachineColor.STREAM_CODEC, RoomTemplate::defaultMachineColor,
            RoomStructureInfo.STREAM_CODEC.apply(ByteBufCodecs.list()), RoomTemplate::structures,
            ByteBufCodecs.optional(ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY)), RoomTemplate::optionalFloor,
            RoomTemplate::new
    );

    public static RoomTemplateBuilder builder() {
        return new RoomTemplateBuilder();
    }

    public RoomTemplate(int cubicSizeInternal, int colorARGB) {
        this(RoomDimensions.cubic(cubicSizeInternal), MachineColor.fromARGB(colorARGB), Collections.emptyList(), Optional.empty());
    }

    public AABB getZeroBoundaries() {
        return AABB.ofSize(Vec3.ZERO, internalDimensions.width(), internalDimensions.height(), internalDimensions.depth())
                .inflate(1)
                .move(0, internalDimensions.height() / 2f, 0);
    }

    public AABB getBoundariesCenteredAt(Vec3 center) {
        return AABB.ofSize(center, internalDimensions.width(), internalDimensions.height(), internalDimensions.depth())
                .inflate(1);
    }

    @Override
    public void addToTooltip(Item.TooltipContext ctx, Consumer<Component> tooltips, TooltipFlag flags) {
        final var roomDimensions = internalDimensions();

        tooltips.accept(MachineTranslations.SIZE.apply(roomDimensions.toString()));

        if (!structures().isEmpty()) {
            tooltips.accept(Component.literal("Generates " + structures().size() + " structures after creation.").withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}

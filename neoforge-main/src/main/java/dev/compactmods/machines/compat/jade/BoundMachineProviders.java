//package dev.compactmods.machines.compat.jade;
//
//import com.mojang.authlib.GameProfile;
//import dev.compactmods.machines.api.CompactMachinesApi;
//import dev.compactmods.machines.i18n.MachineTranslations;
//import dev.compactmods.machines.api.room.CompactMachines.roomApi();
//import dev.compactmods.machines.machine.block.BoundCompactMachineBlockEntity;
//import net.minecraft.ChatFormatting;
//import net.minecraft.Util;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.network.chat.Component;
//import net.minecraft.resources.ResourceLocation;
//import snownee.jade.api.BlockAccessor;
//import snownee.jade.api.IBlockComponentProvider;
//import snownee.jade.api.IServerDataProvider;
//import snownee.jade.api.ITooltip;
//import snownee.jade.api.config.IPluginConfig;
//
//public class BoundMachineProviders {
//
//    public static final ResourceLocation UID = CompactMachinesApi.modRL("bound_machine");
//
//    public static final IBlockComponentProvider COMPONENT_PROVIDER = new IBlockComponentProvider() {
//        @Override
//        public void appendTooltip(ITooltip tooltip, BlockAccessor blockAccessor, IPluginConfig config) {
//            final var serverData = blockAccessor.getServerData();
//
//            if (config.get(CompactMachinesApi.modRL("show_owner")) && serverData.contains("owner")) {
//                final var owner = blockAccessor.getLevel().getPlayerByUUID(serverData.getUUID("owner"));
//                if (owner != null) {
//                    GameProfile ownerProfile = owner.getGameProfile();
//                    tooltip.add(Component
//                            .translatable(MachineTranslations.IDs.OWNER, ownerProfile.getName())
//                            .withStyle(ChatFormatting.GRAY));
//                }
//            }
//
//            if (serverData.contains("room_code")) {
//                tooltip.add(Component
//                        .translatable(MachineTranslations.IDs.BOUND_TO, serverData.getString("room_code"))
//                        .withStyle(ChatFormatting.DARK_GRAY));
//            }
//        }
//
//        @Override
//        public ResourceLocation getUid() {
//            return UID;
//        }
//    };
//
//    public static final IServerDataProvider<BlockAccessor> SERVER_DATA = new IServerDataProvider<>() {
//        @Override
//        public void appendServerData(CompoundTag tag, BlockAccessor blockAccessor) {
//            final var player = blockAccessor.getPlayer();
//            if (blockAccessor.getBlockEntity() instanceof BoundCompactMachineBlockEntity machine) {
//                var owner = machine.getOwnerUUID().orElse(Util.NIL_UUID);
//                tag.putUUID("owner", owner);
//
//                CompactMachines.roomApi().room(machine.connectedRoom()).ifPresent(inst -> {
//                    tag.putString("room_code", inst.code());
//                });
//            }
//        }
//
//        @Override
//        public ResourceLocation getUid() {
//            return UID;
//        }
//    };
//}
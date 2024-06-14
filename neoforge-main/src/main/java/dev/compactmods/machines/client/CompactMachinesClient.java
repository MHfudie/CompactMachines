package dev.compactmods.machines.client;

import dev.compactmods.machines.api.CompactMachines;
import dev.compactmods.machines.client.config.ClientConfig;
import dev.compactmods.machines.client.creative.CreativeTabs;
import dev.compactmods.machines.client.machine.MachinesClient;
import dev.compactmods.machines.client.room.RoomsClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(value = CompactMachines.MOD_ID, dist = Dist.CLIENT)
public class CompactMachinesClient {

   public CompactMachinesClient(ModContainer modContainer, IEventBus modBus) {
	  modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.CONFIG);

	  CreativeTabs.prepare();

	  registerEvents(modBus);
   }

   public static void registerEvents(IEventBus modBus) {
	  MachinesClient.registerEvents(modBus);
	  RoomsClient.registerEvents(modBus);
   }
}

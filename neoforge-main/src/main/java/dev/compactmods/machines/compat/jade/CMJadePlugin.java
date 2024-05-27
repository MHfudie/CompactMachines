package dev.compactmods.machines.compat.jade;

import dev.compactmods.machines.api.CompactMachinesApi;
import dev.compactmods.machines.machine.block.BoundCompactMachineBlock;
import dev.compactmods.machines.machine.block.BoundCompactMachineBlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class CMJadePlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(BoundMachineProviders.SERVER_DATA, BoundCompactMachineBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.addConfig(CompactMachinesApi.modRL("show_owner"), false);

        registration.registerBlockComponent(BoundMachineProviders.COMPONENT_PROVIDER, BoundCompactMachineBlock.class);
    }

}

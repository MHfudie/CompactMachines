package com.robotgryphon.compactmachines.block;

import com.robotgryphon.compactmachines.CompactMachines;
import com.robotgryphon.compactmachines.block.tiles.TileEntityMachine;
import com.robotgryphon.compactmachines.reference.EnumMachineSize;
import com.robotgryphon.compactmachines.util.CompactMachineUtil;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

//import org.dave.compactmachines.tile.TileEntityMachine;
//import org.dave.compactmachines.tile.TileEntityRedstoneTunnel;
//import org.dave.compactmachines.tile.TileEntityTunnel;

// TODO TOP Integration
public class BlockCompactMachine extends Block implements IProbeInfoProvider {

    private final EnumMachineSize size;

    public BlockCompactMachine(EnumMachineSize size, Block.Properties props) {
        super(props);
        this.size = size;
    }

    @Override
    public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
        return false;
    }

    @Override
    public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        return 0;
    }

    @Override
    public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
//        TODO Tile Entity
//        if(!(blockAccess.getTileEntity(pos) instanceof TileEntityMachine)) {
//            return 0;
//        }
//
//        TileEntityMachine machine = (TileEntityMachine) blockAccess.getTileEntity(pos);
//        if(machine.isInsideItself()) {
//            return 0;
//        }
//
//        return machine.getRedstonePowerOutput(side.getOpposite());
        return 0;
    }

    @Override
    public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
        super.onNeighborChange(state, world, pos, neighbor);

        if (world.isRemote()) {
            return;
        }

//        TODO Tile Entity
//        if(!(world.getTileEntity(pos) instanceof TileEntityMachine)) {
//            return;
//        }

        // Determine whether it's an immediate neighbor ...
        Direction facing = null;
        for (Direction dir : Direction.values()) {
            if (pos.offset(dir).equals(neighbor)) {
                facing = dir;
                break;
            }
        }

        // And do nothing if it isnt, e.g. diagonal
        if (facing == null) {
            return;
        }

//        TODO Tile Entity and Server Stuff
//        // Make sure we don't stack overflow when we get in a notifyBlockChange loop.
//        // Just ensure only a single notification happens per tick.
//        TileEntityMachine te = (TileEntityMachine) world.getTileEntity(pos);
//        if(te.isInsideItself() || te.alreadyNotifiedOnTick) {
//            return;
//        }
//
//        ServerWorld machineWorld = DimensionTools.getServerMachineWorld();
//        BlockPos neighborPos = te.getConnectedBlockPosition(facing);
//        if(neighborPos != null && machineWorld.getTileEntity(neighborPos) instanceof TileEntityTunnel) {
//            machineWorld.notifyNeighborsOfStateChange(neighborPos, Blockss.tunnel, false);
//            te.alreadyNotifiedOnTick = true;
//        }
//
//        RedstoneTunnelData tunnelData = te.getRedstoneTunnelForSide(facing);
//        if(tunnelData != null && !tunnelData.isOutput) {
//            BlockPos redstoneNeighborPos = tunnelData.pos;
//            if(redstoneNeighborPos != null && machineWorld.getTileEntity(redstoneNeighborPos) instanceof TileEntityRedstoneTunnel) {
//                machineWorld.notifyNeighborsOfStateChange(redstoneNeighborPos, Blockss.redstoneTunnel, false);
//            }
//        }
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        Block given = CompactMachineUtil.getMachineBlockBySize(this.size);
        ItemStack stack = new ItemStack(given, 1);

        CompoundNBT nbt = stack.getOrCreateTag();
        nbt.putString("size", this.size.getName());

        stack.setTag(nbt);

        return stack;
    }


//    @Override
//    public String getSpecialName(ItemStack stack) {
//        return this.getStateFromMeta(stack.getItemDamage()).getValue(SIZE).getName();
//    }


    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileEntityMachine();
    }

    @Override
    public void onPlayerDestroy(IWorld world, BlockPos pos, BlockState state) {
        if (world.isRemote()) {
            super.onPlayerDestroy(world, pos, state);
            return;
        }

        if (!(world.getTileEntity(pos) instanceof TileEntityMachine)) {
            return;
        }

        TileEntityMachine te = (TileEntityMachine) world.getTileEntity(pos);
//        WorldSavedDataMachines.INSTANCE.removeMachinePosition(te.coords);
//
//        BlockMachine.spawnItemWithNBT(world, pos, state.get(BlockMachine.SIZE), te);
//
//        ChunkLoadingMachines.unforceChunk(te.coords);

        super.onPlayerDestroy(world, pos, state);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {

        boolean hasProperTile = worldIn.getTileEntity(pos) instanceof TileEntityMachine;
        if (!hasProperTile)
            return;

        TileEntityMachine tile = (TileEntityMachine) worldIn.getTileEntity(pos);

        // The machine already has data for some reason
        if(tile.coords != -1)
            return;

//        if (stack.hasDisplayName()) {
//            tile.setCustomName(stack.getDisplayName());
//        }

        CompoundNBT nbt = stack.getOrCreateTag();

        if (nbt.contains("ownerLeast") && nbt.contains("ownerMost")) {
            tile.setOwner(nbt.getUniqueId("owner"));
        }

        if (!tile.hasOwner() && placer instanceof PlayerEntity) {
            tile.setOwner(placer.getUniqueID());
        }

        tile.markDirty();
    }

//        // TODO: Allow storing of schemas in machines
//        if(stack.hasTag()) {
//            if(stack.getTag().contains("coords")) {
//                int coords = stack.getTag().getInt("coords");
//                if (coords != -1) {
//                    tileEntityMachine.coords = coords;
//                    if(!world.isRemote) {
//                        WorldSavedDataMachines.INSTANCE.addMachinePosition(tileEntityMachine.coords, pos, world.provider.getDimension(), tileEntityMachine.getSize());
//                        StructureTools.setBiomeForCoords(coords, world.getBiome(pos));
//                    }
//                }
//            }
//
//            if(stack.getTag().contains("schema")) {
//                tileEntityMachine.setSchema(stack.getTag().getString("schema"));
//            }
//

//        }
//

//
//        tileEntityMachine.markDirty();
//    }


    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {

        if (player.isSneaking())
            return ActionResultType.SUCCESS;

        if(worldIn.isRemote || player instanceof ClientPlayerEntity)
            return ActionResultType.SUCCESS;

        TileEntity te = worldIn.getTileEntity(pos);
        if(te == null || !(te instanceof TileEntityMachine))
            return ActionResultType.SUCCESS;

        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }

//    @Override
//    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
//        if(player.isSneaking()) {
//            return false;
//        }
//
//        if(world.isRemote || !(player instanceof EntityPlayerMP)) {
//            return true;
//        }
//
//        if(!(world.getTileEntity(pos) instanceof TileEntityMachine)) {
//            return false;
//        }
//
//        TileEntityMachine machine = (TileEntityMachine)world.getTileEntity(pos);
//        ItemStack playerStack = player.getHeldItemMainhand();
//        if(ShrinkingDeviceUtils.isShrinkingDevice(playerStack)) {
//            TeleportationTools.tryToEnterMachine(player, machine);
//            return true;
//        }
//
//        player.openGui(compactmachines.instance, GuiIds.MACHINE_VIEW.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
//        PackageHandler.instance.sendTo(new MessageMachineContent(machine.coords), (EntityPlayerMP)player);
//        PackageHandler.instance.sendTo(new MessageMachineChunk(machine.coords), (EntityPlayerMP)player);
//
//        return true;
//    }


    @Override
    public String getID() {
        return CompactMachines.MODID + ":" + "machine";
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
        String size = this.size.getName();
        probeInfo.text(new TranslationTextComponent("machines.sizes." + size));

//        TileEntity te = world.getTileEntity(data.getPos());
//        if(te instanceof TileEntityMachine) {
//            TileEntityMachine machine = (TileEntityMachine) te;
//            if(machine.isInsideItself()) {
//                String text = TextFormatting.DARK_RED + "{*tooltip.compactmachines.machine.stopitsoaryn*}" + TextFormatting.RESET;
//                probeInfo.horizontal().text(new StringTextComponent(text));
//                return;
//            }
//
//            String nameOrId = "";
//            if(machine.coords < 0 && machine.getCustomName().length() == 0) {
//                nameOrId = "{*tooltip.compactmachines.machine.coords.unused*}";
//            } else if(machine.getCustomName().length() > 0) {
//                nameOrId = machine.getCustomName();
//            } else {
//                nameOrId = "#" + machine.coords;
//            }
//
//            String coords = TextFormatting.GREEN + "{*tooltip.compactmachines.machine.coords*} " + TextFormatting.YELLOW + nameOrId + TextFormatting.RESET;
//            probeInfo.horizontal()
//                    .text(new StringTextComponent(coords));
//
//            if(player.isCreative() && mode == ProbeMode.EXTENDED) {
//                if(machine.hasNewSchema()) {
//                    String schemaName = machine.getSchemaName();
//                    String text = TextFormatting.RED + "{*tooltip.compactmachines.machine.schema*} " + TextFormatting.YELLOW + schemaName + TextFormatting.RESET;
//                    probeInfo.horizontal()
//                            .text(new StringTextComponent(text));
//                }
//            }
//
//            String translate = "enumfacing." + data.getSideHit().name();
//            String text = TextFormatting.YELLOW + "{*" + translate + "*}" + TextFormatting.RESET;
//            probeInfo.horizontal()
//                    .item(new ItemStack(Items.COMPASS))
//                    .text(new StringTextComponent(text));
//
//            ItemStack connectedStack = machine.getConnectedPickBlock(data.getSideHit());
//            if(connectedStack != null && !connectedStack.isEmpty()) {
//                probeInfo.horizontal().item(connectedStack).itemLabel(connectedStack);
//            }
//        }
    }
}

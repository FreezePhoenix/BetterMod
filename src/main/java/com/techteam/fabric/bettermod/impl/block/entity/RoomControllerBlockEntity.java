package com.techteam.fabric.bettermod.impl.block.entity;

import com.techteam.fabric.bettermod.api.block.entity.BetterBlockEntity;
import com.techteam.fabric.bettermod.api.block.entity.loadable.IClientLoadableBlockEntity;
import com.techteam.fabric.bettermod.api.hooks.IForceRender;
import com.techteam.fabric.bettermod.impl.BetterMod;
import com.techteam.fabric.bettermod.impl.client.RoomTracker;
import com.techteam.fabric.bettermod.impl.client.gui.RoomControllerScreenHandler;
import com.techteam.fabric.bettermod.impl.network.BoxUpdatePayload;
import com.techteam.fabric.bettermod.impl.util.Texts;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@EnvironmentInterface(value = EnvType.CLIENT, itf = IClientLoadableBlockEntity.class)
@EnvironmentInterface(value = EnvType.CLIENT, itf = IForceRender.class)
public class RoomControllerBlockEntity extends BetterBlockEntity implements IClientLoadableBlockEntity, IForceRender, ExtendedScreenHandlerFactory<BoxUpdatePayload> {
	public static final Identifier ID = Identifier.of("betterperf", "room_controller");
	public byte minX;
	public byte minY;
	public byte minZ;
	public byte maxX;
	public byte maxY;
	public byte maxZ;

	private BlockState variantState = Blocks.AIR.getDefaultState();

	public RoomControllerBlockEntity(@NotNull BlockPos pos, BlockState state) {
		super(BetterMod.ROOM_CONTROLLER_BLOCK_ENTITY_TYPE, pos, state, 1);
		this.minX = 0;
		this.minY = 0;
		this.minZ = 0;
		this.maxX = 1;
		this.maxY = 1;
		this.maxZ = 1;
	}


	@Override
	public @NotNull ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
		return new RoomControllerScreenHandler(syncId, playerInventory, this);
	}

	@Contract(value = " -> !null",
	          pure = true)
	@Override
	public Text getContainerName() {
		return Texts.ROOM_CONTROLLER;
	}

	@Override
	public void markDirty() {
		super.markDirty();
		if (!variantState.isOf(Block.getBlockFromItem(getStack(0).getItem()))) {
			this.setVariantState(Block.getBlockFromItem(getStack(0).getItem()).getDefaultState());
		}
		if (this.world != null) {
			this.world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), 0);
		}
	}

	private void readFromNBTBB(NbtElement tag) {
		switch (tag) {
			case NbtCompound nbtCompound -> {
				if (nbtCompound.getType("nx") == NbtElement.INT_TYPE) {
					this.minX = (byte) (nbtCompound.getInt("nx") - pos.getX());
					this.minY = (byte) (nbtCompound.getInt("ny") - pos.getY());
					this.minZ = (byte) (nbtCompound.getInt("nz") - pos.getZ());
					this.maxX = (byte) (nbtCompound.getInt("px") - pos.getX());
					this.maxY = (byte) (nbtCompound.getInt("py") - pos.getY());
					this.maxZ = (byte) (nbtCompound.getInt("pz") - pos.getZ());
				} else {
					this.minX = nbtCompound.getByte("nx");
					this.minY = nbtCompound.getByte("ny");
					this.minZ = nbtCompound.getByte("nz");
					this.maxX = nbtCompound.getByte("px");
					this.maxY = nbtCompound.getByte("py");
					this.maxZ = nbtCompound.getByte("pz");
				}
			}
			case NbtByteArray byteArray -> {
				byte[] realByteArray = byteArray.getByteArray();
				this.minX = realByteArray[0];
				this.minY = realByteArray[1];
				this.minZ = realByteArray[2];
				this.maxX = realByteArray[3];
				this.maxY = realByteArray[4];
				this.maxZ = realByteArray[5];
			}
			case null, default -> {

			}
		}
	}

	private @NotNull NbtElement writeToNBTBB() {
		return new NbtByteArray(new byte[]{minX, minY, minZ, maxX, maxY, maxZ});
	}

	public boolean disguised() {
		return !this.getVariantState().isAir();
	}

	public void setBounds(byte minX, byte minY, byte minZ, byte maxX, byte maxY, byte maxZ) {
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
	}

	@Contract(pure = true)
	@Override
	public int getMaxCountPerStack() {
		return 1;
	}

	@Override
	@Nullable
	public Object getRenderData() {
		return this.getVariantState();
	}

	@Contract(pure = true)
	public BlockState getVariantState() {
		return variantState;
	}

	public void setVariantState(@NotNull BlockState state) {
		this.variantState = state;
	}

	@Contract(pure = true)
	@Override
	public boolean isValid(int slot, ItemStack item) {
		if (item.getItem() instanceof BlockItem blockItem && !(blockItem.getBlock() instanceof BlockEntityProvider)) {
			return super.isValid(slot, item);
		}
		return false;
	}

	@Override
	public void readNbt(@NotNull NbtCompound NBT, RegistryWrapper.WrapperLookup registryLookup) {
		super.readNbt(NBT, registryLookup);
		this.readFromNBTBB(NBT.get("room"));
		if (NBT.contains("state")) {
			RegistryWrapper<Block> registryEntryLookup = registryLookup.getWrapperOrThrow(RegistryKeys.BLOCK);
			setVariantState(NbtHelper.toBlockState(registryEntryLookup, NBT.getCompound("state")));
		}
		this.updateRoom();
		this.markDirty();
	}

	public void updateRoom() {
		if (world instanceof ClientWorld) {
			RoomTracker.updateRoom(
					this.getUUID(),
					minX + pos.getX(),
					minY + pos.getY(),
					minZ + pos.getZ(),
					maxX + pos.getX(),
					maxY + pos.getY(),
					maxZ + pos.getZ()
			);
		}
	}

	@Override
	public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
		return this.createNbt(registryLookup);
	}

	@Contract(" -> new")
	@Override
	public @NotNull Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public BoxUpdatePayload getScreenOpeningData(ServerPlayerEntity player) {
		return new BoxUpdatePayload(
				pos,
				new BoxUpdatePayload.Vec3b(minX, minY, minZ),
				new BoxUpdatePayload.Vec3b(maxX, maxY, maxZ),
				this.disguised()
				? Blocks.AIR.getDefaultState()
				: this.getVariantState()
		);
	}

	@Override
	public void writeNbt(@NotNull NbtCompound NBT, RegistryWrapper.WrapperLookup registryLookup) {
		NBT.put("room", this.writeToNBTBB());
		if (this.disguised()) {
			NBT.put("state", NbtHelper.fromBlockState(this.getVariantState()));
		}
		super.writeNbt(NBT, registryLookup);
	}

	@Environment(EnvType.CLIENT)
	public void onClientUnload(World world, BlockPos pos, BlockState state) {
		RoomTracker.removeRoom(this.getUUID());
	}

	@Environment(EnvType.CLIENT)
	public boolean forceRender() {
		return true;
	}
}

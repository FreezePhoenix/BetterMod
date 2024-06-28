package com.techteam.fabric.bettermod.block.entity;

import com.techteam.fabric.bettermod.BetterMod;
import com.techteam.fabric.bettermod.api.block.entity.BetterBlockEntity;
import com.techteam.fabric.bettermod.api.block.entity.loadable.IClientLoadableBlockEntity;
import com.techteam.fabric.bettermod.api.hooks.IForceRender;
import com.techteam.fabric.bettermod.client.BoxPropertyDelegate;
import com.techteam.fabric.bettermod.client.RoomTracker;
import com.techteam.fabric.bettermod.client.gui.RoomControllerScreenHandler;
import com.techteam.fabric.bettermod.util.Texts;
import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.*;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
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
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class RoomControllerBlockEntity extends BetterBlockEntity implements PropertyDelegateHolder, IClientLoadableBlockEntity, IForceRender, ExtendedScreenHandlerFactory<BlockPos> {
	public static final Identifier ID = Identifier.of("betterperf", "room_controller");
	private final @NotNull BoxPropertyDelegate delegate;
	public byte minX;
	public byte minY;
	public byte minZ;
	public byte maxX;
	public byte maxY;
	public byte maxZ;
	private BlockState variantState = BetterMod.ROOM_CONTROLLER_BLOCK.getDefaultState();

	public RoomControllerBlockEntity(@NotNull BlockPos pos, BlockState state) {
		super(BetterMod.ROOM_CONTROLLER_BLOCK_ENTITY_TYPE, pos, state, 1);
		delegate = new BoxPropertyDelegate(this, pos);
		this.minX = 0;
		this.minY = 0;
		this.minZ = 0;
		this.maxX = 1;
		this.maxY = 1;
		this.maxZ = 1;
	}

	@Override
	public void markDirty() {
		super.markDirty();
		this.setVariantState(getVariantState());
	}

	private void readFromNBTBB(NbtElement tag) {
		switch(tag) {
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

	@Override
	public @NotNull ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return new RoomControllerScreenHandler(syncId, playerInventory, ScreenHandlerContext.create(world, pos));
	}

	public boolean disguised() {
		return !getVariantState().isOf(BetterMod.ROOM_CONTROLLER_BLOCK);
	}

	public void setBounds(byte minX, byte minY, byte minZ, byte maxX, byte maxY, byte maxZ) {
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
	}

	@Contract(value = " -> !null",
	          pure = true)
	@Override
	public Text getDisplayName() {
		return Texts.ROOM_CONTROLLER;
	}

	@Contract(pure = true)
	public @NotNull ItemStack getItemStack() {
		return inventory.getStack(0);
	}

	@Contract(pure = true)
	@Override
	public int getMaxCountPerStack() {
		return 1;
	}

	@Contract(pure = true)
	@Override
	public @NotNull BoxPropertyDelegate getPropertyDelegate() {
		return delegate;
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
		if(state.isAir()) {
			this.variantState = BetterMod.ROOM_CONTROLLER_BLOCK.getDefaultState();
		} else {
			this.variantState = state;
		}
	}

	@Contract(pure = true)
	@Override
	public boolean isValid(int slot, ItemStack item) {
		Block b = Block.getBlockFromItem(item.getItem());
		if (b == Blocks.AIR || b instanceof BlockEntityProvider || RenderLayers.getBlockLayer(b.getDefaultState()).isTranslucent()) {
			return false;
		}
		return super.isValid(slot, item);
	}

	@Contract(pure = true)
	@Override
	public void onClientLoad(World world, BlockPos pos, BlockState state) {
		RoomTracker.addRoom(
				this.getUUID(),
				minX + pos.getX(),
				minY + pos.getY(),
				minZ + pos.getZ(),
				maxX + pos.getX(),
				maxY + pos.getY(),
				maxZ + pos.getZ()
		);
	}

	@Override
	public void onClientUnload(World world, BlockPos pos, BlockState state) {
		RoomTracker.removeRoom(this);
	}

	@Override
	public void readNbt(@NotNull NbtCompound NBT, RegistryWrapper.WrapperLookup registryLookup) {
		super.readNbt(NBT, registryLookup);
		NbtElement tag = NBT.get("room");
		readFromNBTBB(tag);
		if (NBT.contains("state")) {
			RegistryWrapper<Block> registryEntryLookup = registryLookup.getWrapperOrThrow(RegistryKeys.BLOCK);
			setVariantState(NbtHelper.toBlockState(registryEntryLookup, NBT.getCompound("state")));
		}
		updateRoom();
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
		return createNbt(registryLookup);
	}


	@Contract(" -> new")
	@Override
	public @NotNull Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
		return pos;
	}

	@Override
	public void writeNbt(@NotNull NbtCompound NBT, RegistryWrapper.WrapperLookup registryLookup) {
		NBT.put("room", writeToNBTBB());
		NBT.put("state", NbtHelper.fromBlockState(variantState));
		super.writeNbt(NBT, registryLookup);
	}
	@Override
	public boolean forceRender() {
		return true;
	}
}

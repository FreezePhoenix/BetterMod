package com.techteam.fabric.bettermod.block.entity;

import com.techteam.fabric.bettermod.BetterMod;
import com.techteam.fabric.bettermod.block.entity.loadable.IClientLoadableBlockEntity;
import com.techteam.fabric.bettermod.block.entity.loadable.LoadableBlockEntity;
import com.techteam.fabric.bettermod.client.BoxPropertyDelegate;
import com.techteam.fabric.bettermod.client.RoomTracker;
import com.techteam.fabric.bettermod.client.gui.RoomControllerScreenHandler;
import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public final class RoomControllerBlockEntity extends LoadableBlockEntity implements SidedInventory, PropertyDelegateHolder, RenderAttachmentBlockEntity, IClientLoadableBlockEntity {
	public static final Identifier ID = new Identifier("betterperf", "room_controller");
	public static final Box CUBE = new Box(0, 0, 0, 1, 1, 1);
	private final @NotNull BoxPropertyDelegate delegate;

	public int minX;
	public int minY;
	public int minZ;
	public int maxX;
	public int maxY;
	public int maxZ;
	private Box bounds;
	private Box relativeBounds;
	private int variant;

	public RoomControllerBlockEntity(@NotNull BlockPos pos, BlockState state) {
		super(BetterMod.ROOM_CONTROLLER_BLOCK_ENTITY_TYPE, pos, state, 1);
		delegate = new BoxPropertyDelegate(this);
		this.minX = pos.getX();
		this.minY = pos.getY();
		this.minZ = pos.getZ();
		this.maxX = pos.getX() + 1;
		this.maxY = pos.getY() + 1;
		this.maxZ = pos.getZ() + 1;

	}

	private @NotNull void readFromNBTBB(@NotNull NbtCompound tag) {
		this.minX = tag.getInt("nx");
		this.minY = tag.getInt("ny");
		this.minZ = tag.getInt("nz");
		this.maxX = tag.getInt("px");
		this.maxY = tag.getInt("py");
		this.maxZ = tag.getInt("pz");
	}

	private @NotNull NbtCompound writeToNBTBB() {
		NbtCompound tag = new NbtCompound();
		tag.putInt("nx", this.minX);
		tag.putInt("ny", this.minY);
		tag.putInt("nz", this.minZ);
		tag.putInt("px", this.maxX);
		tag.putInt("py", this.maxY);
		tag.putInt("pz", this.maxZ);
		return tag;
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		return false;
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, Direction dir) {
		return false;
	}

	@Override
	protected @NotNull ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
		return new RoomControllerScreenHandler(syncId, playerInventory, ScreenHandlerContext.create(world, pos));
	}

	public boolean disguised() {
		return !inventory.get(0).isEmpty();
	}

	@Override
	public int @NotNull [] getAvailableSlots(Direction side) {
		return new int[]{};
	}

	public void setBounds(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
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
	protected Text getContainerName() {
		return Text.of("Room Controller");
	}

	@Contract(pure = true)
	public @NotNull ItemStack getItem() {
		return inventory.get(0);
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
	public Object getRenderAttachmentData() {
		return this.getState();
	}

	public BlockState getState() {
		if (inventory.get(0).isEmpty()) {
			return BetterMod.ROOM_CONTROLLER_BLOCK.getDefaultState();
		} else {
			return Block.getBlockFromItem(inventory.get(0).getItem()).getStateManager().getStates().get(variant);
		}
	}

	@Contract(pure = true)
	public int getVariant() {
		return variant;
	}

	@Contract(mutates = "this")
	public void setVariant(int var) {
		this.variant = var;
	}

	public int getVariants() {
		return Block.getBlockFromItem(inventory.get(0).getItem()).getStateManager().getStates().size();
	}

	@Contract(pure = true)
	@Override
	public boolean isValid(int slot, ItemStack stack) {
		return slot == 0;
	}

	@Contract(pure = true)
	@Override
	public void onClientLoad() {

	}

	@Override
	public void onClientUnload() {
		if (this.hasWorld()) {
			RoomTracker.removeRoom(this);
		}
	}

	@Override
	public void readNbt(@NotNull NbtCompound NBT) {
		super.readNbt(NBT);
		NbtCompound tag = NBT.getCompound("room");
		readFromNBTBB(tag);
		if (NBT.contains("var")) {
			variant = NBT.getInt("var");
		} else {
			variant = 0;
		}
		if (this.hasWorld() && this.getWorld().isClient()) {
			RoomTracker.addRoom(this.getUUID(), minX, minY, minZ, maxX, maxY, maxZ);
		}
	}

	@Override
	public NbtCompound toInitialChunkDataNbt() {
		NbtCompound comp = super.toInitialChunkDataNbt();
		writeNbt(comp);
		return comp;
	}

	@Contract(" -> new")
	@Override
	public @NotNull Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public void writeNbt(@NotNull NbtCompound NBT) {
		super.writeNbt(NBT);
		NBT.put("room", writeToNBTBB());
		NBT.putInt("var", variant);
	}

	@Override
	public void writeScreenOpeningData(ServerPlayerEntity player, @NotNull PacketByteBuf buf) {
		buf.writeBlockPos(pos);
	}
}

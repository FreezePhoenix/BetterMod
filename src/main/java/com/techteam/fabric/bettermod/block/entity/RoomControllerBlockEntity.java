package com.techteam.fabric.bettermod.block.entity;

import com.techteam.fabric.bettermod.BetterMod;
import com.techteam.fabric.bettermod.block.entity.loadable.IClientLoadableBlockEntity;
import com.techteam.fabric.bettermod.block.entity.loadable.LoadableBlockEntity;
import com.techteam.fabric.bettermod.client.BoxPropertyDelegate;
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

import static com.techteam.fabric.bettermod.client.RoomTracker.updateRoom;

public final class RoomControllerBlockEntity extends LoadableBlockEntity implements SidedInventory, PropertyDelegateHolder, RenderAttachmentBlockEntity, IClientLoadableBlockEntity {
	public static final Identifier ID = new Identifier("betterperf", "room_controller");
	public static final Box CUBE = new Box(0, 0, 0, 1, 1, 1);
	private final @NotNull BoxPropertyDelegate delegate;
	private Box bounds;
	private Box relativeBounds;
	private int variant;

	public RoomControllerBlockEntity(@NotNull BlockPos pos, BlockState state) {
		super(BetterMod.ROOM_CONTROLLER_BLOCK_ENTITY_TYPE, pos, state, 1);
		delegate = new BoxPropertyDelegate(this);
		setBounds(CUBE.offset(pos));
	}

	private static @NotNull Box readFromNBTBB(@NotNull NbtCompound tag) {
		int nx = tag.getInt("nx");
		int ny = tag.getInt("ny");
		int nz = tag.getInt("nz");
		int px = tag.getInt("px");
		int py = tag.getInt("py");
		int pz = tag.getInt("pz");
		return new Box(nx, ny, nz, px, py, pz);
	}

	private static @NotNull NbtCompound writeToNBTBB(@NotNull Box vec) {
		NbtCompound tag = new NbtCompound();
		tag.putInt("nx", (int) vec.minX);
		tag.putInt("ny", (int) vec.minY);
		tag.putInt("nz", (int) vec.minZ);
		tag.putInt("px", (int) vec.maxX);
		tag.putInt("py", (int) vec.maxY);
		tag.putInt("pz", (int) vec.maxZ);
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

	public @NotNull Box getBounds() {
		return bounds;
	}

	public void setBounds(@NotNull Box bounds) {
		this.bounds = bounds;
		this.relativeBounds = bounds.offset(-pos.getX(), -pos.getY(), -pos.getZ());
	}

	public void setBounds(int x1, int y1, int z1, int x2, int y2, int z2) {
		setBounds(new Box(x1, y1, z1, x2, y2, z2));
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

	@Contract(pure = true)
	public @NotNull Box getRelativeBounds() {
		return relativeBounds;
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
			updateRoom(this, null);
		}
	}

	@Override
	public void readNbt(@NotNull NbtCompound NBT) {
		super.readNbt(NBT);
		NbtCompound tag = NBT.getCompound("room");
		setBounds(readFromNBTBB(tag));
		if (NBT.contains("var")) {
			variant = NBT.getInt("var");
		} else {
			variant = 0;
		}
		if (this.hasWorld() && this.getWorld().isClient()) {
			updateRoom(this, bounds);
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
		NBT.put("room", writeToNBTBB(getBounds()));
		NBT.putInt("var", variant);
	}

	@Override
	public void writeScreenOpeningData(ServerPlayerEntity player, @NotNull PacketByteBuf buf) {
		buf.writeBlockPos(pos);
	}
}

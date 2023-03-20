package com.techteam.fabric.bettermod.block.entity;

import com.techteam.fabric.bettermod.BetterMod;
import com.techteam.fabric.bettermod.block.entity.loadable.IClientLoadableBlockEntity;
import com.techteam.fabric.bettermod.client.BoxPropertyDelegate;
import com.techteam.fabric.bettermod.client.RoomTracker;
import com.techteam.fabric.bettermod.client.gui.RoomControllerScreenHandler;
import com.techteam.fabric.bettermod.hooks.RenderHooks;
import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.Packet;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public final class RoomControllerBlockEntity extends BetterBlockEntity implements PropertyDelegateHolder, RenderAttachmentBlockEntity, IClientLoadableBlockEntity, RenderHooks.IForceRender {
	public static final Identifier ID = new Identifier("betterperf", "room_controller");
	public static final Box CUBE = new Box(0, 0, 0, 1, 1, 1);
	private final @NotNull BoxPropertyDelegate delegate;

	public byte minX;
	public byte minY;
	public byte minZ;
	public byte maxX;
	public byte maxY;
	public byte maxZ;
	private int variantIndex;
	private BlockState variantState;

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
		this.setVariantIndex(this.variantIndex);
	}

	private void readFromNBTBB(@NotNull NbtCompound tag) {
		if (tag.getType("nx") == NbtElement.INT_TYPE) {
			this.minX = (byte) (tag.getInt("nx") - pos.getX());
			this.minY = (byte) (tag.getInt("ny") - pos.getY());
			this.minZ = (byte) (tag.getInt("nz") - pos.getZ());
			this.maxX = (byte) (tag.getInt("px") - pos.getX());
			this.maxY = (byte) (tag.getInt("py") - pos.getY());
			this.maxZ = (byte) (tag.getInt("pz") - pos.getZ());
		} else {

			this.minX = tag.getByte("nx");
			this.minY = tag.getByte("ny");
			this.minZ = tag.getByte("nz");
			this.maxX = tag.getByte("px");
			this.maxY = tag.getByte("py");
			this.maxZ = tag.getByte("pz");
		}
	}

	private @NotNull NbtCompound writeToNBTBB() {
		NbtCompound tag = new NbtCompound();
		tag.putByte("nx", this.minX);
		tag.putByte("ny", this.minY);
		tag.putByte("nz", this.minZ);
		tag.putByte("px", this.maxX);
		tag.putByte("py", this.maxY);
		tag.putByte("pz", this.maxZ);
		return tag;
	}

	@Override
	public @NotNull ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return new RoomControllerScreenHandler(syncId, playerInventory, ScreenHandlerContext.create(world, pos));
	}

	public boolean disguised() {
		return !inventory.getStack(0).isEmpty();
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
		return Text.of("Room Controller");
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
	public Object getRenderAttachmentData() {
		return this.getState();
	}

	@Contract(pure = true)
	public BlockState getState() {
		return variantState;
	}

	@Contract(pure = true)
	public int getVariantIndex() {
		return variantIndex;
	}

	@Contract(mutates = "this")
	public void setVariantIndex(int var) {
		var = MathHelper.clamp(var, 0, this.getVariants() - 1);
		this.variantIndex = var;

		ItemStack stack = inventory.getStack(0);
		if (stack.isEmpty()) {
			variantState = BetterMod.ROOM_CONTROLLER_BLOCK.getDefaultState();
		} else {
			variantState = Block.getBlockFromItem(getItemStack().getItem())
			                    .getStateManager()
			                    .getStates()
			                    .get(variantIndex);
		}
	}

	public int getVariants() {
		return Block.getBlockFromItem(getItemStack().getItem()).getStateManager().getStates().size();
	}

	@Contract(pure = true)
	@Override
	public boolean isValid(int slot, ItemStack item) {
		Block b = Block.getBlockFromItem(item.getItem());
		if (b == Blocks.AIR || b instanceof BlockEntityProvider || !b.getDefaultState().isOpaque()) {
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
	public void readNbt(@NotNull NbtCompound NBT) {
		super.readNbt(NBT);
		NbtCompound tag = NBT.getCompound("room");
		readFromNBTBB(tag);
		if (NBT.contains("var")) {
			setVariantIndex(NBT.getInt("var"));
		} else {
			setVariantIndex(0);
		}
		if (world != null && world.isClient()) {
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
	public NbtCompound toInitialChunkDataNbt() {
		return createNbt();
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
		NBT.putInt("var", variantIndex);
	}

	@Override
	public void writeScreenOpeningData(ServerPlayerEntity player, @NotNull PacketByteBuf buf) {
		buf.writeBlockPos(pos);
	}

	@Override
	public boolean forceRender() {
		return true;
	}
}

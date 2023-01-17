package com.techteam.fabric.bettermod.block;

import com.techteam.fabric.bettermod.block.entity.BetterBookshelfBlockEntity;
import com.techteam.fabric.bettermod.block.entity.BitHopperBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.Hopper;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BitHopperBlock extends BetterTickingBlock<BitHopperBlockEntity> {
	public static final Identifier ID = new Identifier("bettermod", "bithopper");
	public BitHopperBlock(@NotNull Settings settings) {
		super(settings);
	}
	protected void appendProperties(@NotNull StateManager.Builder<Block, BlockState> builder) {
		builder.add(HopperBlock.FACING);
	}
	@Contract("_, _ -> new")
	@Override
	public @NotNull BitHopperBlockEntity createBlockEntity(@NotNull BlockPos pos, BlockState state) {
		return new BitHopperBlockEntity(pos, state);
	}
	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		Direction direction = ctx.getSide().getOpposite();
		return this.getDefaultState().with(
				HopperBlock.FACING,
				direction.getAxis() == Direction.Axis.Y
				? Direction.DOWN
				: direction
		);
	}
}

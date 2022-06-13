package com.techteam.fabric.bettermod.block;

import com.techteam.fabric.bettermod.block.entity.BetterBookshelfBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class BetterBookshelfBlock extends BetterBlock<BetterBookshelfBlockEntity> {
    public static final Identifier ID = new Identifier("minecraft", "bookshelf");

    public BetterBookshelfBlock(@NotNull Settings settings) {
        super(settings);
    }

    @Contract("_, _ -> new")
	@Override
    public @NotNull BetterBookshelfBlockEntity createBlockEntity(@NotNull BlockPos pos, BlockState state) {
        return new BetterBookshelfBlockEntity(pos, state);
    }
}

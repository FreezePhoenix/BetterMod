package com.techteam.fabric.bettermod.client;

import com.techteam.fabric.bettermod.block.entity.RoomControllerBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public final class RoomControllerEntityRenderer implements BlockEntityRenderer<RoomControllerBlockEntity> {
    @Contract(pure = true)
    public RoomControllerEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super();
    }

    @Override
    public void render(@NotNull RoomControllerBlockEntity blockEntity, float tickDelta, @NotNull MatrixStack matrices, @NotNull VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (!blockEntity.disguised()) {
            int offX = blockEntity.getPos().getX();
            int offY = blockEntity.getPos().getY();
            int offZ = blockEntity.getPos().getZ();
            WorldRenderer.drawBox(
                    matrices,
                    vertexConsumers.getBuffer(RenderLayer.LINES),
                    (blockEntity.minX) - 0.001,
                    (blockEntity.minY) - 0.001,
                    (blockEntity.minZ) - 0.001,
                    (blockEntity.maxX) + 0.001,
                    (blockEntity.maxY) + 0.001,
                    (blockEntity.maxZ) + 0.001,
                    1f,
                    1f,
                    1f,
                    1f
            );
        }
    }
}
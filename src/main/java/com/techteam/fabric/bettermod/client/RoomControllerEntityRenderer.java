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
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public final class RoomControllerEntityRenderer implements BlockEntityRenderer<RoomControllerBlockEntity> {
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
                    (blockEntity.minX - offX) - 0.001,
                    (blockEntity.minY - offY) - 0.001,
                    (blockEntity.minZ - offZ) - 0.001,
                    (blockEntity.maxX - offX) + 0.001,
                    (blockEntity.maxY - offY) + 0.001,
                    (blockEntity.maxZ - offZ) + 0.001,
                    1f,
                    1f,
                    1f,
                    1f
            );
        }
    }
}
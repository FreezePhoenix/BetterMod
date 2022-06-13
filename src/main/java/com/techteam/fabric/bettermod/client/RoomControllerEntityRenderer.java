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
        if (!blockEntity.disguised()
                && blockEntity.getBounds() != null) {
            Box bounds = blockEntity.getRelativeBounds();
            WorldRenderer.drawBox(
                    matrices,
                    vertexConsumers.getBuffer(RenderLayer.LINES),
                    bounds.minX - 0.001,
                    bounds.minY - 0.001,
                    bounds.minZ - 0.001,
                    bounds.maxX + 0.001,
                    bounds.maxY + 0.001,
                    bounds.maxZ + 0.001,
                    1f,
                    1f,
                    1f,
                    1f
            );
        }
    }
}
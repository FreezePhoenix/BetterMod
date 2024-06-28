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
import net.minecraft.enchantment.Enchantment;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public final class RoomControllerEntityRenderer implements BlockEntityRenderer<RoomControllerBlockEntity> {
    @Contract(pure = true)
    public RoomControllerEntityRenderer(BlockEntityRendererFactory.Context ignoredContext) {}


    @Override
    public boolean rendersOutsideBoundingBox(RoomControllerBlockEntity blockEntity) {
        return blockEntity.disguised();
    }

    @Override
    public void render(@NotNull RoomControllerBlockEntity blockEntity, float tickDelta, @NotNull MatrixStack matrices, @NotNull VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (!blockEntity.disguised()) {
            WorldRenderer.drawBox(
                    matrices,
                    vertexConsumers.getBuffer(RenderLayer.getLines()),
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
package com.techteam.fabric.bettermod.client;

import com.techteam.fabric.bettermod.BetterMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockview.v2.FabricBlockView;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public final class RoomControllerModel implements UnbakedModel, BakedModel, FabricBakedModel {
    private static final ConcurrentHashMap<BlockState, BakedModel> cache = new ConcurrentHashMap<>();
    private static final Identifier DEFAULT_BLOCK_MODEL = Identifier.of("minecraft:block/block");
    private static final SpriteIdentifier SPRITE_ID = new SpriteIdentifier(
            PlayerScreenHandler.BLOCK_ATLAS_TEXTURE,
            Identifier.of("betterperf:block/room_controller")
    );
    private Mesh mesh;
    private Sprite SPRITE;
    private ModelTransformation transformation;

    public static BakedModel modelFor(BlockState mimic_state) {
        return cache.computeIfAbsent(
                mimic_state,
                (BlockState state) -> {
                    return MinecraftClient.getInstance()
                                   .getBlockRenderManager()
                                   .getModel(state);
                }
        );
    }
    @Override
    public BakedModel bake(@NotNull Baker loader, @NotNull Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer) {
        // Load the default block model
        JsonUnbakedModel defaultBlockModel = (JsonUnbakedModel) loader.getOrLoadModel(DEFAULT_BLOCK_MODEL);
        // Get its ModelTransformation
        transformation = defaultBlockModel.getTransformations();
        SPRITE = textureGetter.apply(SPRITE_ID);
        Renderer renderer = RendererAccess.INSTANCE.getRenderer();
        MeshBuilder builder = renderer.meshBuilder();
        QuadEmitter emitter = builder.getEmitter();

        for (Direction direction : Direction.values()) {
            emitter.square(
                           direction,
                           0.0f,
                           0.0f,
                           1.0f,
                           1.0f,
                           0.0f
                   )
                   .spriteBake(SPRITE,MutableQuadView.BAKE_LOCK_UV)
                   .color(-1,-1,-1,-1).emit();
        }
        mesh = builder.build();

        return this;
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockRenderView, BlockState blockState, BlockPos blockPos, Supplier<Random> randomSupplier, RenderContext renderContext) {
        if (blockRenderView instanceof FabricBlockView extendedView) {
            Object attachedData = extendedView.getBlockEntityRenderData(blockPos);
            if (attachedData instanceof BlockState mimicState) {
                if (mimicState.getBlock() != BetterMod.ROOM_CONTROLLER_BLOCK) {
                    modelFor(mimicState).emitBlockQuads(blockRenderView,mimicState,blockPos,randomSupplier,renderContext);
                    return;
                }
            }
        }
        mesh.outputTo(renderContext.getEmitter());
    }
    @Override
    public void emitItemQuads(ItemStack itemStack, Supplier<Random> supplier, @NotNull RenderContext renderContext) {
        mesh.outputTo(renderContext.getEmitter());
    }

    @Contract(value = " -> new",
              pure = true)
    @Override
    public @NotNull @Unmodifiable Collection<Identifier> getModelDependencies() {
        return Collections.singletonList(DEFAULT_BLOCK_MODEL);
    }

    @Override
    public void setParents(Function<Identifier, UnbakedModel> modelLoader) {

    }

    @Contract(pure = true)
    @Override
    public @NotNull ModelOverrideList getOverrides() {
        return ModelOverrideList.EMPTY;
    }

    @Contract(pure = true)
    @Override
    public Sprite getParticleSprite() {
        return SPRITE;
    }

    @Contract(pure = true)
    @Override
    public @NotNull @Unmodifiable List<BakedQuad> getQuads(BlockState state, Direction face, Random random) {
        return Collections.emptyList();
    }

    @Override
    public ModelTransformation getTransformation() {
        return transformation;
    }

    @Override
    public boolean hasDepth() {
        return false;
    }

    @Override
    public boolean isBuiltin() {
        return false;
    }

    @Override
    public boolean isSideLit() {
        return false;
    }

    @Override
    public boolean isVanillaAdapter() {
        return false; // False to trigger FabricBakedModel rendering
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true; // we want the block to have a shadow depending on the adjacent blocks
    }
}

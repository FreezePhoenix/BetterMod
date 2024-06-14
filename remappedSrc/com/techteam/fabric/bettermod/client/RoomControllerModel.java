package com.techteam.fabric.bettermod.client;

import com.mojang.datafixers.util.Pair;
import com.techteam.fabric.bettermod.BetterMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
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
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import net.minecraft.util.math.random.Random;
import java.util.Collection;
import java.util.List;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public final class RoomControllerModel implements UnbakedModel, BakedModel, FabricBakedModel {
    private static final Identifier DEFAULT_BLOCK_MODEL = new Identifier("minecraft:block/block");
    private static final SpriteIdentifier SPRITE_ID = new SpriteIdentifier(
            PlayerScreenHandler.BLOCK_ATLAS_TEXTURE,
            new Identifier("betterperf:block/room_controller")
    );
    private Mesh mesh;
    private Sprite SPRITE;
    private ModelTransformation transformation;
    @Override
    public BakedModel bake(@NotNull Baker loader, @NotNull Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
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
                   .spriteBake(
                           0,
                           SPRITE,
                           MutableQuadView.BAKE_LOCK_UV
                   )
                   .spriteColor(
                           0,
                           -1,
                           -1,
                           -1,
                           -1
                   )
                   .emit();
        }
        mesh = builder.build();

        return this;
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockRenderView, BlockState blockState, BlockPos blockPos, Supplier<Random> randomSupplier, RenderContext renderContext) {
        if (blockRenderView instanceof RenderAttachedBlockView extendedView) {
            Object attachedData = extendedView.getBlockEntityRenderAttachment(blockPos);
            if (attachedData instanceof BlockState mimicState) {
                if (mimicState.getBlock() != BetterMod.ROOM_CONTROLLER_BLOCK) {
                    renderContext.bakedModelConsumer()
                                 .accept(RoomControllerModelWrapper.modelFor(mimicState));
                    return;
                }
            }
        }
        renderContext.meshConsumer()
                     .accept(mesh);
    }
    @Override
    public void emitItemQuads(ItemStack itemStack, Supplier<Random> supplier, @NotNull RenderContext renderContext) {
        renderContext.meshConsumer().accept(mesh);
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

    @Override
    public @NotNull ModelOverrideList getOverrides() {
        return ModelOverrideList.EMPTY;
    }

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

    public static class RoomControllerModelWrapper extends ForwardingBakedModel {
        private static final ConcurrentHashMap<BlockState, RoomControllerModelWrapper> cache = new ConcurrentHashMap<>();
        private final BlockState wrapped_state;

        RoomControllerModelWrapper(BlockState state) {
            this.wrapped = MinecraftClient.getInstance()
                                          .getBlockRenderManager()
                                          .getModel(state);
            this.wrapped_state = state;
            BetterMod.LOGGER.info("State was not found in model cache: " + state);
        }

        public static RoomControllerModelWrapper modelFor(BlockState mimic_state) {
            return cache.computeIfAbsent(
                    mimic_state,
                    RoomControllerModelWrapper::new
            );
        }

        @Override
        public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
            ((FabricBakedModel) wrapped).emitBlockQuads(
                    blockView,
                    wrapped_state,
                    pos,
                    randomSupplier,
                    context
            );
        }

        @Override
        public List<BakedQuad> getQuads(BlockState blockState, Direction face, Random rand) {
            return wrapped.getQuads(
                    wrapped_state,
                    face,
                    rand
            );
        }
    }
}

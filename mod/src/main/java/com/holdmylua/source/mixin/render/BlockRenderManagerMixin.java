package com.holdmylua.source.mixin.render;

import com.holdmylua.source.access.AlternateBlockRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.ARGB;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

// PORT-26.1: BlockRenderDispatcher no longer exists; the state->model lookup lives on
// BlockStateModelSet (Minecraft.getModelManager().getBlockStateModelSet()), so the
// AlternateBlockRenderer duck interface is attached there instead.
@Mixin(BlockStateModelSet.class)
public abstract class BlockRenderManagerMixin implements AlternateBlockRenderer {
   @Shadow
   public abstract BlockStateModel get(BlockState state);

   @Unique
   @Override
   public void renderSingleBlockWithEmission(
      BlockState blockState, PoseStack poseStack, SubmitNodeCollector queue, int combinedLight, ClientLevel world, AbstractClientPlayer player
   ) {
      RenderShape renderShape = blockState.getRenderShape();
      if (renderShape != RenderShape.INVISIBLE) {
         combinedLight = LightCoordsUtil.lightCoordsWithEmission(combinedLight, blockState.getLightEmission());
         BlockStateModel blockStateModel = this.get(blockState);
         // PORT-26.1: getTintSource is @Nullable for untinted blocks where the old
         // BlockColors.getColor returned -1 (white).
         BlockTintSource tintSource = Minecraft.getInstance().getBlockColors().getTintSource(blockState, 0);
         int tint = tintSource != null ? tintSource.colorInWorld(blockState, world, player.blockPosition()) : -1;
         float r = (tint >> 16 & 0xFF) / 255.0F;
         float g = (tint >> 8 & 0xFF) / 255.0F;
         float b = (tint & 0xFF) / 255.0F;

         List<BlockStateModelPart> parts = new ArrayList<>();
         blockStateModel.collectParts(RandomSource.create(42L), parts);

         for (BlockStateModelPart blockModelPart : parts) {
            for (Direction direction : Direction.values()) {
               for (BakedQuad bakedQuad : blockModelPart.getQuads(direction)) {
                  this.hmi$renderBakedQuad(bakedQuad, poseStack, queue, r, g, b, combinedLight, blockState);
               }
            }

            for (BakedQuad bakedQuad : blockModelPart.getQuads(null)) {
               this.hmi$renderBakedQuad(bakedQuad, poseStack, queue, r, g, b, combinedLight, blockState);
            }
         }
         // PORT-26.1: SpecialBlockModelRenderer.renderByBlock no longer exists; special
         // block geometry (chests, beds, banners, skulls, ...) flows through the item
         // model pipeline in 26.1 and cannot be re-rendered from here.
      }
   }

   @Unique
   private void hmi$renderBakedQuad(
      BakedQuad bakedQuad, PoseStack poseStack, SubmitNodeCollector queue, float r, float g, float b, int combinedLight, BlockState blockState
   ) {
      if (bakedQuad.materialInfo().isTinted()) {
         r = Math.clamp(r, 0.0F, 1.0F);
         g = Math.clamp(g, 0.0F, 1.0F);
         b = Math.clamp(b, 0.0F, 1.0F);
      } else {
         r = 1.0F;
         g = 1.0F;
         b = 1.0F;
      }

      RenderType usedLayer = bakedQuad.materialInfo().shade() && blockState.getLightEmission() == 0
         ? Sheets.translucentBlockItemSheet()
         : RenderTypes.cutoutMovingBlock();
      int color = ARGB.colorFromFloat(1.0F, r, g, b);
      queue.submitCustomGeometry(poseStack, usedLayer, (pose, consumer) -> {
         QuadInstance quadInstance = new QuadInstance();
         quadInstance.setColor(color);
         quadInstance.setLightCoords(combinedLight);
         quadInstance.setOverlayCoords(OverlayTexture.NO_OVERLAY);
         consumer.putBakedQuad(pose, bakedQuad, quadInstance);
      });
   }
}

package com.holdmylua.source.mixin.render;

import com.holdmylua.source.access.AlternateBlockRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin({BlockRenderDispatcher.class})
public abstract class BlockRenderManagerMixin implements AlternateBlockRenderer {
   @Shadow
   @Final
   private BlockColors blockColors;

   @Shadow
   public abstract BlockStateModel getBlockModel(BlockState var1);

   @Unique
   @Override
   public void renderSingleBlockWithEmission(
      BlockState blockState, PoseStack poseStack, SubmitNodeCollector queue, int combinedLight, ClientLevel world, AbstractClientPlayer player
   ) {
      RenderShape renderShape = blockState.getRenderShape();
      if (renderShape != RenderShape.INVISIBLE) {
         combinedLight = LightTexture.lightCoordsWithEmission(combinedLight, blockState.getLightEmission());
         BlockStateModel blockStateModel = this.getBlockModel(blockState);
         int tint = this.blockColors.getColor(blockState, world, player.blockPosition(), 0);
         float r = (tint >> 16 & 0xFF) / 255.0F;
         float g = (tint >> 8 & 0xFF) / 255.0F;
         float b = (tint & 0xFF) / 255.0F;

         for (BlockModelPart blockModelPart : blockStateModel.collectParts(RandomSource.create(42L))) {
            for (Direction direction : Direction.values()) {
               for (BakedQuad bakedQuad : blockModelPart.getQuads(direction)) {
                  this.hmi$renderBakedQuad(bakedQuad, poseStack, queue, r, g, b, combinedLight, blockState);
               }
            }

            for (BakedQuad bakedQuad : blockModelPart.getQuads(null)) {
               this.hmi$renderBakedQuad(bakedQuad, poseStack, queue, r, g, b, combinedLight, blockState);
            }
         }

         Minecraft.getInstance()
            .getModelManager()
            .specialBlockModelRenderer()
            .renderByBlock(blockState.getBlock(), ItemDisplayContext.NONE, poseStack, queue, combinedLight, OverlayTexture.NO_OVERLAY, 0);
      }
   }

   @Unique
   private void hmi$renderBakedQuad(
      BakedQuad bakedQuad, PoseStack poseStack, SubmitNodeCollector queue, float r, float g, float b, int combinedLight, BlockState blockState
   ) {
      if (bakedQuad.isTinted()) {
         r = Math.clamp(r, 0.0F, 1.0F);
         g = Math.clamp(g, 0.0F, 1.0F);
         b = Math.clamp(b, 0.0F, 1.0F);
      } else {
         r = 1.0F;
         g = 1.0F;
         b = 1.0F;
      }

      RenderType usedLayer = bakedQuad.shade() && blockState.getLightEmission() == 0 ? Sheets.translucentBlockItemSheet() : RenderTypes.cutoutMovingBlock();
      float finalR = r;
      float finalG = g;
      float finalB = b;
      queue.submitCustomGeometry(
         poseStack,
         usedLayer,
         (matricesEntry, consumer) -> consumer.putBulkData(
            matricesEntry,
            bakedQuad,
            new float[]{1.0F, 1.0F, 1.0F, 1.0F},
            finalR,
            finalG,
            finalB,
            1.0F,
            new int[]{combinedLight, combinedLight, combinedLight, combinedLight},
            OverlayTexture.NO_OVERLAY
         )
      );
   }
}

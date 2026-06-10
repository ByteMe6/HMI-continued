package com.holdmylua.source.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model.Simple;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({HangingSignRenderer.class})
public abstract class HangingSignMixin {
   @Inject(
      method = {"submitSpecial"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void render(
      MaterialSet spriteHolder,
      PoseStack matrixStack,
      SubmitNodeCollector orderedRenderCommandQueue,
      int i,
      int j,
      Simple singlePartModel,
      Material spriteIdentifier,
      CallbackInfo ci
   ) {
      matrixStack.pushPose();
      HangingSignRenderer.translateBase(matrixStack, 0.0F);
      matrixStack.scale(1.0F, -1.0F, -1.0F);
      Unit var10002 = Unit.INSTANCE;
      orderedRenderCommandQueue.submitModel(
         singlePartModel,
         var10002,
         matrixStack,
         spriteIdentifier.renderType(singlePartModel::renderType),
         i,
         OverlayTexture.NO_OVERLAY,
         -1,
         spriteHolder.get(spriteIdentifier),
         0,
         (CrumblingOverlay)null
      );
      matrixStack.popPose();
      ci.cancel();
   }
}

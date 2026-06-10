package com.holdmylua.source.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model.Simple;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// PORT-26.1: submitSpecial now takes (SpriteGetter, ..., SpriteId) and no longer
// applies the translateBase/flip transforms itself, so HMI keeps only its original
// delta: suppressing the damage-flash overlay on held hanging signs.
@Mixin(HangingSignRenderer.class)
public abstract class HangingSignMixin {
   @Inject(method = "submitSpecial", at = @At("HEAD"), cancellable = true)
   private static void render(
      SpriteGetter sprites,
      PoseStack poseStack,
      SubmitNodeCollector submitNodeCollector,
      int lightCoords,
      int overlayCoords,
      Simple model,
      SpriteId sprite,
      CallbackInfo ci
   ) {
      submitNodeCollector.submitModel(model, Unit.INSTANCE, poseStack, lightCoords, OverlayTexture.NO_OVERLAY, -1, sprite, sprites, 0, null);
      ci.cancel();
   }
}

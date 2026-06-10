package com.holdmylua.source.mixin.render;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   targets = {"net/minecraft/client/renderer/ItemInHandRenderer$HandRenderSelection"}
)
public abstract class HandRenderTypeMixin {
   @Shadow
   @Mutable
   private boolean renderMainHand;
   @Shadow
   @Mutable
   private boolean renderOffHand;

   @Inject(
      method = {"<init>"},
      at = {@At("RETURN")}
   )
   private void makeBothHandsRenderTrue(String string, int i, boolean renderMainHand, boolean renderOffHand, CallbackInfo ci) {
      this.renderMainHand = true;
      this.renderOffHand = true;
   }
}

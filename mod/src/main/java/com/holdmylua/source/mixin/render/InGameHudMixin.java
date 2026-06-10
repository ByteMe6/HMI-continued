package com.holdmylua.source.mixin.render;

import com.holdmylua.source.global.GlobalsStorage;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Gui.class})
public abstract class InGameHudMixin {
   @Shadow
   public abstract Font getFont();

   @Inject(
      method = {"render"},
      at = {@At("HEAD")}
   )
   private void debugText(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci) {
      if (!GlobalsStorage.debugTextRenderer.get().isEmpty()) {
         int y = 10;

         for (String s : GlobalsStorage.debugTextRenderer.get()) {
            context.drawCenteredString(this.getFont(), s, 10, y, 255);
            y += 10;
         }

         GlobalsStorage.debugTextRenderer.clear();
      }
   }
}

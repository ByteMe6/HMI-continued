package com.holdmylua.source.mixin.client;

import com.holdmylua.source.LuaTestHMI;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GameRenderer.class})
public class GameRendererMixin {
   @Inject(
      method = {"render"},
      at = {@At("HEAD")}
   )
   private void deltaTime(DeltaTracker tickCounter, boolean tick, CallbackInfo ci) {
      float currentTime = (float)GLFW.glfwGetTime();
      LuaTestHMI.deltaTime = currentTime - LuaTestHMI.prevTime;
      LuaTestHMI.prevTime = currentTime;
      if (Minecraft.getInstance().isPaused()) {
         LuaTestHMI.deltaTime = 0.0F;
      } else {
         LuaTestHMI.deltaTime = (float)Math.min(0.05, (double)LuaTestHMI.deltaTime);
      }
   }
}

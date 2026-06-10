package com.holdmylua.source.scripting.custom_api;

import com.holdmylua.source.annotation.Safe;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class KeyBindManager {
   KeyMapping key;

   @Safe
   public boolean isKeyPressed(int keyCode) {
      if (keyCode != 0) {
         long windowHandle = Minecraft.getInstance().getWindow().handle();
         return GLFW.glfwGetKey(windowHandle, keyCode) == 1;
      } else {
         return false;
      }
   }
}

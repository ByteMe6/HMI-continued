package com.holdmylua.source.scripting.script_wrappers;

import com.holdmylua.source.access.CameraAccessor;
import com.holdmylua.source.annotation.Safe;
import net.minecraft.client.Minecraft;

public class C {
   @Safe
   public void setCamPos(double x, double y, double z) {
      if (Minecraft.getInstance().gameRenderer.getMainCamera() instanceof CameraAccessor camera) {
         camera.hMI5_0$setPosValues((float)x, (float)y, (float)z);
      }
   }

   @Safe
   public void setCamRot(double x, double y, double z) {
      if (Minecraft.getInstance().gameRenderer.getMainCamera() instanceof CameraAccessor camera) {
         camera.hMI5_0$setRotationValues((float)x, (float)y, (float)z);
      }
   }
}

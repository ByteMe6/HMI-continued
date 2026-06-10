package com.holdmylua.source.access;

public interface LivingEntityAccessor {
   float hMI5_0$getMainHandSwingProgress(float var1);

   float hMI5_0$getOffHandSwingProgress(float var1);

   boolean hMI5_0$getMHandEvent();

   boolean hMI5_0$getOHandEvent();

   boolean hMI5_0$getMInteract();

   boolean hMI5_0$getOInteract();

   boolean hMI5_0$getBlockBreak();

   void hMI5_0$resetOffHandSwing(boolean var1);

   void hMI5_0$resetMainHandSwing(boolean var1);

   int hMI5_0$getSwingCount();
}

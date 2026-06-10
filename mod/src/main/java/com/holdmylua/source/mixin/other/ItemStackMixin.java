package com.holdmylua.source.mixin.other;

import com.holdmylua.source.access.ItemStackAccessor;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({ItemStack.class})
public class ItemStackMixin implements ItemStackAccessor {
   @Unique
   private int transform = -1;
   @Unique
   private int swingSpeed = 10;
   @Unique
   private int shouldRenderAsBlock = -1;

   @Override
   public void hMI5_0$setTransform(boolean value) {
      this.transform = value ? 1 : 0;
   }

   @Override
   public void hMI5_0$setTransform(int value) {
      this.transform = value;
   }

   @Override
   public int hMI5_0$getTransform() {
      return this.transform;
   }

   @Override
   public void hMI5_0$setSwingSpeed(int value) {
      this.swingSpeed = value;
   }

   @Override
   public int hMI5_0$getSwingSpeed() {
      return this.swingSpeed;
   }

   @Override
   public void hMI5_0$setRenderAsBlock(boolean value) {
      this.shouldRenderAsBlock = value ? 1 : 0;
   }

   @Override
   public int hMI5_0$getRenderAsBlock() {
      return 0;
   }
}

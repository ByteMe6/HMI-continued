package com.holdmylua.source.mixin.player;

import com.holdmylua.source.access.LivingEntityAccessor;
import com.holdmylua.source.global.GlobalsStorage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin({LivingEntity.class})
public abstract class LivingEntityMixin implements LivingEntityAccessor {
   private boolean interactOffhand = false;
   private boolean interactMainHand = false;
   private boolean blockBreaking = false;
   @Unique
   private int offHandSwingTicks;
   @Unique
   private boolean offHandSwinging;
   @Unique
   public float offHandSwingProgress;
   @Unique
   public float lastOffHandSwingProgress;
   @Unique
   private int mainHandSwingTicks;
   @Unique
   private boolean mainHandSwinging;
   @Unique
   public float mainHandSwingProgress;
   @Unique
   public float lastMainHandSwingProgress;
   private int mainSwingCount = 0;
   @Unique
   private boolean swingMHand = false;
   @Unique
   private boolean swingOHand = false;

   @Shadow
   protected abstract int getCurrentSwingDuration();

   @Shadow
   public abstract ItemStack getMainHandItem();

   @Shadow
   public abstract ItemStack getOffhandItem();

   @Override
   public int hMI5_0$getSwingCount() {
      return this.mainSwingCount;
   }

   @Override
   public void hMI5_0$resetOffHandSwing(boolean interact) {
      this.offHandSwingTicks = 0;
      this.offHandSwinging = true;
      this.swingOHand = !this.swingOHand;
      this.interactOffhand = interact;
   }

   @Override
   public void hMI5_0$resetMainHandSwing(boolean interact) {
      this.mainHandSwingTicks = 0;
      this.mainHandSwinging = true;
      this.swingMHand = !this.swingMHand;
      this.mainSwingCount++;
      this.interactMainHand = interact;
      if (Minecraft.getInstance().gameMode.isDestroying()) {
         this.blockBreaking = true;
      }
   }

   @Override
   public float hMI5_0$getMainHandSwingProgress(float tickDelta) {
      float f = this.mainHandSwingProgress - this.lastMainHandSwingProgress;
      if (f < 0.0F) {
         f++;
      }

      return this.lastMainHandSwingProgress + f * tickDelta;
   }

   @Override
   public float hMI5_0$getOffHandSwingProgress(float tickDelta) {
      float f = this.offHandSwingProgress - this.lastOffHandSwingProgress;
      if (f < 0.0F) {
         f++;
      }

      return this.lastOffHandSwingProgress + f * tickDelta;
   }

   @Override
   public boolean hMI5_0$getMInteract() {
      return this.interactMainHand;
   }

   @Override
   public boolean hMI5_0$getOInteract() {
      return this.interactOffhand;
   }

   @Override
   public boolean hMI5_0$getBlockBreak() {
      return this.blockBreaking;
   }

   @Override
   public boolean hMI5_0$getMHandEvent() {
      return this.swingMHand;
   }

   @Override
   public boolean hMI5_0$getOHandEvent() {
      return this.swingOHand;
   }

   @Inject(
      method = {"baseTick"},
      at = {@At("HEAD")}
   )
   private void tick(CallbackInfo ci) {
      this.lastOffHandSwingProgress = this.offHandSwingProgress;
      this.lastMainHandSwingProgress = this.mainHandSwingProgress;
      int i = GlobalsStorage.itemSwingSpeed.getOrDefault(this.getMainHandItem().getItem().toString(), 10);
      if (this.mainHandSwinging) {
         this.mainHandSwingTicks++;
         if (this.mainHandSwingTicks >= i) {
            this.mainHandSwingTicks = 0;
            this.mainHandSwinging = false;
         }
      } else {
         if (this.interactMainHand) {
            this.interactMainHand = false;
         }

         if (this.blockBreaking) {
            this.blockBreaking = false;
         }

         this.mainHandSwingTicks = 0;
      }

      this.mainHandSwingProgress = (float)this.mainHandSwingTicks / i;
      int i2 = GlobalsStorage.itemSwingSpeed.getOrDefault(this.getOffhandItem().getItem().toString(), 10);
      if (this.offHandSwinging) {
         this.offHandSwingTicks++;
         if (this.offHandSwingTicks >= i2) {
            this.offHandSwingTicks = 0;
            this.offHandSwinging = false;
         }
      } else {
         if (this.interactOffhand) {
            this.interactOffhand = false;
         }

         this.offHandSwingTicks = 0;
      }

      this.offHandSwingProgress = (float)this.offHandSwingTicks / i2;
   }

   @Inject(
      method = {"swing(Lnet/minecraft/world/InteractionHand;Z)V"},
      at = {@At("HEAD")}
   )
   private void onSwingHand(InteractionHand hand, boolean fromServerPlayer, CallbackInfo ci) {
      if (hand == InteractionHand.OFF_HAND) {
         int duration = GlobalsStorage.itemSwingSpeed.getOrDefault(this.getOffhandItem().getItem().toString(), 10);
         if (!this.offHandSwinging || this.offHandSwingTicks >= duration / 2 || this.offHandSwingTicks < 0) {
            this.offHandSwingTicks = -1;
            this.offHandSwinging = true;
         }
      } else {
         int duration = GlobalsStorage.itemSwingSpeed.getOrDefault(this.getMainHandItem().getItem().toString(), 10);
         if (!this.mainHandSwinging || this.mainHandSwingTicks >= duration / 2 || this.mainHandSwingTicks < 0) {
            this.mainHandSwingTicks = -1;
            this.mainHandSwinging = true;
            if (Minecraft.getInstance().gameMode.isDestroying()) {
               this.blockBreaking = true;
            }
         }
      }
   }
}

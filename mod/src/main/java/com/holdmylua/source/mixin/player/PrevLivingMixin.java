package com.holdmylua.source.mixin.player;

import com.holdmylua.source.access.LivingEntityAccessor;
import com.holdmylua.source.lua_runtime.LuaScriptCache;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin({LivingEntity.class})
public abstract class PrevLivingMixin implements LivingEntityAccessor {
   @Shadow
   private int swingTime;
   @Shadow
   public float attackAnim;
   @Shadow
   public float oAttackAnim;
   @Shadow
   private boolean swinging;
   @Shadow
   public InteractionHand swingingArm;
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
   private boolean swingMHand = false;
   private boolean swingOHand = false;

   @Shadow
   protected abstract int getCurrentSwingDuration();

   @Shadow
   public abstract void swing(InteractionHand var1);

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
   public boolean hMI5_0$getMHandEvent() {
      return this.swingMHand;
   }

   @Override
   public boolean hMI5_0$getOHandEvent() {
      return this.swingOHand;
   }

   @Inject(
      method = {"swing"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onSwingHand(InteractionHand hand, CallbackInfo ci) {
      if (hand == InteractionHand.OFF_HAND) {
         int duration = LuaScriptCache.swingSpeed;
         if (!this.offHandSwinging || this.offHandSwingTicks >= duration / 2) {
            this.offHandSwingTicks = 0;
            this.offHandSwinging = true;
            this.swingOHand = !this.swingOHand;
         }
      } else {
         int duration = LuaScriptCache.swingSpeed;
         if (!this.mainHandSwinging || this.mainHandSwingTicks >= duration / 2) {
            this.mainHandSwingTicks = 0;
            this.mainHandSwinging = true;
            this.swingMHand = !this.swingMHand;
         }
      }
   }

   @Inject(
      method = {"updateSwingTime"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onTickHandSwing(CallbackInfo ci) {
      this.lastOffHandSwingProgress = this.offHandSwingProgress;
      int offHandDuration = LuaScriptCache.swingSpeed;
      if (this.offHandSwinging) {
         this.offHandSwingTicks++;
         if (this.offHandSwingTicks >= offHandDuration) {
            this.offHandSwinging = false;
            this.offHandSwingTicks = 0;
         }
      } else {
         this.offHandSwingTicks = 0;
      }

      this.offHandSwingProgress = (float)this.offHandSwingTicks / offHandDuration;
      this.lastMainHandSwingProgress = this.mainHandSwingProgress;
      int mainHandDuration = LuaScriptCache.swingSpeed;
      if (this.mainHandSwinging) {
         this.mainHandSwingTicks++;
         if (this.mainHandSwingTicks >= mainHandDuration) {
            this.mainHandSwinging = false;
            this.mainHandSwingTicks = 0;
         }
      } else {
         this.mainHandSwingTicks = 0;
      }

      this.mainHandSwingProgress = (float)this.mainHandSwingTicks / mainHandDuration;
   }

   @ModifyConstant(
      method = {"getCurrentSwingDuration()I"},
      constant = {@Constant(
         intValue = 6
      )}
   )
   private int modifySwingDuration(int original) {
      return this == Minecraft.getInstance().player ? LuaScriptCache.swingSpeed : original;
   }
}

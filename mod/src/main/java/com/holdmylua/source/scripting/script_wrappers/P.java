package com.holdmylua.source.scripting.script_wrappers;

import com.holdmylua.source.access.LivingEntityAccessor;
import com.holdmylua.source.annotation.Safe;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class P {
   @Safe
   public double getHealth(AbstractClientPlayer player) {
      return player.getHealth();
   }

   @Safe
   public boolean isSneaking(AbstractClientPlayer player) {
      return player.isShiftKeyDown();
   }

   @Safe
   public boolean isOnGround(AbstractClientPlayer player) {
      return player.onGround();
   }

   @Safe
   public boolean isSwimming(AbstractClientPlayer player) {
      return player.isSwimming();
   }

   @Safe
   public boolean isClimbing(AbstractClientPlayer player) {
      return player.onClimbable();
   }

   @Safe
   public boolean isCrawling(AbstractClientPlayer player) {
      return player.isVisuallyCrawling();
   }

   @Safe
   public boolean isSubmergedInWater(AbstractClientPlayer player) {
      return player.isUnderWater();
   }

   @Safe
   public boolean isTouchingWater(AbstractClientPlayer player) {
      return player.isInWater();
   }

   @Safe
   public boolean isUsingSpyglass(AbstractClientPlayer player) {
      return player.isScoping();
   }

   @Safe
   public boolean isUsingRiptide(AbstractClientPlayer player) {
      return player.isAutoSpinAttack();
   }

   @Safe
   public double getX(AbstractClientPlayer player) {
      return player.getX();
   }

   @Safe
   public double getY(AbstractClientPlayer player) {
      return player.getY();
   }

   @Safe
   public double getZ(AbstractClientPlayer player) {
      return player.getZ();
   }

   @Safe
   public double getXSpeed(AbstractClientPlayer player) {
      return player.getDeltaMovement().x();
   }

   @Safe
   public double getYSpeed(AbstractClientPlayer player) {
      return player.getDeltaMovement().y();
   }

   @Safe
   public double getZSpeed(AbstractClientPlayer player) {
      return player.getDeltaMovement().z();
   }

   @Safe
   public double getSpeed(AbstractClientPlayer player) {
      return player.getDeltaMovement().length();
   }

   @Safe
   public boolean isUsingItem(AbstractClientPlayer player) {
      return player.isUsingItem();
   }

   @Safe
   public double getYaw(AbstractClientPlayer player) {
      return player.getYHeadRot();
   }

   @Safe
   public double getPitch(AbstractClientPlayer player) {
      return player.getXRot();
   }

   @Safe
   public ItemStack getMainItem(AbstractClientPlayer player) {
      return player.getMainHandItem();
   }

   @Safe
   public ItemStack getOffhandItem(AbstractClientPlayer player) {
      return player.getOffhandItem();
   }

   @Safe
   public InteractionHand getActiveHand(AbstractClientPlayer player) {
      return player.getUsedItemHand();
   }

   @Safe
   public int getAge(AbstractClientPlayer player) {
      return player.tickCount;
   }

   @Safe
   public boolean isItemCoolingDown(ItemStack item, AbstractClientPlayer player) {
      return player.getCooldowns().isOnCooldown(item);
   }

   @Safe
   public double getSwingCount(AbstractClientPlayer player) {
      return player instanceof LivingEntityAccessor access ? access.hMI5_0$getSwingCount() : 0.0;
   }

   @Safe
   public String getStandingBlock(AbstractClientPlayer player) {
      return player.level().getBlockState(player.blockPosition().below()).getBlockHolder().getRegisteredName();
   }

   @Safe
   public String getBlockBelow(AbstractClientPlayer player, int steps) {
      return player.level().getBlockState(player.blockPosition().below().below(steps)).getBlockHolder().getRegisteredName();
   }

   @Safe
   public String getBlockAbove(AbstractClientPlayer player, int steps) {
      return player.level().getBlockState(player.blockPosition().above().above().above(steps)).getBlockHolder().getRegisteredName();
   }

   @Safe
   public boolean hasVehicle(AbstractClientPlayer player, int steps) {
      return player.isPassenger();
   }
}

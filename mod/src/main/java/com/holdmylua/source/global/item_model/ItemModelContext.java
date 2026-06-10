package com.holdmylua.source.global.item_model;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class ItemModelContext {
   public boolean bl;
   public float swingProgress;
   public ItemStack item;
   public AbstractClientPlayer player;
   public InteractionHand hand;
   public boolean mainHand;
   public float deltaTime;
   public float equipProgress;
   public float mainHandSwingProgress;
   public float offHandSwingProgress;
   public boolean mainHandSwitchEvent;
   public boolean offHandSwitchEvent;
   public boolean swingMHand;
   public boolean swingOHand;
   public boolean interact;
   public boolean blockBreaking;

   public ItemModelContext(
      boolean bl,
      float swingProgress,
      AbstractClientPlayer player,
      InteractionHand hand,
      boolean mainHand,
      float deltaTime,
      float equipProgress,
      float mainHandSwingProgress,
      float offHandSwingProgress,
      boolean mainHandSwitchEvent,
      boolean offHandSwitchEvent,
      boolean swingMHand,
      boolean swingOHand,
      boolean interact,
      boolean blockBreaking,
      ItemStack item
   ) {
      this.bl = bl;
      this.swingProgress = swingProgress;
      this.player = player;
      this.hand = hand;
      this.mainHand = mainHand;
      this.deltaTime = deltaTime;
      this.equipProgress = equipProgress;
      this.mainHandSwingProgress = mainHandSwingProgress;
      this.offHandSwingProgress = offHandSwingProgress;
      this.mainHandSwitchEvent = mainHandSwitchEvent;
      this.offHandSwitchEvent = offHandSwitchEvent;
      this.swingMHand = swingMHand;
      this.swingOHand = swingOHand;
      this.interact = interact;
      this.blockBreaking = blockBreaking;
      this.item = item;
   }

   public void set(ItemModelContext data) {
      this.bl = data.bl;
      this.swingProgress = data.swingProgress;
      this.player = data.player;
      this.hand = data.hand;
      this.mainHand = data.mainHand;
      this.deltaTime = data.deltaTime;
      this.equipProgress = data.equipProgress;
      this.mainHandSwingProgress = data.mainHandSwingProgress;
      this.offHandSwingProgress = data.offHandSwingProgress;
      this.mainHandSwitchEvent = data.mainHandSwitchEvent;
      this.offHandSwitchEvent = data.offHandSwitchEvent;
      this.swingMHand = data.swingMHand;
      this.swingOHand = data.swingOHand;
      this.interact = data.interact;
      this.blockBreaking = data.blockBreaking;
      this.item = data.item;
   }
}

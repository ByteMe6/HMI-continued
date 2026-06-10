package com.holdmylua.source.mixin.property;

import com.holdmylua.source.global.GlobalsStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.UseDuration;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({UseDuration.class})
public class UseDurationMixin {
   @Inject(
      method = {"get"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void swapProperty(ItemStack stack, ClientLevel world, ItemOwner context, int seed, CallbackInfoReturnable<Float> cir) {
      if (Minecraft.getInstance().player != null
         && Minecraft.getInstance().player.getUseItem() == stack
         && GlobalsStorage.useDuration.containsKey(stack.getItem().toString())) {
         cir.setReturnValue(Float.parseFloat(GlobalsStorage.useDuration.get(stack.getItem().toString()).toString()));
      }
   }
}

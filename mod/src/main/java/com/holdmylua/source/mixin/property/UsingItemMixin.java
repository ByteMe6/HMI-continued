package com.holdmylua.source.mixin.property;

import com.holdmylua.source.global.GlobalsStorage;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.IsUsingItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({IsUsingItem.class})
public class UsingItemMixin {
   @Inject(
      method = {"get"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void swapProperty(
      ItemStack stack, ClientLevel world, LivingEntity entity, int seed, ItemDisplayContext displayContext, CallbackInfoReturnable<Boolean> cir
   ) {
      if (entity != null && entity.getUseItem() == stack && GlobalsStorage.usingItem.containsKey(stack.getItem().toString())) {
         cir.setReturnValue(GlobalsStorage.usingItem.get(stack.getItem().toString()));
      }
   }
}

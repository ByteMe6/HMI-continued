package com.holdmylua.source.global;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;

public class DispatcherStorage {
   private static final List<ItemStack> renderedItems = new ArrayList<>();

   public static void setItem(ItemStack item) {
      if (!item.isEmpty() && item.getUseAnimation() != ItemUseAnimation.BLOCK && item.getUseAnimation() != ItemUseAnimation.TRIDENT) {
         renderedItems.add(item);
      }
   }

   public static ItemStack getRenderedItem() {
      if (!renderedItems.isEmpty()) {
         ItemStack stack = renderedItems.getFirst();
         renderedItems.remove(stack);
         return stack;
      } else {
         return Items.AIR.getDefaultInstance();
      }
   }

   public static void clear() {
      renderedItems.clear();
   }
}

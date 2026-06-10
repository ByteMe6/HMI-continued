package com.holdmylua.source.global.item_model;

import com.holdmylua.source.LuaTestHMI;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;

public class ItemModelStorage {
   private static final List<ItemModelContext> data = new ArrayList<>();

   public static void addData(ItemModelContext info, ItemStack item) {
      if (!item.isEmpty() && item.getUseAnimation() != ItemUseAnimation.BLOCK && item.getUseAnimation() != ItemUseAnimation.TRIDENT) {
         data.add(info);
      }
   }

   public static ItemModelContext get() {
      if (!data.isEmpty()) {
         ItemModelContext record = data.getFirst();
         data.remove(record);
         return record;
      } else {
         return new ItemModelContext(
            false,
            0.0F,
            Minecraft.getInstance().player,
            InteractionHand.MAIN_HAND,
            false,
            LuaTestHMI.deltaTime,
            0.0F,
            0.0F,
            0.0F,
            false,
            false,
            false,
            false,
            false,
            false,
            Items.AIR.getDefaultInstance()
         );
      }
   }

   public static void clear() {
      data.clear();
   }
}

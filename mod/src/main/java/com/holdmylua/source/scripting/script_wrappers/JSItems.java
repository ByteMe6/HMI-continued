package com.holdmylua.source.scripting.script_wrappers;

import com.holdmylua.source.annotation.Safe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class JSItems {
   @Safe
   public Item get(String name) {
      Identifier id = Identifier.parse(name);
      return (Item)BuiltInRegistries.ITEM.getValue(id);
   }

   @Safe
   public String checkItemName(ItemStack item) {
      return item.getCustomName().toString();
   }
}

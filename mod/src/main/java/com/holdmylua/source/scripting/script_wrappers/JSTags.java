package com.holdmylua.source.scripting.script_wrappers;

import com.holdmylua.source.annotation.Safe;
import net.fabricmc.fabric.impl.tag.convention.v2.TagRegistration;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class JSTags {
   @Safe
   public TagKey<Item> getVanillaTag(String id) {
      return TagKey.create(Registries.ITEM, Identifier.withDefaultNamespace(id));
   }

   @Safe
   public TagKey<Item> getFabricTag(String id) {
      return TagRegistration.ITEM_TAG.registerC(id);
   }
}

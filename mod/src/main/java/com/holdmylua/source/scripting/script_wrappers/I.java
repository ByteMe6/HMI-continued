package com.holdmylua.source.scripting.script_wrappers;

import com.google.gson.JsonElement;
import com.holdmylua.source.access.ItemStackAccessor;
import com.holdmylua.source.annotation.Safe;
import com.holdmylua.source.data_structures.SpearData;
import com.holdmylua.source.global.GlobalsStorage;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.SplashPotionItem;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.KineticWeapon;
import net.minecraft.world.item.component.ItemAttributeModifiers.Entry;
import net.minecraft.world.item.component.KineticWeapon.Condition;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LanternBlock;

public class I {
   @Safe
   public float getAttackDamage(ItemStack stack) {
      ItemAttributeModifiers modifiers = (ItemAttributeModifiers)stack.getComponents().get(DataComponents.ATTRIBUTE_MODIFIERS);
      if (modifiers == null) {
         return 0.0F;
      } else {
         float totalDamage = 0.0F;

         for (Entry entry : modifiers.modifiers()) {
            if (entry.attribute().value() == Attributes.ATTACK_DAMAGE.value()) {
               totalDamage += (float)entry.modifier().amount();
            }
         }

         return totalDamage;
      }
   }

   @Safe
   public boolean isOf(ItemStack itemStack, Item item) {
      return itemStack.is(item);
   }

   @Safe
   public boolean isIn(ItemStack itemStack, TagKey<Item> tag) {
      return itemStack.is(tag);
   }

   @Safe
   public boolean isEmpty(ItemStack itemStack) {
      return itemStack.isEmpty();
   }

   @Safe
   public String getUseAction(ItemStack item) {
      return item.getUseAnimation().getSerializedName();
   }

   @Safe
   public String getName(ItemStack item) {
      return item.getItem().toString();
   }

   @Safe
   public String getActualName(ItemStack item) {
      return item.getCustomName() != null ? item.getCustomName().getString() : item.getHoverName().getString();
   }

   @Safe
   public boolean isChargedCrossbow(ItemStack item) {
      return CrossbowItem.isCharged(item);
   }

   @Safe
   public ItemStack getDefaultStack(Item item) {
      return item.getDefaultInstance();
   }

   @Safe
   public boolean isBlock(ItemStack item) {
      return Block.byItem(item.getItem()) != Blocks.AIR;
   }

   @Safe
   public boolean shouldTranslateItem(ItemStack item) {
      int t = ((ItemStackAccessor)(Object)item).hMI5_0$getTransform();
      return (t != 0 || t == -1)
         && (
            !(item.getItem() instanceof FishingRodItem)
                  && !item.is(ConventionalItemTags.RODS)
                  && !item.is(ConventionalItemTags.TOOLS)
                  && !item.is(ItemTags.SWORDS)
                  && !item.is(ConventionalItemTags.MACE_TOOLS)
                  && item.getUseAnimation() != ItemUseAnimation.BLOCK
                  && !(this.getAttackDamage(item) > 0.0F)
               || item.getUseAnimation() == ItemUseAnimation.EAT
               || item.getUseAnimation() == ItemUseAnimation.DRINK
               || item.getUseAnimation() == ItemUseAnimation.SPYGLASS
         );
   }

   @Safe
   public boolean isCustomTranslate(ItemStack item) {
      return GlobalsStorage.translateItem.getOrDefault(this.getName(item), false);
   }

   @Safe
   public void setTranslate(ItemStack item, boolean translate) {
      ((ItemStackAccessor)(Object)item).hMI5_0$setTransform(translate);
   }

   @Safe
   public void setRenderAsBlock(ItemStack item, boolean render) {
      if (Block.byItem(item.getItem()) != Blocks.AIR) {
         ((ItemStackAccessor)(Object)item).hMI5_0$setRenderAsBlock(render);
      }
   }

   @Safe
   public boolean shouldRenderAsBlock(ItemStack item) {
      if (Block.byItem(item.getItem()) != Blocks.AIR) {
         int t = ((ItemStackAccessor)(Object)item).hMI5_0$getRenderAsBlock();
         return t == 1 || t == -1;
      } else {
         return false;
      }
   }

   @Safe
   public boolean isLantern(ItemStack item) {
      return Block.byItem(item.getItem()) instanceof LanternBlock;
   }

   @Safe
   public boolean isThrowable(ItemStack item) {
      return item.getItem() instanceof SplashPotionItem || item.getItem() instanceof ProjectileItem;
   }

   @Safe
   public void setSwingSpeed(ItemStack item, double value) {
      ((ItemStackAccessor)(Object)item).hMI5_0$setSwingSpeed((int)value);
   }

   @Safe
   public boolean isEnchanted(ItemStack item) {
      return item.hasFoil();
   }

   @Safe
   public static ComponentWrapper getComponents(ItemStack stack) {
      DynamicOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, Minecraft.getInstance().level.registryAccess());
      DataComponentMap components = stack.getComponents();
      JsonElement json = (JsonElement)DataComponentMap.CODEC.encodeStart(ops, components).getOrThrow();
      return new ComponentWrapper(json.getAsJsonObject());
   }

   @Safe
   public void copyAppearanceComponents(ItemStack source) {
      ItemStack target = source.getItem().getDefaultInstance();
      if (source.has(DataComponents.ENCHANTMENTS)) {
         target.set(DataComponents.ENCHANTMENTS, (ItemEnchantments)source.get(DataComponents.ENCHANTMENTS));
      }

      if (source.has(DataComponents.ITEM_MODEL)) {
         target.set(DataComponents.ITEM_MODEL, (Identifier)source.get(DataComponents.ITEM_MODEL));
      }

      if (source.has(DataComponents.CUSTOM_MODEL_DATA)) {
         target.set(DataComponents.CUSTOM_MODEL_DATA, (CustomModelData)source.get(DataComponents.CUSTOM_MODEL_DATA));
      }

      if (source.has(DataComponents.CUSTOM_DATA)) {
         target.set(DataComponents.CUSTOM_DATA, (CustomData)source.get(DataComponents.CUSTOM_DATA));
      }
   }

   @Safe
   public void setMainStack(Item item) {
      GlobalsStorage.mainHandItem = item.getDefaultInstance();
   }

   @Safe
   public void setOffStack(Item item) {
      GlobalsStorage.offHandItem = item.getDefaultInstance();
   }

   @Safe
   public SpearData getSpearData(ItemStack item) {
      AbstractClientPlayer player = Minecraft.getInstance().player;
      KineticWeapon kineticWeaponComponent = (KineticWeapon)item.get(DataComponents.KINETIC_WEAPON);
      int spearUseDuration = item.getUseDuration(player) - (player.getUseItemRemainingTicks() + 1);
      int i = kineticWeaponComponent.delayTicks();
      boolean canDismount = spearUseDuration < kineticWeaponComponent.dismountConditions().<Integer>map(Condition::maxDurationTicks).orElse(0) + i;
      boolean canKnockBack = spearUseDuration < kineticWeaponComponent.knockbackConditions().<Integer>map(Condition::maxDurationTicks).orElse(0) + i;
      boolean canDamage = spearUseDuration < kineticWeaponComponent.damageConditions().<Integer>map(Condition::maxDurationTicks).orElse(0) + i;
      boolean hitImpact = player.getTicksSinceLastKineticHitFeedback(0.0F) == 1.0F;
      return new SpearData(canDamage, canKnockBack, canDismount, hitImpact);
   }
}

package com.holdmylua.source.mixin.render;

import com.holdmylua.source.global.DispatcherStorage;
import com.holdmylua.source.global.GlobalsStorage;
import com.holdmylua.source.global.item_model.ItemModelContext;
import com.holdmylua.source.global.item_model.ItemModelStorage;
import com.holdmylua.source.lua_runtime.ModelScriptCache;
import com.holdmylua.source.lua_runtime.ScriptHolder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.MatrixUtil;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState.FoilType;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({ItemFeatureRenderer.class})
public abstract class ItemRendererMixin {
   @Unique
   private static int getTint(int[] tints, int index) {
      return index >= 0 && index < tints.length ? tints[index] : -1;
   }

   @Unique
   private static boolean useTranslucentGlint(RenderType renderLayer) {
      return Minecraft.useShaderTransparency() && renderLayer == Sheets.translucentItemSheet();
   }

   @Redirect(
      method = {"render"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderItem(Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II[ILjava/util/List;Lnet/minecraft/client/renderer/rendertype/RenderType;Lnet/minecraft/client/renderer/item/ItemStackRenderState$FoilType;)V"
      )
   )
   private static void render(
      ItemDisplayContext displayContext,
      PoseStack matrices,
      MultiBufferSource vertexConsumers,
      int light,
      int overlay,
      int[] tints,
      List<BakedQuad> quads,
      RenderType layer,
      FoilType glint
   ) {
      VertexConsumer vertexConsumer;
      if (glint == FoilType.SPECIAL) {
         Pose entry = matrices.last().copy();
         if (displayContext == ItemDisplayContext.GUI) {
            MatrixUtil.mulComponentWise(entry.pose(), 0.5F);
         } else if (displayContext.firstPerson()) {
            MatrixUtil.mulComponentWise(entry.pose(), 0.75F);
         }

         vertexConsumer = getSpecialItemGlintConsumer(vertexConsumers, layer, entry);
      } else {
         vertexConsumer = ItemRenderer.getFoilBuffer(vertexConsumers, layer, true, glint != FoilType.NONE);
      }

      customRenderBakedQuads(matrices, vertexConsumer, quads, tints, light, overlay, displayContext);
   }

   @Unique
   private static void customRenderBakedQuads(
      PoseStack matrices, VertexConsumer vertexConsumer, List<BakedQuad> quads, int[] tints, int light, int overlay, ItemDisplayContext displayContext
   ) {
      AbstractClientPlayer player = Minecraft.getInstance().player;
      int index = 0;
      if ((displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
         && Minecraft.getInstance().getEntityRenderDispatcher().options.getCameraType().isFirstPerson()
         && player == Minecraft.getInstance().player) {
         ItemModelContext context = ItemModelStorage.get();
         ItemStack renderedItem = DispatcherStorage.getRenderedItem();
         ScriptHolder.itemModelCache.executeModel(context, renderedItem, player, GlobalsStorage.modelPartAnimator);

         for (ModelScriptCache cache : ScriptHolder.itemModelAddonsCache) {
            cache.executeModel(context, renderedItem, player, GlobalsStorage.modelPartAnimator);
         }
      }

      for (BakedQuad bakedQuad : quads) {
         float f;
         float g;
         float h;
         float j;
         if (bakedQuad.isTinted()) {
            int i = getTint(tints, bakedQuad.tintIndex());
            f = ARGB.alpha(i) / 255.0F;
            g = ARGB.red(i) / 255.0F;
            h = ARGB.green(i) / 255.0F;
            j = ARGB.blue(i) / 255.0F;
         } else {
            f = 1.0F;
            g = 1.0F;
            h = 1.0F;
            j = 1.0F;
         }

         matrices.pushPose();
         GlobalsStorage.modelPartAnimator.applyPoses(index, matrices);
         Pose entry = matrices.last();
         vertexConsumer.putBulkData(entry, bakedQuad, g, h, j, f, light, overlay);
         matrices.popPose();
         index++;
      }

      GlobalsStorage.modelPartAnimator.clear();
   }

   @Unique
   private static VertexConsumer getSpecialItemGlintConsumer(MultiBufferSource consumers, RenderType layer, Pose matrix) {
      return VertexMultiConsumer.create(
         new SheetedDecalTextureGenerator(
            consumers.getBuffer(useTranslucentGlint(layer) ? RenderTypes.glintTranslucent() : RenderTypes.glint()), matrix, 0.0078125F
         ),
         consumers.getBuffer(layer)
      );
   }
}

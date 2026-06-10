package com.holdmylua.source.mixin.render;

import com.holdmylua.source.global.DispatcherStorage;
import com.holdmylua.source.global.GlobalsStorage;
import com.holdmylua.source.global.item_model.ItemModelContext;
import com.holdmylua.source.global.item_model.ItemModelStorage;
import com.holdmylua.source.lua_runtime.ModelScriptCache;
import com.holdmylua.source.lua_runtime.ScriptHolder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.MatrixUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState.FoilType;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// PORT-26.1: the old redirect target (static ItemRenderer.renderItem inside
// ItemFeatureRenderer.render) no longer exists. The per-quad loop moved into
// ItemFeatureRenderer.renderItem(BufferSource, OutlineBufferSource, ItemSubmit);
// we take that method over for first-person hand items so the Lua model scripts
// can pose individual quads, mirroring the vanilla body otherwise.
@Mixin(ItemFeatureRenderer.class)
public abstract class ItemRendererMixin {
   @Unique
   private static int hmi$getTint(int[] tints, int index) {
      return index >= 0 && index < tints.length ? tints[index] : -1;
   }

   @Inject(method = "renderItem", at = @At("HEAD"), cancellable = true)
   private void hmi$renderItem(
      MultiBufferSource.BufferSource bufferSource, OutlineBufferSource outlineBufferSource, SubmitNodeStorage.ItemSubmit submit, CallbackInfo ci
   ) {
      Minecraft minecraft = Minecraft.getInstance();
      AbstractClientPlayer player = minecraft.player;
      ItemDisplayContext displayContext = submit.displayContext();
      boolean handContext = (displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND
            || displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
         && player != null
         && minecraft.getEntityRenderDispatcher().options.getCameraType().isFirstPerson();
      if (!handContext) {
         return;
      }

      ItemModelContext context = ItemModelStorage.get();
      ItemStack renderedItem = DispatcherStorage.getRenderedItem();
      ScriptHolder.itemModelCache.executeModel(context, renderedItem, player, GlobalsStorage.modelPartAnimator);

      for (ModelScriptCache cache : ScriptHolder.itemModelAddonsCache) {
         cache.executeModel(context, renderedItem, player, GlobalsStorage.modelPartAnimator);
      }

      PoseStack.Pose basePose = submit.pose();
      FoilType foilType = submit.foilType();
      PoseStack.Pose foilDecalPose = null;
      if (foilType == FoilType.SPECIAL) {
         foilDecalPose = basePose.copy();
         if (displayContext == ItemDisplayContext.GUI) {
            MatrixUtil.mulComponentWise(foilDecalPose.pose(), 0.5F);
         } else if (displayContext.firstPerson()) {
            MatrixUtil.mulComponentWise(foilDecalPose.pose(), 0.75F);
         }
      }

      QuadInstance quadInstance = new QuadInstance();
      quadInstance.setLightCoords(submit.lightCoords());
      quadInstance.setOverlayCoords(submit.overlayCoords());
      if (submit.outlineColor() != 0) {
         outlineBufferSource.setColor(submit.outlineColor());
      }

      PoseStack posed = new PoseStack();
      int index = 0;

      for (BakedQuad quad : submit.quads()) {
         BakedQuad.MaterialInfo material = quad.materialInfo();
         RenderType renderType = material.itemRenderType();
         quadInstance.setColor(material.isTinted() ? hmi$getTint(submit.tintLayers(), material.tintIndex()) : -1);
         posed.pushPose();
         posed.last().set(basePose);
         GlobalsStorage.modelPartAnimator.applyPoses(index, posed);
         PoseStack.Pose pose = posed.last();
         if (foilType != FoilType.NONE) {
            VertexConsumer foilBuffer = bufferSource.getBuffer(ItemFeatureRenderer.getFoilRenderType(renderType, true));
            if (foilDecalPose != null) {
               foilBuffer = new SheetedDecalTextureGenerator(foilBuffer, foilDecalPose, 0.0078125F);
            }
            foilBuffer.putBakedQuad(pose, quad, quadInstance);
         }
         if (submit.outlineColor() != 0) {
            outlineBufferSource.getBuffer(renderType).putBakedQuad(pose, quad, quadInstance);
         }
         bufferSource.getBuffer(renderType).putBakedQuad(pose, quad, quadInstance);
         posed.popPose();
         index++;
      }

      GlobalsStorage.modelPartAnimator.clear();
      ci.cancel();
   }
}

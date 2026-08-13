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
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState.FoilType;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HangingSignBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// PORT-26.2: MultiBufferSource/OutlineBufferSource are gone and the old per-submit
// renderItem(BufferSource, OutlineBufferSource, SubmitNodeStorage.ItemSubmit) hook no
// longer exists. The submit record moved to ItemFeatureRenderer.Submit (same shape) and
// the quad loop now runs as two passes from buildGroup -> prepareSubmit(submit, foil):
//   pass 1 (foil=false): prepareMainSubmit / prepareOutlineSubmit
//   pass 2 (foil=true):  prepareFoilSubmit
// We take prepareSubmit over for first-person hand items so the Lua model scripts can
// pose individual quads, mirroring the vanilla body otherwise. Vertex buffers come from
// the inherited RenderTypeFeatureRenderer.getVertexBuilder(RenderType) via the
// RenderTypeFeatureRendererAccessor (an @Invoker only resolves members declared in the
// target class itself, so it cannot be declared here), and the foil buffer replicates
// the private getFoilBuffer logic (RenderTypes.glint/glintTranslucent +
// SheetedDecalTextureGenerator).
// PORT-26.2: the old HangingSignMixin (HangingSignRenderer.submitSpecial) has no target
// anymore — 26.2 removed the hanging-sign special renderer entirely. Held hanging signs
// now flow through this item pipeline, so the damage-flash suppression is folded in
// here: whenever the rendered item is a hanging sign, the overlay is dropped.
@Mixin(ItemFeatureRenderer.class)
public abstract class ItemRendererMixin {
   @Unique
   private ItemStack hmi$currentItem = null;

   @Unique
   private VertexConsumer hmi$vertexBuilder(RenderType renderType) {
      // getVertexBuilder is declared on RenderTypeFeatureRenderer, so it is reached
      // through the accessor mixin attached to that class.
      return ((RenderTypeFeatureRendererAccessor) this).hmi$getVertexBuilder(renderType);
   }

   @Unique
   private static int hmi$getTint(int[] tints, int index) {
      return index >= 0 && index < tints.length ? tints[index] : -1;
   }

   @Unique
   private boolean hmi$isHandContext(ItemFeatureRenderer.Submit submit) {
      Minecraft minecraft = Minecraft.getInstance();
      AbstractClientPlayer player = minecraft.player;
      ItemDisplayContext displayContext = submit.displayContext();
      return (displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND
            || displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
         && player != null
         && minecraft.getEntityRenderDispatcher().options.getCameraType().isFirstPerson();
   }

   @Inject(method = "prepareSubmit", at = @At("HEAD"), cancellable = true)
   private void hmi$prepareSubmit(ItemFeatureRenderer.Submit submit, boolean foil, CallbackInfo ci) {
      if (!hmi$isHandContext(submit)) {
         return;
      }

      if (foil) {
         // pass 2: foil quads only — the scripts already ran in pass 1
         hmi$renderFoil(submit);
         GlobalsStorage.modelPartAnimator.clear();
      } else {
         // pass 1: run the Lua model scripts once, then the main/outline quad loop
         this.hmi$currentItem = DispatcherStorage.getRenderedItem();
         hmi$runScripts();
         if (submit.outlineColor() != 0) {
            hmi$renderOutline(submit);
         } else {
            hmi$renderMain(submit);
         }
      }
      ci.cancel();
   }

   @Unique
   private void hmi$runScripts() {
      Minecraft minecraft = Minecraft.getInstance();
      AbstractClientPlayer player = minecraft.player;
      ItemModelContext context = ItemModelStorage.get();
      ScriptHolder.itemModelCache.executeModel(context, this.hmi$currentItem, player, GlobalsStorage.modelPartAnimator);
      for (ModelScriptCache cache : ScriptHolder.itemModelAddonsCache) {
         cache.executeModel(context, this.hmi$currentItem, player, GlobalsStorage.modelPartAnimator);
      }
   }

   @Unique
   private void hmi$renderMain(ItemFeatureRenderer.Submit submit) {
      QuadInstance quadInstance = new QuadInstance();
      quadInstance.setLightCoords(submit.lightCoords());
      // PORT-26.2: held hanging signs render through this pipeline now; keep the old
      // HangingSignMixin behaviour of suppressing the damage-flash overlay on them.
      quadInstance.setOverlayCoords(hmi$isHangingSign() ? OverlayTexture.NO_OVERLAY : submit.overlayCoords());

      // PORT-26.2: the item translucent render type now writes depth (DepthStencilState.
      // DEFAULT), so submitting quads in model order makes the translucent water (listed
      // before the walls) occlude them — the back wall ends up depth-rejected and the
      // bucket looks see-through. Submit opaque quads first (they write depth), then the
      // translucent ones (blended on top), mirroring the 1.21.11 solid-then-translucent
      // draw order. The pose index stays the quad's position in submit.quads().
      for (int pass = 0; pass < 2; pass++) {
         boolean translucentPass = pass == 1;
         PoseStack posed = new PoseStack();
         int index = 0;
         for (BakedQuad quad : submit.quads()) {
            BakedQuad.MaterialInfo material = quad.materialInfo();
            if ((material.layer() == ChunkSectionLayer.TRANSLUCENT) != translucentPass) {
               index++;
               continue;
            }
            RenderType renderType = material.itemRenderType();
            quadInstance.setColor(material.isTinted() ? hmi$getTint(submit.tintLayers(), material.tintIndex()) : -1);
            posed.pushPose();
            posed.last().set(submit.pose());
            GlobalsStorage.modelPartAnimator.applyPoses(index, posed);
            this.hmi$vertexBuilder(renderType).putBakedQuad(posed.last(), quad, quadInstance);
            posed.popPose();
            index++;
         }
      }
   }

   @Unique
   private void hmi$renderOutline(ItemFeatureRenderer.Submit submit) {
      QuadInstance quadInstance = new QuadInstance();
      quadInstance.setLightCoords(submit.lightCoords());
      quadInstance.setOverlayCoords(hmi$isHangingSign() ? OverlayTexture.NO_OVERLAY : submit.overlayCoords());

      PoseStack posed = new PoseStack();
      int index = 0;
      for (BakedQuad quad : submit.quads()) {
         RenderType renderType = quad.materialInfo().itemRenderType().outline().orElse(null);
         if (renderType != null) {
            quadInstance.setColor(submit.outlineColor());
            posed.pushPose();
            posed.last().set(submit.pose());
            GlobalsStorage.modelPartAnimator.applyPoses(index, posed);
            this.hmi$vertexBuilder(renderType).putBakedQuad(posed.last(), quad, quadInstance);
            posed.popPose();
         }
         index++;
      }
   }

   @Unique
   private void hmi$renderFoil(ItemFeatureRenderer.Submit submit) {
      FoilType foilType = submit.foilType();
      if (foilType == FoilType.NONE) {
         return;
      }

      PoseStack.Pose basePose = submit.pose();
      PoseStack.Pose foilDecalPose = null;
      if (foilType == FoilType.SPECIAL) {
         foilDecalPose = basePose.copy();
         if (submit.displayContext() == ItemDisplayContext.GUI) {
            MatrixUtil.mulComponentWise(foilDecalPose.pose(), 0.5F);
         } else if (submit.displayContext().firstPerson()) {
            MatrixUtil.mulComponentWise(foilDecalPose.pose(), 0.75F);
         }
      }

      QuadInstance quadInstance = new QuadInstance();
      quadInstance.setLightCoords(submit.lightCoords());
      quadInstance.setOverlayCoords(hmi$isHangingSign() ? OverlayTexture.NO_OVERLAY : submit.overlayCoords());

      PoseStack posed = new PoseStack();
      int index = 0;
      for (BakedQuad quad : submit.quads()) {
         RenderType renderType = quad.materialInfo().itemRenderType();
         // replicates ItemFeatureRenderer.getFoilBuffer / useTransparentGlint
         boolean transparent = Minecraft.getInstance().gameRenderer.gameRenderState().useShaderTransparency()
            && renderType.outputTarget() == OutputTarget.ITEM_ENTITY_TARGET;
         VertexConsumer foilBuffer = this.hmi$vertexBuilder(transparent ? RenderTypes.glintTranslucent() : RenderTypes.glint());
         if (foilDecalPose != null) {
            foilBuffer = new SheetedDecalTextureGenerator(foilBuffer, foilDecalPose, 0.0078125F);
         }
         posed.pushPose();
         posed.last().set(basePose);
         GlobalsStorage.modelPartAnimator.applyPoses(index, posed);
         foilBuffer.putBakedQuad(posed.last(), quad, quadInstance);
         posed.popPose();
         index++;
      }
   }

   @Unique
   private boolean hmi$isHangingSign() {
      return this.hmi$currentItem != null && !this.hmi$currentItem.isEmpty()
         && Block.byItem(this.hmi$currentItem.getItem()) instanceof HangingSignBlock;
   }
}

package com.holdmylua.source.mixin.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.feature.RenderTypeFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

// PORT-26.2: ItemFeatureRenderer's quad loop obtains vertex consumers via the inherited
// RenderTypeFeatureRenderer.getVertexBuilder(RenderType) (protected final). A mixin on
// ItemFeatureRenderer cannot @Invoker that method — accessors only resolve members
// declared in the target class itself — so the accessor lives here, on the class that
// declares it, and ItemRendererMixin calls it through a cast.
@Mixin(RenderTypeFeatureRenderer.class)
public interface RenderTypeFeatureRendererAccessor {
   @Invoker("getVertexBuilder")
   VertexConsumer hmi$getVertexBuilder(RenderType renderType);
}

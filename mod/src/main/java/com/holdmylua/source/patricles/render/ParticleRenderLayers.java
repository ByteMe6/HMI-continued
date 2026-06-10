package com.holdmylua.source.patricles.render;

import com.holdmylua.source.mixin.render.RenderTypeInvoker;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.function.Function;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

public class ParticleRenderLayers {
   static BlendFunction ADDITIVE = new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE);
   // PORT-26.1: RenderPipelines.GUI_TEXTURED_SNIPPET and RenderPipelines.register are no
   // longer accessible; the pipeline is built standalone with the same shaders/format and
   // is compiled lazily on first use instead of being preload-registered.
   static RenderPipeline ADDITIVE_PARTICLE = RenderPipeline.builder()
      .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
      .withUniform("Projection", UniformType.UNIFORM_BUFFER)
      .withVertexShader("core/position_tex_color")
      .withFragmentShader("core/position_tex_color")
      .withSampler("Sampler0")
      .withVertexFormat(DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS)
      .withLocation("pipeline/additive_particle_effect")
      .withColorTargetState(new ColorTargetState(ADDITIVE))
      .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
      .build();
   private static final Function<Identifier, RenderType> ADDITIVE_PARTICLE_LAYER = Util.memoize(
      texture -> RenderTypeInvoker.hmi$create(
         "fire_screen_effect", RenderSetup.builder(ADDITIVE_PARTICLE).withTexture("Sampler0", texture).createRenderSetup()
      )
   );

   public static RenderType additiveParticle(Identifier texture) {
      return ADDITIVE_PARTICLE_LAYER.apply(texture);
   }
}

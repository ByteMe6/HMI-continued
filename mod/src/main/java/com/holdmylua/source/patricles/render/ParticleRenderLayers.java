package com.holdmylua.source.patricles.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import java.util.function.Function;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

public class ParticleRenderLayers {
   private static final Function<Identifier, RenderType> ADDITIVE_PARTICLE_LAYER = Util.memoize(
      texture -> RenderType.create(
         "fire_screen_effect", RenderSetup.builder(ParticleRenderLayers.ADDITIVE_PARTICLE).withTexture("Sampler0", texture).createRenderSetup()
      )
   );
   static BlendFunction ADDITIVE = new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE);
   static RenderPipeline ADDITIVE_PARTICLE = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.GUI_TEXTURED_SNIPPET})
         .withLocation("pipeline/additive_particle_effect")
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(ADDITIVE)
         .build()
   );

   public static RenderType additiveParticle(Identifier texture) {
      return ADDITIVE_PARTICLE_LAYER.apply(texture);
   }
}

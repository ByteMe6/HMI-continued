package com.holdmylua.source.patricles;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.InteractionHand;

public class ParticleRenderManager {
   public static void draw(
      ArrayList<Particle> particles,
      PoseStack matrices,
      SubmitNodeCollector queue,
      String space,
      InteractionHand hand,
      int light,
      AbstractClientPlayer player,
      float tickProgress
   ) {
      Iterator<Particle> iterator = particles.iterator();

      while (iterator.hasNext()) {
         Particle particle = iterator.next();
         if (particle.dead) {
            iterator.remove();
         } else if (particle.space == space && particle.hand == hand) {
            if (particle.p_matrices != null) {
               particle.render(particle.p_matrices, queue, light, player, tickProgress);
            } else {
               particle.render(matrices, queue, light, player, tickProgress);
            }

            particle.tick();
         }
      }
   }
}

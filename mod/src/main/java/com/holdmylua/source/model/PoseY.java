package com.holdmylua.source.model;

import com.holdmylua.source.model.interfaces.Poses;
import com.holdmylua.source.model.parents.AbstractPose;
import com.mojang.blaze3d.vertex.PoseStack;

public class PoseY extends AbstractPose implements Poses {
   float amount;

   public PoseY(float amount) {
      this.amount = amount;
   }

   @Override
   public void applyPose(PoseStack matrices) {
      matrices.translate(0.0F, this.amount, 0.0F);
   }
}

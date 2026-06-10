package com.holdmylua.source.model;

import com.holdmylua.source.model.interfaces.Poses;
import com.holdmylua.source.model.parents.AbstractPose;
import com.mojang.blaze3d.vertex.PoseStack;

public class PoseX extends AbstractPose implements Poses {
   float amount;

   public PoseX(float amount) {
      this.amount = amount;
   }

   @Override
   public void applyPose(PoseStack matrices) {
      matrices.translate(this.amount, 0.0F, 0.0F);
   }
}

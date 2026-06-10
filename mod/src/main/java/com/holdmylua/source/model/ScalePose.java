package com.holdmylua.source.model;

import com.holdmylua.source.model.interfaces.Poses;
import com.holdmylua.source.model.parents.AbstractPose;
import com.mojang.blaze3d.vertex.PoseStack;

public class ScalePose extends AbstractPose implements Poses {
   float x;
   float y;
   float z;

   public ScalePose(float x, float y, float z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   @Override
   public void applyPose(PoseStack matrices) {
      matrices.scale(this.x, this.y, this.z);
   }
}

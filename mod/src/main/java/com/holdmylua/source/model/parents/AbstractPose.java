package com.holdmylua.source.model.parents;

import com.holdmylua.source.model.interfaces.Poses;
import com.mojang.blaze3d.vertex.PoseStack;

public class AbstractPose implements Poses {
   public static int id = 0;
   public int index = 0;
   public int order = id;

   protected AbstractPose() {
   }

   public int getOrder() {
      return this.order;
   }

   public void setOrder(int order) {
      this.order = order;
   }

   @Override
   public void applyPose(PoseStack matrices) {
   }
}

package com.holdmylua.source.data_structures;

public class SpearData {
   public boolean canDamage;
   public boolean canKnockback;
   public boolean canDismount;
   public boolean hitImpact;

   public SpearData(boolean canDamage, boolean canKnockback, boolean canDismount, boolean hitImpact) {
      this.canDamage = canDamage;
      this.canKnockback = canKnockback;
      this.canDismount = canDismount;
      this.hitImpact = hitImpact;
   }
}

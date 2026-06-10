package com.holdmylua.source;

public enum SwordAttacks {
   RTL,
   LTR,
   FWD;

   private static final SwordAttacks[] vals = values();

   public SwordAttacks next() {
      return vals[(this.ordinal() + 1) % vals.length];
   }
}

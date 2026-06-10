package com.holdmylua.source.patricles;

import java.util.function.Consumer;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public class LuaConsumer implements Consumer<Particle> {
   private final LuaFunction function;

   public LuaConsumer(LuaFunction function) {
      this.function = function;
   }

   public void accept(Particle particle) {
      try {
         this.function.call(CoerceJavaToLua.coerce(particle));
      } catch (Exception var3) {
         var3.printStackTrace();
      }
   }
}

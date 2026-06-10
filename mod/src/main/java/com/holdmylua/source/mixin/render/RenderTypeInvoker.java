package com.holdmylua.source.mixin.render;

import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

// PORT-26.1: RenderType.create is package-private in 26.1, so custom render types
// are created through this invoker.
@Mixin(RenderType.class)
public interface RenderTypeInvoker {
   @Invoker("create")
   static RenderType hmi$create(String name, RenderSetup setup) {
      throw new AssertionError();
   }
}

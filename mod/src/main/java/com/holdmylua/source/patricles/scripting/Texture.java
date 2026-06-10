package com.holdmylua.source.patricles.scripting;

import com.holdmylua.source.annotation.Safe;
import net.minecraft.resources.Identifier;

public class Texture {
   @Safe
   public Identifier of(String namespace, String path) {
      return Identifier.fromNamespaceAndPath(namespace, path);
   }
}

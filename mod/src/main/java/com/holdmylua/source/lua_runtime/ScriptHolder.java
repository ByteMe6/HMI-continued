package com.holdmylua.source.lua_runtime;

import java.io.IOException;
import java.util.ArrayList;

public class ScriptHolder {
   public static ArrayList<LuaScriptCache> handAddonsCache = new ArrayList<>();
   public static ArrayList<LuaScriptCache> handRelativeAddonsCache = new ArrayList<>();
   public static ArrayList<LuaScriptCache> itemAddonsCache = new ArrayList<>();
   public static ArrayList<ModelScriptCache> itemModelAddonsCache = new ArrayList<>();
   public static LuaScriptCache handScriptCache;
   public static LuaScriptCache handRelativeScriptCache;
   public static LuaScriptCache itemScriptCache;
   public static ModelScriptCache itemModelCache;

   static {
      try {
         handScriptCache = new LuaScriptCache("return");
         handRelativeScriptCache = new LuaScriptCache("return");
         itemScriptCache = new LuaScriptCache("return");
         itemModelCache = new ModelScriptCache("return");
      } catch (IOException var1) {
         throw new RuntimeException(var1);
      }
   }
}

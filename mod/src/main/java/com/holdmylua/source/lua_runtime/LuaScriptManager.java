package com.holdmylua.source.lua_runtime;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.Bit32Lib;
import org.luaj.vm2.lib.CoroutineLib;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.StringLib;
import org.luaj.vm2.lib.TableLib;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JseMathLib;

public class LuaScriptManager {
   private static final LuaScriptManager INSTANCE = new LuaScriptManager();
   public final Globals sharedGlobals = standardGlobals();

   private LuaScriptManager() {
   }

   public static LuaScriptManager getInstance() {
      return INSTANCE;
   }

   private static Globals standardGlobals() {
      Globals globals = new Globals();
      globals.load(new JseBaseLib());
      globals.load(new PackageLib());
      globals.load(new Bit32Lib());
      globals.load(new TableLib());
      globals.load(new StringLib());
      globals.load(new CoroutineLib());
      globals.load(new JseMathLib());
      LoadState.install(globals);
      LuaC.install(globals);
      return globals;
   }
}

package com.holdmylua.source.lua_runtime.resource_controller;

import com.holdmylua.source.global.GlobalsStorage;
import com.holdmylua.source.lua_runtime.LuaScriptCache;
import com.holdmylua.source.lua_runtime.ModelScriptCache;
import com.holdmylua.source.lua_runtime.ScriptHolder;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public class LuaAnimationResourceLoader implements SimpleSynchronousResourceReloadListener {
   public Identifier getFabricId() {
      return Identifier.fromNamespaceAndPath("holdmyitems", "lua_animation_loader");
   }

   private String preprocessScript(String script) {
      if (script != null && !script.isEmpty()) {
         Pattern globalPattern = Pattern.compile("global\\.(\\w+)\\s*=\\s*([^;]+)\\s*;");
         Pattern placeholderPattern = Pattern.compile("\\$\\{(\\w+)\\}");
         Set<String> persistVars = new HashSet<>();
         StringBuilder processed = new StringBuilder();
         boolean respackoptsLoaded = FabricLoader.getInstance().isModLoaded("respackopts");

         for (String line : script.split("\\r?\\n")) {
            if (!respackoptsLoaded) {
               Matcher placeholderMatcher = placeholderPattern.matcher(line);
               StringBuffer placeholderBuffer = new StringBuffer();

               while (placeholderMatcher.find()) {
                  placeholderMatcher.appendReplacement(placeholderBuffer, "0");
               }

               placeholderMatcher.appendTail(placeholderBuffer);
               line = placeholderBuffer.toString();
            }

            Matcher globalMatcher = globalPattern.matcher(line);
            StringBuffer lineBuffer = new StringBuffer();

            boolean found;
            for (found = false; globalMatcher.find(); found = true) {
               String varName = globalMatcher.group(1);
               String value = globalMatcher.group(2).trim();
               persistVars.add(varName);
               globalMatcher.appendReplacement(lineBuffer, "local " + varName + " = registry:getOrDefault('" + varName + "', " + value + ")");
            }

            globalMatcher.appendTail(lineBuffer);
            if (found) {
               processed.append(lineBuffer.toString());
            } else {
               processed.append(line);
            }

            processed.append("\n");
         }

         if (!persistVars.isEmpty()) {
            processed.append("\n-- PERSIST VARIABLES\n");

            for (String varName : persistVars) {
               processed.append("registry:put('").append(varName).append("', ").append(varName).append(")\n");
            }
         }

         return processed.toString();
      } else {
         return script;
      }
   }

   public void onResourceManagerReload(ResourceManager manager) {
      GlobalsStorage.renderAsBlock.clear();
      GlobalsStorage.translateItem.clear();
      GlobalsStorage.registry.clear();
      GlobalsStorage.useDuration.clear();
      GlobalsStorage.applyBlockRotation.clear();
      this.loadSingle(manager, "holdmyitems/hand_pose.lua", script -> {
         try {
            ScriptHolder.handScriptCache = new LuaScriptCache(script);
         } catch (IOException var2) {
            throw new RuntimeException(var2);
         }
      });
      this.loadSingle(manager, "holdmyitems/item_pose.lua", script -> {
         try {
            ScriptHolder.itemScriptCache = new LuaScriptCache(script);
         } catch (IOException var2) {
            throw new RuntimeException(var2);
         }
      });
      this.loadSingle(manager, "holdmyitems/hand_relative_pose.lua", script -> {
         try {
            ScriptHolder.handRelativeScriptCache = new LuaScriptCache(script);
         } catch (IOException var2) {
            throw new RuntimeException(var2);
         }
      });
      this.loadSingle(manager, "holdmyitems/item_model.lua", script -> {
         try {
            ScriptHolder.itemModelCache = new ModelScriptCache(script);
         } catch (IOException var2) {
            throw new RuntimeException(var2);
         }
      });
      ScriptHolder.handAddonsCache = this.loadMultiple(manager, "holdmyitems/hand_addon.lua");
      ScriptHolder.handRelativeAddonsCache = this.loadMultiple(manager, "holdmyitems/hand_relative_addon.lua");
      ScriptHolder.itemAddonsCache = this.loadMultiple(manager, "holdmyitems/item_addon.lua");
      ScriptHolder.itemModelAddonsCache = this.loadMultipleModel(manager, "holdmyitems/item_model_addon.lua");
   }

   private void loadSingle(ResourceManager manager, String path, Consumer<String> consumer) {
      Identifier id = Identifier.fromNamespaceAndPath("minecraft", path);

      try {
         Resource resource = (Resource)manager.getResource(id).orElse(null);
         if (resource == null) {
            consumer.accept("");
            return;
         }

         try (InputStream stream = resource.open()) {
            String content = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            content = this.preprocessScript(content);
            consumer.accept(content);
         }
      } catch (Exception var11) {
         consumer.accept("");
         var11.printStackTrace();
      }
   }

   private ArrayList<LuaScriptCache> loadMultiple(ResourceManager manager, String path) {
      Identifier id = Identifier.fromNamespaceAndPath("minecraft", path);
      ArrayList<LuaScriptCache> caches = new ArrayList<>();

      try {
         for (Resource resource : manager.getResourceStack(id)) {
            try (InputStream stream = resource.open()) {
               String content = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
               content = this.preprocessScript(content);
               caches.add(new LuaScriptCache(content));
            }
         }
      } catch (Exception var13) {
         var13.printStackTrace();
      }

      return caches;
   }

   private ArrayList<ModelScriptCache> loadMultipleModel(ResourceManager manager, String path) {
      Identifier id = Identifier.fromNamespaceAndPath("minecraft", path);
      ArrayList<ModelScriptCache> caches = new ArrayList<>();

      try {
         for (Resource resource : manager.getResourceStack(id)) {
            try (InputStream stream = resource.open()) {
               String content = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
               content = this.preprocessScript(content);
               caches.add(new ModelScriptCache(content));
            }
         }
      } catch (Exception var13) {
         var13.printStackTrace();
      }

      return caches;
   }
}

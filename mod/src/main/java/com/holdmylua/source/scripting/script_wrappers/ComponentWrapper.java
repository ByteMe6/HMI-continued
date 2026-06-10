package com.holdmylua.source.scripting.script_wrappers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.holdmylua.source.annotation.Safe;

public class ComponentWrapper {
   private final JsonObject component;

   public ComponentWrapper(JsonObject obj) {
      this.component = obj;
   }

   @Safe
   public ComponentWrapper next(String key) {
      JsonElement el = this.component.get(key);
      return el != null && el.isJsonObject() ? new ComponentWrapper(el.getAsJsonObject()) : null;
   }

   @Safe
   public Object get(String key) {
      JsonElement el = this.component.get(key);
      if (el != null && !el.isJsonNull()) {
         if (el.isJsonPrimitive()) {
            JsonPrimitive prim = el.getAsJsonPrimitive();
            if (prim.isBoolean()) {
               return prim.getAsBoolean();
            }

            if (prim.isNumber()) {
               Number num = prim.getAsNumber();
               double d = num.doubleValue();
               if (d == (int)d) {
                  return (int)d;
               }

               if (d == (long)d) {
                  return (long)d;
               }

               return d;
            }

            if (prim.isString()) {
               return prim.getAsString();
            }
         }

         return null;
      } else {
         return null;
      }
   }
}

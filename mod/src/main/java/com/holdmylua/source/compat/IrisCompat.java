package com.holdmylua.source.compat;

import java.lang.reflect.Method;
import net.fabricmc.loader.api.FabricLoader;

public class IrisCompat {
   private static Boolean irisAvailable = null;
   private static Method isShaderPackInUseMethod = null;
   private static Object irisApiInstance = null;
   private static boolean initializationAttempted = false;

   public static boolean isShaderPackInUse() {
      if (!isIrisAvailable()) {
         return false;
      } else if (initializationAttempted && irisApiInstance == null) {
         return false;
      } else {
         try {
            if (irisApiInstance == null) {
               initializationAttempted = true;
               Class<?> irisApiClass = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
               Method getInstanceMethod = irisApiClass.getMethod("getInstance");
               irisApiInstance = getInstanceMethod.invoke(null);
               if (irisApiInstance == null) {
                  System.err.println("Iris API instance is null - Iris may not be fully initialized yet");
                  return false;
               }

               isShaderPackInUseMethod = irisApiInstance.getClass().getMethod("isShaderPackInUse");
            }

            return (Boolean)isShaderPackInUseMethod.invoke(irisApiInstance);
         } catch (Exception var2) {
            System.err.println("Failed to check Iris shader status: " + var2.getMessage());
            irisApiInstance = null;
            isShaderPackInUseMethod = null;
            return false;
         }
      }
   }

   public static boolean isIrisAvailable() {
      if (irisAvailable == null) {
         irisAvailable = FabricLoader.getInstance().isModLoaded("iris");
      }

      return irisAvailable;
   }

   public static void reset() {
      irisApiInstance = null;
      isShaderPackInUseMethod = null;
      initializationAttempted = false;
   }
}

package com.holdmylua.source.global;

import com.holdmylua.source.model.ModelPartAnimator;
import com.holdmylua.source.patricles.Particle;
import com.holdmylua.source.scripting.custom_api.DebugTextRenderer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class GlobalsStorage {
   public static HashMap<String, Boolean> renderAsBlock = new HashMap<>();
   public static HashMap<String, Boolean> translateItem = new HashMap<>();
   public static HashMap<String, Boolean> applyBlockRotation = new HashMap<>();
   public static HashMap<String, Integer> itemSwingSpeed = new HashMap<>();
   public static HashMap<String, Object> useDuration = new HashMap<>();
   public static HashMap<String, Boolean> usingItem = new HashMap<>();
   public static final HashMap<String, Object> registry = new HashMap<>();
   public static final List<Particle> particles = new ArrayList<>();
   public static final ModelPartAnimator modelPartAnimator = new ModelPartAnimator();
   public static final DebugTextRenderer debugTextRenderer = new DebugTextRenderer();
   public static ItemStack mainHandItem = Items.AIR.getDefaultInstance();
   public static ItemStack offHandItem = Items.AIR.getDefaultInstance();
   public static ItemStack renderedStack = Items.AIR.getDefaultInstance();
}

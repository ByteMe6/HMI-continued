package com.holdmylua.source.scripting.script_wrappers;

import com.holdmylua.source.annotation.Safe;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class S {
   @Safe
   public void playSound(String id, double volume) {
      Minecraft.getInstance().player.playSound(SoundEvent.createVariableRangeEvent(Identifier.withDefaultNamespace(id)), (float)volume, 1.0F);
   }
}

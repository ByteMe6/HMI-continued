package com.holdmylua.source.scripting.custom_api;

import com.holdmylua.source.annotation.Safe;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;

public class Sound {
   private SoundEvent sound;

   @Safe
   public void play(float volume, float pitch) {
      Minecraft.getInstance().player.playSound(this.sound, volume, pitch);
   }
}

package com.holdmylua.source.access;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.level.block.state.BlockState;

public interface AlternateBlockRenderer {
   void renderSingleBlockWithEmission(BlockState var1, PoseStack var2, SubmitNodeCollector var3, int var4, ClientLevel var5, AbstractClientPlayer var6);
}

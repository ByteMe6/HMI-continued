package com.holdmylua.source.mixin.render;

import com.holdmylua.source.LuaTestHMI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.FishingHookRenderer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({FishingHookRenderer.class})
public abstract class FishingBobberMixin {
   private EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
   float DEG_TO_RAD = (float) (Math.PI / 180.0);

   @Inject(
      method = {"getPlayerHandPos"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void getHandPos(Player player, float f, float tickProgress, CallbackInfoReturnable<Vec3> cir) {
      float m = 960.0F / ((Integer)this.dispatcher.options.fov().get()).intValue();
      int i = FishingHookRenderer.getHoldingArm(player) == HumanoidArm.RIGHT ? 1 : -1;
      Matrix4f matrix = FishingHookRenderer.getHoldingArm(player) == HumanoidArm.RIGHT ? LuaTestHMI.matricesMain : LuaTestHMI.matricesOff;
      matrix.translate(0.0F, 0.0F, 0.0F);
      Vector4f tipPos = new Vector4f(0.0F, 0.0F, 0.0F, 1.0F);
      tipPos.mul(matrix);
      Vec3 worldPos = new Vec3(tipPos.x(), tipPos.y(), tipPos.z());
      worldPos.scale(m);
      if (this.dispatcher.options.getCameraType().isFirstPerson() && player == Minecraft.getInstance().player) {
         Vec3 vec3d = this.dispatcher.camera.getNearPlane().getPointOnPlane(0.7F, 0.0F).scale(m).add(worldPos);
         cir.setReturnValue(this.dispatcher.camera.position().add(vec3d));
      }
   }

   private void getHandPose(Player player, float f, float tickProgress, CallbackInfoReturnable<Vec3> cir) {
      float m = 960.0F / ((Integer)this.dispatcher.options.fov().get()).intValue();
      int i = FishingHookRenderer.getHoldingArm(player) == HumanoidArm.RIGHT ? 1 : -1;
      Matrix4f matrix = FishingHookRenderer.getHoldingArm(player) == HumanoidArm.RIGHT ? LuaTestHMI.matricesMain : LuaTestHMI.matricesOff;
      matrix.translate(0.0F, 0.0F, 0.0F);
      Vector4f tipPos = new Vector4f(0.0F, 0.0F, 0.0F, 1.0F);
      tipPos.mul(matrix);
      Vec3 worldPos = new Vec3(tipPos.x(), tipPos.y(), tipPos.z());
      worldPos.scale(m);
      if (this.dispatcher.options.getCameraType().isFirstPerson() && player == Minecraft.getInstance().player) {
         Vec3 vec3d = this.dispatcher.camera.getNearPlane().getPointOnPlane(0.7F, 0.0F).scale(m).add(worldPos);
         cir.setReturnValue(this.dispatcher.camera.position().add(vec3d));
      }
   }
}

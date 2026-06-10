package com.holdmylua.source.mixin.player;

import com.holdmylua.source.access.CameraAccessor;
import com.mojang.math.Axis;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Camera.class})
public abstract class CameraMixin implements CameraAccessor {
   @Shadow
   @Final
   private Quaternionf rotation;
   @Shadow
   private float xRot;
   @Shadow
   private float yRot;
   @Shadow
   @Final
   private static Vector3f FORWARDS;
   @Shadow
   @Final
   private static Vector3f UP;
   @Shadow
   @Final
   private static Vector3f LEFT;
   @Shadow
   @Final
   private Vector3f forwards;
   @Shadow
   @Final
   private Vector3f up;
   @Shadow
   @Final
   private Vector3f left;
   @Shadow
   private Vec3 position;
   @Unique
   private float pitchM = 0.0F;
   @Unique
   private float yawM = 0.0F;
   @Unique
   private float rollM = 0.0F;
   @Unique
   private float xM = 0.0F;
   @Unique
   private float yM = 0.0F;
   @Unique
   private float zM = 0.0F;

   @Shadow
   protected abstract void setPosition(Vec3 var1);

   @Override
   public void hMI5_0$applyRotation() {
      this.rotation.add(Axis.XP.rotationDegrees(this.pitchM));
      this.rotation.add(Axis.YP.rotationDegrees(this.yawM));
      this.rotation.add(Axis.ZP.rotationDegrees(this.rollM));
   }

   @Override
   public void hMI5_0$setRotationValues(float pitch, float yaw, float roll) {
      this.pitchM = pitch;
      this.yawM = yaw;
      this.rollM = roll;
   }

   @Override
   public void hMI5_0$setPosValues(float x, float y, float z) {
      this.xM = x;
      this.yM = y;
      this.zM = z;
   }

   @Inject(
      method = {"setRotation"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void setRotationMixin(float yaw, float pitch, CallbackInfo ci) {
      if (!FabricLoader.getInstance().isModLoaded("do_a_barrel_roll")) {
         this.xRot = pitch;
         this.yRot = yaw;
         this.rotation
            .rotationYXZ(
               (float) Math.PI - (yaw + this.yawM) * (float) (Math.PI / 180.0),
               (-pitch + this.pitchM) * (float) (Math.PI / 180.0),
               this.rollM * (float) (Math.PI / 180.0)
            );
         FORWARDS.rotate(this.rotation, this.forwards);
         UP.rotate(this.rotation, this.up);
         LEFT.rotate(this.rotation, this.left);
         ci.cancel();
      }
   }

   @Inject(
      method = {"setPosition(DDD)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void setRotationMixin(double x, double y, double z, CallbackInfo ci) {
      if (!FabricLoader.getInstance().isModLoaded("do_a_barrel_roll")) {
         x += this.xM;
         y += this.yM;
         z += this.zM;
         this.setPosition(new Vec3(x, y, z));
         ci.cancel();
      }
   }
}

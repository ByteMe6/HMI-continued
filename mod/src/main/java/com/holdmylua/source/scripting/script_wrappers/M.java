package com.holdmylua.source.scripting.script_wrappers;

import com.holdmylua.source.annotation.Safe;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public class M {
   public double PI = (float) Math.PI;

   @Safe
   public void scale(PoseStack matrices, double x, double y, double z) {
      matrices.scale((float)x, (float)y, (float)z);
   }

   @Safe
   public void push(PoseStack matrices) {
      matrices.pushPose();
   }

   @Safe
   public void pop(PoseStack matrices) {
      matrices.popPose();
   }

   @Safe
   public void moveX(PoseStack matrices, double amount) {
      matrices.translate(amount, 0.0, 0.0);
   }

   @Safe
   public void moveY(PoseStack matrices, double amount) {
      matrices.translate(0.0, amount, 0.0);
   }

   @Safe
   public void moveZ(PoseStack matrices, double amount) {
      matrices.translate(0.0, 0.0, amount);
   }

   @Safe
   public void translate(PoseStack matrices, double x, double y, double z) {
      matrices.translate(x, y, z);
   }

   @Safe
   public void rotateX(PoseStack matrices, double amount) {
      matrices.mulPose(Axis.XP.rotationDegrees((float)amount));
   }

   @Safe
   public void rotateY(PoseStack matrices, double amount) {
      matrices.mulPose(Axis.YP.rotationDegrees((float)amount));
   }

   @Safe
   public void rotateZ(PoseStack matrices, double amount) {
      matrices.mulPose(Axis.ZP.rotationDegrees((float)amount));
   }

   @Safe
   public void rotateX(PoseStack matrices, double amount, double x, double y, double z) {
      matrices.rotateAround(Axis.XP.rotationDegrees((float)amount), (float)x, (float)y, (float)z);
   }

   @Safe
   public void rotateY(PoseStack matrices, double amount, double x, double y, double z) {
      matrices.rotateAround(Axis.YP.rotationDegrees((float)amount), (float)x, (float)y, (float)z);
   }

   @Safe
   public void rotateZ(PoseStack matrices, double amount, double x, double y, double z) {
      matrices.rotateAround(Axis.ZP.rotationDegrees((float)amount), (float)x, (float)y, (float)z);
   }

   @Safe
   public double sin(double a) {
      return Math.sin(a);
   }

   @Safe
   public double cos(double a) {
      return Math.cos(a);
   }

   @Safe
   public double clamp(double a, double min, double max) {
      return Math.clamp(a, min, max);
   }

   @Safe
   public double floor(double a) {
      return Math.floor(a);
   }

   @Safe
   public double abs(double a) {
      return Math.abs(a);
   }

   @Safe
   public double lerp(double a, double start, double end) {
      return Mth.lerp(a, start, end);
   }

   @Safe
   public double pow(double a, double b) {
      return Math.pow(a, b);
   }

   @Safe
   public double ceil(double a) {
      return Math.ceil(a);
   }

   @Safe
   public double round(double a) {
      return Math.round(a);
   }

   @Safe
   public void shear(PoseStack matrices, double shearX, double shearY, double shearZ) {
      Matrix4f shearMatrix = new Matrix4f(
         1.0F, (float)shearX, (float)shearX, 0.0F, (float)shearY, 1.0F, (float)shearY, 0.0F, (float)shearZ, (float)shearZ, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F
      );
      matrices.last().pose().mul(shearMatrix);
   }
}

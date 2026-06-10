package com.holdmylua.source.scripting.script_wrappers;

import com.holdmylua.source.annotation.Safe;

public class Easings {
   @Safe
   public double easeInOutBack(double x) {
      double c1 = 1.70158F;
      double c2 = c1 * 1.525F;
      return x < 0.5
         ? Math.pow(2.0 * x, 2.0) * ((c2 + 1.0) * 2.0 * x - c2) / 2.0
         : (Math.pow(2.0 * x - 2.0, 2.0) * ((c2 + 1.0) * (x * 2.0 - 2.0) + c2) + 2.0) / 2.0;
   }

   @Safe
   public double easeInSine(double x) {
      return 1.0 - Math.cos(x * Math.PI / 2.0);
   }

   @Safe
   public double easeOutSine(double x) {
      return Math.sin(x * Math.PI / 2.0);
   }

   @Safe
   public double easeInOutSine(double x) {
      return -(Math.cos(Math.PI * x) - 1.0) / 2.0;
   }

   @Safe
   public double easeInQuad(double x) {
      return x * x;
   }

   @Safe
   public double easeOutQuad(double x) {
      return 1.0 - (1.0 - x) * (1.0 - x);
   }

   @Safe
   public double easeInOutQuad(double x) {
      return x < 0.5 ? 2.0 * x * x : 1.0 - Math.pow(-2.0 * x + 2.0, 2.0) / 2.0;
   }

   @Safe
   public double easeInCubic(double x) {
      return x * x * x;
   }

   @Safe
   public double easeOutCubic(double x) {
      return 1.0 - Math.pow(1.0 - x, 3.0);
   }

   @Safe
   public double easeInOutCubic(double x) {
      return x < 0.5 ? 4.0 * x * x * x : 1.0 - Math.pow(-2.0 * x + 2.0, 3.0) / 2.0;
   }

   @Safe
   public double easeInQuart(double x) {
      return x * x * x * x;
   }

   @Safe
   public double easeOutQuart(double x) {
      return 1.0 - Math.pow(1.0 - x, 4.0);
   }

   @Safe
   public double easeInOutQuart(double x) {
      return x < 0.5 ? 8.0 * x * x * x * x : 1.0 - Math.pow(-2.0 * x + 2.0, 4.0) / 2.0;
   }

   @Safe
   public double easeInQuint(double x) {
      return x * x * x * x * x;
   }

   @Safe
   public double easeOutQuint(double x) {
      return 1.0 - Math.pow(1.0 - x, 5.0);
   }

   @Safe
   public double easeInOutQuint(double x) {
      return x < 0.5 ? 16.0 * x * x * x * x * x : 1.0 - Math.pow(-2.0 * x + 2.0, 5.0) / 2.0;
   }

   @Safe
   public double easeInExpo(double x) {
      return x == 0.0 ? 0.0 : Math.pow(2.0, 10.0 * x - 10.0);
   }

   @Safe
   public double easeOutExpo(double x) {
      return x == 1.0 ? 1.0 : 1.0 - Math.pow(2.0, -10.0 * x);
   }

   @Safe
   public double easeInOutExpo(double x) {
      return x == 0.0 ? 0.0 : (x == 1.0 ? 1.0 : (x < 0.5 ? Math.pow(2.0, 20.0 * x - 10.0) / 2.0 : (2.0 - Math.pow(2.0, -20.0 * x + 10.0)) / 2.0));
   }

   @Safe
   public double easeInCirc(double x) {
      return 1.0 - Math.sqrt(1.0 - Math.pow(x, 2.0));
   }

   @Safe
   public double easeOutCirc(double x) {
      return Math.sqrt(1.0 - Math.pow(x - 1.0, 2.0));
   }

   @Safe
   public double easeInOutCirc(double x) {
      return x < 0.5 ? (1.0 - Math.sqrt(1.0 - Math.pow(2.0 * x, 2.0))) / 2.0 : (Math.sqrt(1.0 - Math.pow(-2.0 * x + 2.0, 2.0)) + 1.0) / 2.0;
   }

   @Safe
   public double easeInBack(double x) {
      double c1 = 1.70158F;
      double c3 = c1 + 1.0;
      return c3 * x * x * x - c1 * x * x;
   }

   @Safe
   public double easeOutBack(double x) {
      double c1 = 1.70158F;
      double c3 = c1 + 1.0;
      return 1.0 + c3 * Math.pow(x - 1.0, 3.0) + c1 * Math.pow(x - 1.0, 2.0);
   }

   @Safe
   public double easeInElastic(double x) {
      double c4 = Math.PI * 2.0 / 3.0;
      return x == 0.0 ? 0.0 : (x == 1.0 ? 1.0 : -Math.pow(2.0, 10.0 * x - 10.0) * Math.sin((x * 10.0 - 10.75) * c4));
   }

   @Safe
   public double easeOutElastic(double x) {
      double c4 = Math.PI * 2.0 / 3.0;
      return x == 0.0 ? 0.0 : (x == 1.0 ? 1.0 : Math.pow(2.0, -10.0 * x) * Math.sin((x * 10.0 - 0.75) * c4) + 1.0);
   }

   @Safe
   public double easeInOutElastic(double x) {
      double c5 = Math.PI * 4.0 / 9.0;
      return x == 0.0
         ? 0.0
         : (
            x == 1.0
               ? 1.0
               : (
                  x < 0.5
                     ? -(Math.pow(2.0, 20.0 * x - 10.0) * Math.sin((20.0 * x - 11.125) * c5)) / 2.0
                     : Math.pow(2.0, -20.0 * x + 10.0) * Math.sin((20.0 * x - 11.125) * c5) / 2.0 + 1.0
               )
         );
   }

   @Safe
   public double easeOutBounce(double x) {
      double n1 = 7.5625;
      double d1 = 2.75;
      if (x < 1.0 / d1) {
         return n1 * x * x;
      } else if (x < 2.0 / d1) {
         double var9;
         return n1 * (var9 = x - 1.5 / d1) * var9 + 0.75;
      } else {
         double var7;
         double var8;
         return x < 2.5 / d1 ? n1 * (var7 = x - 2.25 / d1) * var7 + 0.9375 : n1 * (var8 = x - 2.625 / d1) * var8 + 0.984375;
      }
   }

   @Safe
   public double easeInBounce(double x) {
      return 1.0 - this.easeOutBounce(1.0 - x);
   }

   @Safe
   public double easeInOutBounce(double x) {
      return x < 0.5 ? (1.0 - this.easeOutBounce(1.0 - 2.0 * x)) / 2.0 : (1.0 + this.easeOutBounce(2.0 * x - 1.0)) / 2.0;
   }

   @Safe
   public double cubicEase(double t) {
      return t * t * (3.0 - 2.0 * t);
   }
}

-- hand_relative_pose.lua

local l = (context.bl and 1) or -1


global.foodCount = 0.0;
global.mainHandSwitch = 0.0;
global.offHandSwitch = 0.0;
global.drinkCount = 0.0;

if I:isEmpty(context.item) and drinkCount > 0 then
    M:moveX(context.matrices, 1.5 * l)
    M:moveY(context.matrices, -0.3)
    M:moveZ(context.matrices, -0.47)
    M:rotateX(context.matrices, 15, 0.5 * l, 0.5, 0.5)
    M:rotateY(context.matrices, 35 * l, 0.5 * l, 0.5, 0.5)
    M:rotateZ(context.matrices, -65 * l, 0.5 * l, 0.5, 0.5)
    M:scale(context.matrices, 0.9, 0.9, 0.9)
end

local switch_val = (context.mainHand and mainHandSwitch) or offHandSwitch
local switchAnimationVariable = Easings:easeInBack(M:sin(M:clamp(switch_val, 0.09723, 0.60632) * 3.24 * 1.65 - 0.1))

if (I:isIn(context.item, Tags:getVanillaTag("bundles")) or I:isOf(context.item, Items:get("minecraft:ender_pearl")) or I:isOf(context.item, Items:get("minecraft:ender_eye")) or I:isThrowable(context.item) or I:isIn(context.item, Tags:getFabricTag("music_discs")) or I:isIn(context.item, Tags:getFabricTag("nuggets")) or I:isIn(context.item, Tags:getVanillaTag("skulls"))) and I:getUseAction(context.item) ~= "trident" then
    M:rotateX(context.matrices, 10 * switchAnimationVariable)
    M:rotateZ(context.matrices, 6 * switchAnimationVariable)
end

local musicDiscHandTilt
if mainHandSwitch < 0.65245 then
    musicDiscHandTilt = M:sin(M:clamp(mainHandSwitch, 0, 0.16675) * 3.14 * 3)
else
    musicDiscHandTilt = M:sin(M:clamp(mainHandSwitch, 0.65245, 1) * 4.4 - 1.3)
end

local musicDiscHandJump = M:sin(M:clamp(mainHandSwitch, 0.52459, 0.85809) * 3.14 * 3 - 1.8)

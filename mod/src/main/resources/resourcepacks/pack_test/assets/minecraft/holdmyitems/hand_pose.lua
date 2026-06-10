-- hand_pose.lua


local player = context.player
local useAction = I:getUseAction(context.item)
local isUsingItem = P:isUsingItem(player)
local activeHand = P:getActiveHand(player)
local mat = context.matrices
local pSpeed = P:getSpeed(player)

if useAction == "spear" then
    context.equipProgress = 0
end
local l = (context.bl and 1) or -1

function easeCustom(t)
    local t2 = t * t
    local t3 = t2 * t
    return 3 * t * (1 - t) * (1 - t) * 0.44 +
            3 * t2 * (1 - t) * 1 + -- 84
            t3
    --[[
    const t2 = t * t;
    const t3 = t2 * t;
    const mt = 1 - t;
    const mt2 = mt * mt;

    return 3 * .66 * t * mt2 +
           3 * 0.81 * t2 * mt +
           t3;
    ]]
end

function easeCustomSec(t)
    local t2 = t * t
    local t3 = t2 * t
    return 3 * t * (1 - t) * (1 - t) * 0.44 +
            3 * t2 * (1 - t) * 0.94 +
            t3
end

local GRAVITY = 0.1
local DAMPING = 0.82
local INTENSITY = 0.27

global.isChargedM = false;
global.isChargedO = false;

global.shootCM = 0;
global.shootCO = 0;


global.riptideCounter = 0;
global.riptideCounterO = 0;

global.inWaterCount = 0;


global.inspectionCounter = 0.0;
global.inspectionSpin = 0.0;
global.isMapHeldBelow = false;
global.mapTransition = 0.0;
global.mapSmoother = 0.0;
global.mapZoomer = 0.0;
global.shieldDisable = 0.0;
global.foodSpeed = 0.0;
global.pitchAngleO = 0.0;
global.yawAngleO = 0.0;
global.pitchAngle = 0.0;
global.yawAngle = 0.0;
global.brushCounter = 0.0;
global.brushCounterO = 0.0;
global.smoothingCrawl = 0.0;
global.crawlDefaulPos = 0.0;
global.swimSmoother = 0.0;
global.bowWiggle = 0.0;
global.bowWiggleO = 0.0;
global.bowCountO = 0.0;
global.bowCountSecO = 0.0;
global.bowCount = 0.0;
global.bowCountSec = 0.0;
global.tridentMO = 0.0;
global.trident = 0.0;
global.tridentO = 0.0;
global.tridentJO = 0.0;
global.tridentM = 0.0;
global.tridentJ = 0.0;
global.shieldM = 0.0;
global.shieldO = 0.0;
global.walk = 0.0;
global.walkSmoother = 0.0;
global.fall = 0.0;
global.fallSpeed = 0.0;
global.sneak = 0.0;
global.a = 0.0;
global.smoothing = 0.0;
global.crawler = 0.0;
global.offhand = 0.0;
global.crossBowM = 0.0;
global.crossBowSecM = 0.0;
global.crossBowO = 0.0;
global.crossBowSecO = 0.0;
global.foodCount = 0.0;
global.foodCountSec = 0.0;
global.foodCountO = 0.0;
global.foodCountSecO = 0.0;
global.drinkCount = 0.0;
global.drinkCountO = 0.0;
global.crwl = 0.0;
global.mainHandSwitch = 0.0;
global.offHandSwitch = 0.0;
global.swordAttack = false;
global.swordAttack2 = false;
global.swimCounter = 0.0;
global.prevSwingM = false;

global.waterWalk = 0;

global.tilting = 0.0;

global.usingOffBowPrev = false;

global.spearCounterM = 0;
global.spearUsageTime = 0;

global.canDismountCounter = 0;
global.canKnockbackCounter = 0;

global.spearCounterO = 0;

global.canDismountCounterO = 0;
global.canKnockbackCounterO = 0;

global.hitImpactCounter = 0;
global.hitImpactCounterO = 0;

global.regularSwing = 1;
global.swordSwing = 1;
global.pickaxeSwing = 1;
global.shovelSwing = 1;
global.generalSwing = 1;
global.axeSwing = 1;
global.tridentSwing = 1;
global.bowAnimation = 1;
global.crossBowAnimation = 1;
global.tridentAnimation = 1;
global.drinkingAnimation = 1;
global.mainHandSwitchingAnimation = 1;
global.offHandSwitchingAnimation = 1;
global.shieldAnimation = 1;
global.brushAnimation = 1;
global.swimAnimation = 1;
global.crawlAnimation = 1;
global.climbAnimation = 1;
global.foodAnimation = 1;

if I:isIn(context.item, Tags:getVanillaTag("pickaxes")) then
context.swingProgress = easeCustom(context.swingProgress)
else
context.swingProgress = easeCustomSec(context.swingProgress)
end

M:moveX(mat, 0.2 * l)

local swing_rot
if context.swingProgress < 0.70016 then
swing_rot = M:sin(M:clamp(context.swingProgress, 0, 0.308) * 5.1)
else
swing_rot = M:sin(M:clamp(context.swingProgress, 0.70016, 1) * 5.1 - 2)
end

local swing_sword_tilt
if context.swingProgress < 0.65245 then
swing_sword_tilt = M:sin(M:clamp(context.swingProgress, 0, 0.16675) * 3.14 * 3)
else
swing_sword_tilt = M:sin(M:clamp(context.swingProgress, 0.65245, 1) * 4.4 - 1.2584)
end

swing_rot = swing_rot * swing_rot * swing_rot
local swing = M:clamp(M:sin(context.swingProgress * 4.78), 0, 1)
local swing_hit = M:sin(M:clamp(context.swingProgress, 0.16561, 0.49422) * 4.78 * 2 + 4.7)

local swing_hit_second
if context.swingProgress < 0.65594 then
swing_hit_second = M:sin(M:clamp(context.swingProgress, 0.16561, 0.32991) * 4.78 * 2 + 4.7)
else
swing_hit_second = M:sin(M:clamp(context.swingProgress, 0.65594, 0.82025) * 4.78 * 2 - 4.7)
end

local usingOffBow
-- -------------------------Offhand Bow Counter (placing the hands in the ready to shoot position)--------
if isUsingItem and useAction == "bow" and not context.mainHand and activeHand == context.hand then -- Start counting if player is using renderedItem by his offhand & renderedItem useAction is "bow"
    bowCountO = bowCountO + 0.075 * context.deltaTime * 30
    usingOffBow = true;
elseif not context.mainHand then -- Decrease the counter only if using renderedItem condition is not true. Decreasing starts after pulling counter (bowSecO) reaches zero for better timing
    bowCountO = bowCountO - 0.07 * context.deltaTime * 30
    usingOffBow = false
end
bowCountO = M:clamp(bowCountO, 0, 1) -- Limit the counter from 0 to 1
-- -------------------------Offhand secondary bow counter (pulling)----------------------------------------------------------
if isUsingItem and useAction == "bow" and not context.mainHand and activeHand == context.hand and bowCountO == 1 then -- Same as bowCountO but starts only when bowCountO (ready to shoot pos) reaches 1
    bowCountSecO = bowCountSecO + 0.025 * context.deltaTime * 30
    bowWiggleO = bowWiggleO + 0.07 * context.deltaTime * 30
elseif not context.mainHand then -- Same as bowCountO but doesn't rely on other counter
    bowCountSecO = bowCountSecO - 0.11 * context.deltaTime * 30
end
bowCountSecO = M:clamp(bowCountSecO, 0, 1) -- Limit the counter from 0 to 1(it's the last time i will say this XD)

-- ------------Two exactly same bow counters with only difference being the context.hand (offhand/main)----------

if isUsingItem and useAction == "bow" and context.mainHand and activeHand == context.hand then
    bowCount = bowCount + 0.075 * context.deltaTime * 30
elseif context.mainHand then
    bowCount = bowCount - 0.07 * context.deltaTime * 30
end
bowCount = M:clamp(bowCount, 0, 1)

if isUsingItem and useAction == "bow" and context.mainHand and activeHand == context.hand and bowCount == 1 then
    bowCountSec = bowCountSec + 0.025 * context.deltaTime * 30
    bowWiggle = bowWiggle + 0.07 * context.deltaTime * 30
elseif context.mainHand then
    bowCountSec = 0
end
bowCountSec = M:clamp(bowCountSec, 0, 1)
-- ----------------END END---------------

local ptAngle = (context.mainHand and pitchAngle) or pitchAngleO
local ywAngle = (context.mainHand and yawAngle) or yawAngleO


if pSpeed > 0.05 then
    waterWalk = waterWalk + pSpeed * 2 * context.deltaTime * 30
end

if P:isTouchingWater(player) then
    inWaterCount = inWaterCount + 0.07 * context.deltaTime * 30
else
    inWaterCount = inWaterCount - 0.07 * context.deltaTime * 30
end
inWaterCount = M:clamp(inWaterCount, 0, 1)


local xOffset = ${xOffset}
M:translate(mat, ${xOffset} * l, ${yOffset}, ${zOffset})
tilting = tilting + context.swingProgress * context.deltaTime * 3

if not I:isEmpty(context.item) then
M:moveZ(mat, -0.16)
else
M:moveZ(mat, -0.08)
end

if I:isOf(context.item, Items:get("minecraft:filled_map")) and context.mainHand and I:isEmpty(P:getOffhandItem(player)) then
mapSmoother = mapSmoother + 0.07 * context.deltaTime * 30
elseif I:isOf(context.item, Items:get("minecraft:filled_map")) then
mapSmoother = mapSmoother - 0.07 * context.deltaTime * 30
end
mapSmoother = M:clamp(mapSmoother, 0, 1)

if context.mainHandSwitchEvent and context.mainHand and drinkCount == 0 then
mainHandSwitch = 0
end
mainHandSwitch = mainHandSwitch + 0.015 * context.deltaTime * 30
mainHandSwitch = M:clamp(mainHandSwitch, 0, 1)

-- if context.mainHand then
-- 	context.equipProgress = context.equipProgress * mainHandSwitch
-- end

if context.mainHand then
local switchItems = M:sin(M:clamp(mainHandSwitch, 0, 0.5) * 3.14) * mainHandSwitchingAnimation
local switch_fast = M:sin(M:clamp(mainHandSwitch, 0, 0.125) * 12.56) * mainHandSwitchingAnimation

switchItems = Easings:easeInOutBack(switchItems)

if useAction == "trident" and tridentM > 0.9 then
M:translate(mat, 0, -0.15 * switch_fast, -0.3 * switch_fast)
M:rotateX(mat, 75 * switch_fast, 0.3 * l, -0.4, 0)
M:rotateX(mat, -75 * switchItems, 0.3 * l, -0.4, 0)
M:translate(mat, 0, 0.15 * switch_fast, 0.3 * switch_fast)
else
if useAction == "crossbow" then
M:moveY(mat, -0.25 * switch_fast)
end
M:translate(mat, 0 * switch_fast, 0, -0.2 * switch_fast)
M:rotateY(mat, 25 * l * switch_fast, 0.3 * l, -0.4, 0)
M:rotateX(mat, -55 * switch_fast, 0.3 * l, -0.4, 0)
M:rotateZ(mat, 40 * l * switch_fast, 0.3 * l, -0.4, 0)

M:rotateZ(mat, -40 * l * switchItems, 0.3 * l, -0.4, 0)
M:rotateX(mat, 55 * switchItems, 0.3 * l, -0.4, 0)
M:rotateY(mat, -25 * l * switchItems, 0.3 * l, -0.4, 0)
if useAction == "crossbow" then
M:moveY(mat, 0.25 * switchItems)
end
M:translate(mat, -0 * switchItems, 0, 0.2 * switchItems)
end
end

if context.offHandSwitchEvent then
offHandSwitch = 0
end
offHandSwitch = offHandSwitch + 0.015 * context.deltaTime * 30
offHandSwitch = M:clamp(offHandSwitch, 0, 1)

if not context.mainHand and foodCountO == 0 then
local switchItems = M:sin(M:clamp(offHandSwitch, 0, 0.5) * 3.14) * offHandSwitchingAnimation
local switch_fast = M:sin(M:clamp(offHandSwitch, 0, 0.125) * 12.56) * offHandSwitchingAnimation

switchItems = Easings:easeInOutBack(switchItems)

if useAction == "crossbow" then
M:moveY(mat, -0.25 * switch_fast)
end
M:translate(mat, 0 * switch_fast, 0, -0.2 * switch_fast)
M:rotateY(mat, 25 * switch_fast * l, 0.3 * l, -0.4, 0)
M:rotateX(mat, -55 * switch_fast, 0.3 * l, -0.4, 0)
M:rotateZ(mat, 40 * switch_fast * l, 0.3 * l, -0.4, 0)

M:rotateZ(mat, -40 * switchItems * l, 0.3 * l, -0.4, 0)
M:rotateX(mat, 55 * switchItems, 0.3 * l, -0.4, 0)
M:rotateY(mat, -25 * switchItems * l, 0.3 * l, -0.4, 0)
if useAction == "crossbow" then
M:moveY(mat, 0.25 * switch_fast)
end
M:translate(mat, -0 * switchItems, 0, 0.2 * switchItems)
end

if not P:isOnGround(player) and context.mainHandSwingProgress == 0 and context.mainHand then
swordAttack = false
elseif context.mainHand and context.mainHandSwingProgress == 0 then
swordAttack = true
end
if prevSwingM ~= context.swingMHand and context.mainHand then
swordAttack2 = not swordAttack2
end

if P:isCrawling(player) and pSpeed > 0.08 then
crwl = crwl + pSpeed * context.deltaTime * 30
end
if P:getPitch(player) > 40 then
mapZoomer = mapZoomer + 0.05 * context.deltaTime * 30
else
mapZoomer = mapZoomer - 0.095 * context.deltaTime * 30
end
mapZoomer = M:clamp(mapZoomer, 0, 1)
local prevSpearUsageTime = spearUsageTime
local prevKnockback = canKnockbackCounter
local prevDismount = canDismountCounter

local prevKnockbackO = canKnockbackCounterO
local prevDismountO = canDismountCounterO
if isUsingItem and useAction == "spear" and activeHand == context.hand and context.mainHand and I:getSpearData(context.item).canDamage then
    spearCounterM = spearCounterM + 0.08 * context.deltaTime * 30
    if not I:getSpearData(context.item).canDismount then
        canDismountCounter = canDismountCounter + 0.035 * context.deltaTime * 30
    end

    if not I:getSpearData(context.item).canKnockback then
        canKnockbackCounter = canKnockbackCounter + 0.055 * context.deltaTime * 30
    end

    if I:getSpearData(context.item).hitImpact then
        hitImpactCounter = hitImpactCounter + 0.08 * context.deltaTime * 30
    end
    if hitImpactCounter > 0 then
        hitImpactCounter = hitImpactCounter + 0.08 * context.deltaTime * 30
    end
elseif context.mainHand then
    spearCounterM = spearCounterM - 0.08 * context.deltaTime * 30
    canDismountCounter = 0
    canKnockbackCounter = 0
    spearUsageTime = 0;
    hitImpactCounter = 0
end
if hitImpactCounter >= 1 then
    hitImpactCounter = 0
end
spearCounterM = M:clamp(spearCounterM, 0, 1) *  M:clamp(1 - M:clamp(swing * 8, 0, 1), 0, 1)
canDismountCounter = M:clamp(canDismountCounter, 0, 1)
canKnockbackCounter = M:clamp(canKnockbackCounter, 0, 1)
-----------------------
if isUsingItem and useAction == "spear" and activeHand == context.hand  and not context.mainHand and I:getSpearData(context.item).canDamage then
    spearCounterO = spearCounterO + 0.08 * context.deltaTime * 30
    if not I:getSpearData(context.item).canDismount then
        canDismountCounterO = canDismountCounterO + 0.035 * context.deltaTime * 30
    end

    if not I:getSpearData(context.item).canKnockback then
        canKnockbackCounterO = canKnockbackCounterO + 0.055 * context.deltaTime * 30
    end
elseif not context.mainHand then
    spearCounterO = spearCounterO - 0.08 * context.deltaTime * 30
    canDismountCounterO = 0
    canKnockbackCounterO = 0
    spearUsageTime = 0;
end
spearCounterO = M:clamp(spearCounterO, 0, 1) * M:clamp(1 - M:clamp(swing * 8, 0, 1), 0, 1)
canDismountCounterO = M:clamp(canDismountCounterO, 0, 1)
canKnockbackCounterO = M:clamp(canKnockbackCounterO, 0, 1)

if canDismountCounter == 0 then
    canDismountCounter = M:lerp(0.5 * context.deltaTime * 30, prevDismount, canDismountCounter)
end
if canKnockbackCounter == 0 then
    canKnockbackCounter = M:lerp(0.5 * context.deltaTime * 30, prevKnockback, canKnockbackCounter)
end

if canDismountCounterO == 0 then
    canDismountCounterO = M:lerp(0.5 * context.deltaTime * 30, prevDismountO, canDismountCounterO)
end
if canKnockbackCounterO == 0 then
    canKnockbackCounterO = M:lerp(0.5 * context.deltaTime * 30, prevKnockbackO, canKnockbackCounterO)
end

--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
if isUsingItem and (useAction == "eat" or useAction == "drink" or useAction == "toot_horn") and context.mainHand and activeHand == context.hand then
foodCount = foodCount + 0.1 * context.deltaTime * 30
elseif context.mainHand then
foodCount = foodCount - 0.1 * context.deltaTime * 30
end
foodCount = M:clamp(foodCount, 0, 1)

if isUsingItem and (useAction == "eat" or useAction == "drink" or useAction == "toot_horn" or useAction == "brush") and context.mainHand and activeHand == context.hand and (foodCount == 1 or brushCounter > 0.4) then
foodCountSec = foodCountSec + 0.1 * context.deltaTime * 30
end

if isUsingItem and (useAction == "eat" or useAction == "drink" or useAction == "toot_horn") and not context.mainHand and activeHand == context.hand then
foodCountO = foodCountO + 0.1 * context.deltaTime * 30
elseif not context.mainHand then
foodCountO = foodCountO - 0.1 * context.deltaTime * 30
end
foodCountO = M:clamp(foodCountO, 0, 1)

if isUsingItem and (useAction == "eat" or useAction == "drink" or useAction == "toot_horn" or useAction == "brush") and not context.mainHand and activeHand == context.hand and (foodCountO == 1 or brushCounterO > 0.4) then
foodCountSecO = foodCountSecO + 0.1 * context.deltaTime * 30
end
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
if isUsingItem and (useAction == "drink") and context.mainHand and activeHand == context.hand and foodCount == 1 then
drinkCount = drinkCount + 0.04 * context.deltaTime * 30
elseif context.mainHand then
drinkCount = drinkCount - 0.1 * context.deltaTime * 30
end
drinkCount = M:clamp(drinkCount, 0, 1)

if isUsingItem and useAction == "drink" and not context.mainHand and activeHand == context.hand and foodCountO == 1 then
drinkCountO = drinkCountO + 0.04 * context.deltaTime * 30
elseif not context.mainHand then
drinkCountO = drinkCountO - 0.1 * context.deltaTime * 30
end
drinkCountO = M:clamp(drinkCountO, 0, 1)

if isUsingItem and useAction == "crossbow" and not context.mainHand and activeHand == context.hand then
crossBowO = crossBowO + 0.1 * context.deltaTime * 30
elseif not context.mainHand then
crossBowO = crossBowO - 0.1 * context.deltaTime * 30
end
crossBowO = M:clamp(crossBowO, 0, 1)

if isUsingItem and useAction == "crossbow" and not context.mainHand and activeHand == context.hand and crossBowO == 1 then
crossBowSecO = crossBowSecO + 0.02 * context.deltaTime * 30
elseif not context.mainHand then
crossBowSecO = crossBowSecO - 0.1 * context.deltaTime * 30
end
crossBowSecO = M:clamp(crossBowSecO, 0, 1)
-- --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
if isUsingItem and useAction == "crossbow" and context.mainHand and activeHand == context.hand and not I:isChargedCrossbow(context.item) then
crossBowM = crossBowM + 0.1 * context.deltaTime * 30
elseif context.mainHand then
crossBowM = crossBowM - 0.1 * context.deltaTime * 30
end
crossBowM = M:clamp(crossBowM, 0, 1)

if isUsingItem and useAction == "crossbow" and context.mainHand and activeHand == context.hand and crossBowM == 1 and not I:isChargedCrossbow(context.item) then
crossBowSecM = crossBowSecM + 0.02 * context.deltaTime * 30
elseif context.mainHand then
crossBowSecM = crossBowSecM - 0.1 * context.deltaTime * 30
end
crossBowSecM = M:clamp(crossBowSecM, 0, 1)

-- -------------------------Counter for hiding your offhand-------------------------
if not context.mainHand and I:isEmpty(context.item) and not (isUsingItem and not I:isChargedCrossbow(P:getMainItem(player)) and I:getUseAction(P:getMainItem(player)) ~= "block" and I:getUseAction(P:getMainItem(player)) ~= "eat" and I:getUseAction(P:getMainItem(player)) ~= "toot_horn" and I:getUseAction(P:getMainItem(player)) ~= "drink" and I:getUseAction(P:getMainItem(player)) ~= "brush" and I:getUseAction(P:getMainItem(player)) ~= "spear") and not P:isClimbing(player) and not P:isSwimming(player) and not P:isCrawling(player) then -- Start counting if renderedItem is empty & players is not using any items
offhand = offhand + 0.08 * context.deltaTime * 30
elseif not context.mainHand or (isUsingItem and not I:isChargedCrossbow(P:getMainItem(player)) and I:getUseAction(P:getMainItem(player)) ~= "block" and I:getUseAction(P:getMainItem(player)) ~= "eat" and I:getUseAction(P:getMainItem(player)) ~= "drink" and I:getUseAction(P:getMainItem(player)) ~= "toot_horn" and I:getUseAction(P:getMainItem(player)) ~= "brush" and I:getUseAction(P:getMainItem(player)) ~= "spear") or P:isClimbing(player) or P:isCrawling(player) then -- Decrease the counter if one of the conditions above is true
offhand = offhand - 0.08 * context.deltaTime * 30
end
offhand = M:clamp(offhand, 0, 1) -- Limit the counter from 0 to 1




if isUsingItem and useAction == "brush" and context.mainHand and activeHand == context.hand then
brushCounter = brushCounter + 0.1 * context.deltaTime * 30
elseif context.mainHand then
brushCounter = brushCounter - 0.1 * context.deltaTime * 30
end
brushCounter = M:clamp(brushCounter, 0, 1) * brushAnimation

if isUsingItem and useAction == "brush" and not context.mainHand and activeHand == context.hand then
brushCounterO = brushCounterO + 0.1 * context.deltaTime * 30
elseif not context.mainHand then
brushCounterO = brushCounterO - 0.1 * context.deltaTime * 30
end
brushCounterO = M:clamp(brushCounterO, 0, 1) * brushAnimation

-- --------------------------------------Offhand trident counters-------------------------------------------
if isUsingItem and useAction == "trident" and not context.mainHand and activeHand == context.hand then -- Start is the same as bow counter. The only difference being renderedItem use action "trident"
tridentMO = tridentMO + 0.07 * context.deltaTime * 30 -- Main counter for lifting the trident up
tridentO = tridentO + 0.045 * context.deltaTime * 30 -- Same
tridentJO = tridentJO + 0.1 * context.deltaTime * 30 -- Secondary one for jiggling when it's ready
elseif not context.mainHand then
if P:isUsingRiptide(player) then
tridentMO = tridentMO - 0.57 * context.deltaTime * 30
tridentO = tridentO - 0.53 * context.deltaTime * 30 -- Same

else
tridentMO = tridentMO - 0.1 * context.deltaTime * 30
tridentO = tridentO - 0.07 * context.deltaTime * 30 -- Same

end
tridentJO = tridentJO - 0.1 * context.deltaTime * 30
end
tridentMO = M:clamp(tridentMO, 0, 1)
tridentO = M:clamp(tridentO, 0, 1)

-- -------------------------------------Main context.hand trident counters-----------------------------------------
if isUsingItem and useAction == "trident" and context.mainHand and activeHand == context.hand then -- Same but for "context.mainHand"
tridentM = tridentM + 0.07 * context.deltaTime * 30 -- Same
trident = trident + 0.05 * context.deltaTime * 30 -- Same
tridentJ = tridentJ + 0.1 * context.deltaTime * 30 -- Same
elseif context.mainHand then
if P:isUsingRiptide(player) then
tridentM = tridentM - 0.57 * context.deltaTime * 30
trident = trident - 0.53 * context.deltaTime * 30 -- Same

else
tridentM = tridentM - 0.1 * context.deltaTime * 30
trident = trident - 0.07 * context.deltaTime * 30 -- Same

end

tridentJ = tridentJ - 0.1 * context.deltaTime * 30
end
trident = trident * M:pow(0.95, context.deltaTime * 30)
tridentM = M:clamp(tridentM, 0, 1)
trident = M:clamp(trident, 0, 1)

-- -------------------------------------Main context.hand shield counter-------------------------------------------
if isUsingItem and useAction == "block" and context.mainHand and activeHand == context.hand then -- Start is the same as trident counter. The only difference being renderedItem use action "shield"
if I:isIn(context.item, Tags:getVanillaTag("swords")) then
shieldM = shieldM + 0.12 * context.deltaTime * 30
else
shieldM = shieldM + 0.07 * context.deltaTime * 30
end
elseif context.mainHand then
if I:isIn(context.item, Tags:getVanillaTag("swords")) then
shieldM = shieldM - 0.12 * context.deltaTime * 30
else
shieldM = shieldM - 0.07 * context.deltaTime * 30
end
end
shieldM = shieldM - shieldDisable * 0.04 * context.deltaTime * 30
shieldM = M:clamp(shieldM, 0, 1)

-- --------------------------------------Off context.hand shield counter--------------------------------------------
if isUsingItem and useAction == "block" and not context.mainHand and activeHand == context.hand then -- Start is the same as shield counter. The only difference being renderedItem use action "context.mainHand"
shieldO = shieldO + 0.07 * context.deltaTime * 30
elseif not context.mainHand then
shieldO = shieldO - 0.07 * context.deltaTime * 30
end
shieldO = shieldO - shieldDisable * 0.04 * context.deltaTime * 30
shieldO = M:clamp(shieldO, 0, 1)

if pSpeed > 0.08 and M:abs(P:getYSpeed(player)) < 0.08 and P:isOnGround(player) then
walk = walk + pSpeed * context.deltaTime * 30
walkSmoother = walkSmoother + 0.1 * context.deltaTime * 30
else
walkSmoother = walkSmoother - 0.1 * context.deltaTime * 30
end
walkSmoother = M:clamp(walkSmoother, 0, 1)

fallSpeed = fallSpeed + (-1 * P:getYSpeed(player) + M:sin(sneak * 3.14) * 0.14 + M:sin(bowCount * 3.14) * 0.12 + M:sin(bowCountO * 3.14) * 0.12) * INTENSITY * context.deltaTime * 30
fallSpeed = fallSpeed - GRAVITY * fall * context.deltaTime * 30
fallSpeed = fallSpeed * M:pow(DAMPING, context.deltaTime * 30)
fall = fall + fallSpeed * context.deltaTime * 30

if P:isSneaking(player) then
sneak = sneak + 0.1 * context.deltaTime * 30
else
sneak = sneak - 0.1 * context.deltaTime * 30
end
sneak = M:clamp(sneak, 0, 1)
M:moveY(mat, -0.08 * sneak)
M:rotateX(mat, 4 * M:sin(sneak * 3.14), 0, -0.4, 0)

a = a + 0.04 * context.deltaTime * 30

if P:isClimbing(player) then
smoothing = smoothing + 0.1 * context.deltaTime * 30
else
smoothing = smoothing - 0.1 * context.deltaTime * 30
end
if smoothing > 1 then
smoothing = 1
end
if smoothing < 0 then
smoothing = 0
end

if P:isCrawling(player) then
smoothingCrawl = smoothingCrawl + 0.1 * context.deltaTime * 30
else
smoothingCrawl = smoothingCrawl - 0.1 * context.deltaTime * 30
end
smoothingCrawl = M:clamp(smoothingCrawl, 0, 1)

if P:isCrawling(player) and pSpeed > 0.08 then
crawlDefaulPos = crawlDefaulPos + 0.1 * context.deltaTime * 30
else
crawlDefaulPos = crawlDefaulPos - 0.06 * context.deltaTime * 30
end
crawlDefaulPos = M:clamp(crawlDefaulPos, 0, 1)

if P:isClimbing(player) and M:abs(P:getYSpeed(player)) > 0.08 then
if P:getYSpeed(player) > 0 then
crawler = crawler + P:getYSpeed(player) * context.deltaTime * 30
else
crawler = crawler + P:getYSpeed(player) / 2 * context.deltaTime * 30
end
end

if P:isSwimming(player) and not isUsingItem then
swimCounter = swimCounter + pSpeed * context.deltaTime * 30
swimSmoother = swimSmoother + 0.1 * context.deltaTime * 30
else
swimSmoother = swimSmoother - 0.1 * context.deltaTime * 30
end
swimSmoother = M:clamp(swimSmoother, 0, 1)

M:moveZ(mat, 0.3 * M:sin(swimCounter * 0.55) * swimSmoother * swimAnimation)

if I:isIn(context.item, Tags:getVanillaTag("axes")) or I:isOf(context.item, Items:get("minecraft:mace")) then
-- M:rotateZ(mat, ywAngle * -0.1, 0.2 * l, -0.3, 0)
M:rotateX(mat, (P:getPitch(player) * -0.03) + ptAngle * 0.1, 0, -0.4, 0)
else -- if (not I:isEmpty(renderedItem))
-- M:rotateZ(mat, ywAngle * -0.05, 0.2 * l, -0.3, 0)
M:rotateX(mat, (P:getPitch(player) * -0.03) + ptAngle * 0.1, 0, -0.4, 0)
end

if I:isEmpty(context.item) then
M:translate(mat, 0, -0.15 * -M:cos(swimCounter * 0.55) * swimSmoother + 0.25 * swimSmoother * swimAnimation, 0)
M:rotateY(mat, -15 * l * M:cos(swimCounter * 0.55) * swimSmoother * swimAnimation, 0.3 * l, -0.4, 0)
M:rotateX(mat, -10 * M:cos(swimCounter * 0.55) * swimSmoother * swimAnimation, 0.3 * l, -0.4, 0)
M:rotateX(mat, -30 * swimSmoother * swimAnimation, 0.3 * l, -0.4, 0)
M:rotateZ(mat, -20 * l * M:sin(swimCounter * 0.55) * swimSmoother * swimAnimation, 0.3 * l, -0.4, 0)
else
M:rotateY(mat, -15 * l * M:cos(swimCounter * 0.55) * swimSmoother * swimAnimation, 0.3 * l, -0.4, 0)
M:rotateX(mat, -10 * M:cos(swimCounter * 0.55) * swimSmoother * swimAnimation, 0.3 * l, -0.4, 0)
end

if I:isOf(context.item, Items:get("minecraft:bell")) or I:isLantern(context.item) or I:isIn(context.item, Tags:getVanillaTag("hanging_signs")) or I:isOf(context.item, Items:get("minecraft:pink_petals")) or I:isOf(context.item, Items:get("minecraft:leaf_litter")) or I:isOf(context.item, Items:get("minecraft:wildflowers")) or I:isOf(context.item, Items:get("minecraft:end_crystal")) or I:isOf(context.item, Items:get("minecraft:painting")) or I:isOf(context.item, Items:get("minecraft:item_frame")) then
if I:isOf(context.item, Items:get("minecraft:pink_petals")) or I:isOf(context.item, Items:get("minecraft:leaf_litter")) or I:isOf(context.item, Items:get("minecraft:wildflowers")) then
M:translate(mat, 0, 0.25, -0.05)
elseif I:isOf(context.item, Items:get("minecraft:end_crystal")) then
M:moveZ(mat, -0.12)
M:rotateX(mat, -10)
else
M:translate(mat, 0, -0.1, 0.05)
M:rotateX(mat, 25)
end
elseif not I:isEmpty(context.item) and useAction ~= "crossbow" then
M:moveY(mat, -0.12)
M:rotateZ(mat, -6 * l)
M:rotateX(mat, 6)
end

M:moveY(mat, 0.01 * M:sin(a)) -- Idle animation example
M:rotateX(mat, 1.1 * l * M:cos(a), 0.3 * l, -0.4, 0) -- Idle animation example
M:rotateY(mat, 0.5 * l * M:sin(a) * l, 0.3 * l, -0.4, 0) -- Idle animation example
M:rotateZ(mat, 2 * l * M:sin(a * 0.3) * l, 0.3 * l, -0.4, 0) -- Idle animation example

if I:isEmpty(context.item) and not context.mainHand then
M:translate(mat, 0, -1 * Easings:easeInOutExpo(offhand), 0.5 * Easings:easeInOutExpo(offhand))
end

local fallMul
if I:isEmpty(context.item) or I:isBlock(context.item) then
fallMul = 0.7
else
fallMul = 1
end

if I:isEmpty(context.item) then
M:moveZ(mat, 0.06 * (fall * fallMul))
end
M:rotateX(mat, 2 * (fall * fallMul), 0, -0.4, 0)
M:moveY(mat, 0.06 * fall * fallMul)

local walk_val = (context.bl and walk) or (walk - 0.5 * 1.5)
M:rotateX(mat, 1.5 * M:sin(walk_val) * walkSmoother, 0, -0.4, 0)
M:rotateY(mat, -0.5 * M:cos(walk * 1.5) * walkSmoother * l, 0, -0.4, 0)
M:rotateZ(mat, 1 * M:cos(walk * 1.5) * walkSmoother * l, 0, -0.4, 0)

if useAction == "block" and not I:isIn(context.item, Tags:getVanillaTag("swords")) then
M:translate(mat, 0.2 * l, 0, 0.1)
M:rotateY(mat, 20 * l, 0.3 * l, -0.4, 0)
end
if useAction == "block" and context.mainHand then
M:translate(mat, (-xOffset) * l * Easings:easeInOutBack(shieldM) + 0.1 * l * Easings:easeInOutBack(shieldM) * shieldAnimation, 0, 0)
if I:isIn(context.item, Tags:getVanillaTag("swords")) then
M:rotateY(mat, 50 * Easings:easeInOutBack(shieldM) * l * shieldAnimation, 0.3 * l, -0.4, 0)
else
M:rotateY(mat, 70 * Easings:easeInOutBack(shieldM) * l * shieldAnimation, 0.3 * l, -0.4, 0)
end
M:rotateX(mat, 13 * M:clamp(M:sin(shieldM * 4.14), 0, 1) * shieldAnimation, 0.3 * l, -0.4, 0)
M:rotateX(mat, -15 * Easings:easeInOutBack(shieldM) * shieldAnimation, 0.3 * l, -0.4, 0)
end
if useAction == "block" and not context.mainHand then
M:translate(mat, (-xOffset) * l * Easings:easeInOutBack(shieldO) + 0.1 * l * Easings:easeInOutBack(shieldO) * shieldAnimation, 0, 0)
if I:isIn(context.item, Tags:getVanillaTag("swords")) then
M:rotateY(mat, 50 * Easings:easeInOutBack(shieldO) * l * shieldAnimation, 0.3 * l, -0.4, 0)
else
M:rotateY(mat, 70 * Easings:easeInOutBack(shieldO) * l * shieldAnimation, 0.3 * l, -0.4, 0)
end
M:rotateX(mat, 13 * M:clamp(M:sin(shieldO * 4.14), 0, 1) * shieldAnimation, 0.3 * l, -0.4, 0)
M:rotateX(mat, -15 * Easings:easeInOutBack(shieldO) * shieldAnimation, 0.3 * l, -0.4, 0)
end

local tridentDraw = Easings:easeInOutBack(tridentM)
local tridentD = Easings:easeInOutBack(trident)
local tridentDrawS = Easings:easeOutBack(tridentM)
if useAction == "trident" and context.mainHand then
M:translate(mat, 0, -0.15 * tridentDrawS * tridentAnimation, -0.3 * tridentDrawS * tridentAnimation)
M:rotateX(mat, 65 * tridentDrawS, 0.3 * l * tridentAnimation, -0.4, 0)
M:rotateX(mat, 55 * trident * tridentAnimation, 0.3 * l, -0.4, 0)
M:rotateZ(mat, 10 * l * tridentDrawS * tridentAnimation, 0.3 * l, -0.4, 0)
M:rotateX(mat, 0.3 * M:sin(tridentJ * tridentDrawS * 9.14) * tridentAnimation, 0.3 * l, -0.4, 0)

--M:rotateZ(mat, 10 * M:sin(tridentM * 3.14), 0.3 * l, -0.4, 0)
end
if not context.mainHand then
if I:isEmpty(context.item) then
M:moveY(mat, 0.6 * tridentDrawS * tridentAnimation)
else
M:moveY(mat, 0.2 * tridentDrawS * tridentAnimation)
end
M:translate(mat, 0.25 * l * tridentDrawS * tridentAnimation, 0, -0.15 * tridentDrawS * tridentAnimation)
--M:moveZ(mat, 0.1525 * M:sin(tridentM * 3.14))

M:rotateY(mat, 25 * l * tridentDrawS * tridentAnimation, 0.3 * l, -0.4, 0)
M:rotateX(mat, -30 * tridentDrawS * tridentAnimation, 0.3 * l, -0.4, 0)
--M:rotateY(mat, 10 * M:sin(tridentM * 3.14), 0.3 * l, -0.4, 0)
end

local tridentDrawO = Easings:easeInOutBack(tridentMO)
local tridentDrawSO = Easings:easeOutBack(tridentMO)
if useAction == "trident" and not context.mainHand then
M:translate(mat, 0, -0.15 * tridentDrawSO * tridentAnimation, -0.3 * tridentDrawSO * tridentAnimation)
M:rotateX(mat, 65 * tridentDrawSO * tridentAnimation, 0.3 * l, -0.4, 0)
M:rotateZ(mat, 10 * l * tridentDrawSO * tridentAnimation, 0.3 * l, -0.4, 0)
M:rotateX(mat, 0.3 * M:sin(tridentJO * tridentDrawSO * 9.14) * tridentAnimation, 0.3 * l, -0.4, 0)
end
if context.mainHand then
if I:isEmpty(context.item) then
M:moveY(mat, 0.6 * tridentDrawSO * tridentAnimation)
else
M:moveY(mat, 0.2 * tridentDrawSO * tridentAnimation)
end
M:translate(mat, 0.25 * l * tridentDrawSO * tridentAnimation, 0, -0.15 * tridentDrawSO * tridentAnimation)
--M:moveZ(mat, 0.1525 * M:sin(tridentM * 3.14))

M:rotateY(mat, 25 * l * tridentDrawSO * tridentAnimation, 0.3 * l, -0.4, 0)
M:rotateX(mat, -30 * tridentDrawSO * tridentAnimation, 0.3 * l, -0.4, 0)
--M:rotateY(mat, 10 * M:sin(tridentM * 3.14), 0.3 * l, -0.4, 0)
end
-- M:moveX(mat, 0.1 * yawTiltingAngle);
-- M:moveY(mat, 0.1 * yawTiltingAngle);
-- M:rotateZ(mat,  yawTiltingAngle);
-- if(not I:isIn(renderedItem, Tags:getVanillaTag("swords")))


local swingOverall = M:sin(context.swingProgress * 3.14)
local swingRise = M:clamp(M:sin(context.swingProgress * 6.28), 0, 1)
local swingRiseS = M:sin(context.swingProgress * 6.28)
if I:isEmpty(context.item) then
M:translate(mat, -0.15 * l * swing * regularSwing, 0.1 * swingRiseS + 0.33 * swing + 0.05 * swing_rot + 0.14 * swingRise * regularSwing, -0.1 * swingRiseS - 0.4 * swing_hit - 0.2 * swing * regularSwing)
M:rotateX(mat, -10 * swingRise * regularSwing)
M:moveZ(mat, 0.15 * swing_rot * regularSwing)
M:rotateX(mat, -20 * swing * regularSwing, 0.3 * l, -0.4, 0)
M:rotateX(mat, -7 * swing_hit, 0.3 * l * regularSwing, -0.4, 0)
M:rotateX(mat, 10 * swing_rot, 0.3 * l * regularSwing, -0.4, 0)
M:rotateZ(mat, 20 * l * swing, 0.3 * l * regularSwing, -0.4, 0)
M:rotateY(mat, 5 * l * swing, 0.3 * l * regularSwing, -0.4, 0)
M:rotateX(mat, -5 * swingRiseS, 0.3 * l * regularSwing, -0.4, 0)
M:rotateZ(mat, 10 * l * swingRiseS, 0.3 * l * regularSwing, -0.4, 0)
M:rotateY(mat, 5 * l * swingRiseS, 0.3 * l * regularSwing, -0.4, 0)
M:rotateY(mat, 15 * l * swing_hit, 0.3 * l * regularSwing, -0.4, 0)
-- M:scale(mat, 1 - 0.1 * swingRise, 1 - 0.1 * swingRise, 1 - 0.1 * swingRise)
elseif I:isIn(context.item, Tags:getVanillaTag("pickaxes")) then
M:moveZ(mat, -0.1 * swing_sword_tilt * pickaxeSwing)
M:moveZ(mat, -0 * swing_hit * pickaxeSwing)
M:moveZ(mat, -0 * swing_hit_second * pickaxeSwing)
M:moveY(mat, 0 * swing_hit * pickaxeSwing)
M:moveY(mat, -0.1 * swingRiseS * pickaxeSwing)
M:moveX(mat, -0.15 * l * swing_hit * pickaxeSwing)
M:moveX(mat, 0.15 * l * swing_hit_second * pickaxeSwing)
M:moveZ(mat, -0.3 * swingOverall * pickaxeSwing)
M:moveY(mat, 0.2 * swingOverall * pickaxeSwing)
M:moveX(mat, -0.15 * l * swingOverall * pickaxeSwing)
M:rotateX(mat, 20 * swing_sword_tilt * pickaxeSwing, 0.3 * l, -0.4, 0)
--M:rotateZ(mat, -20 * l * swing_sword_tilt, 0.3 * l, -0.4, 0)
M:rotateX(mat, 40 * swing_sword_tilt * pickaxeSwing, 0.3 * l, -0.4, 0)
M:rotateX(mat, 10 * swingRise * pickaxeSwing, 0.3 * l, -0.4, 0)
M:rotateX(mat, 10 * swing_rot * pickaxeSwing, 0.3 * l, -0.4, 0)
M:rotateX(mat, -45 * swing_hit * pickaxeSwing, 0.3 * l, -0.4, 0)
M:rotateX(mat, -10 * swing_hit_second * pickaxeSwing, 0.3 * l, -0.4, 0)
M:rotateX(mat, -95 * swingOverall * pickaxeSwing, 0.3 * l, -0.4, 0)
M:rotateY(mat, 20 * swingOverall * l * pickaxeSwing, 0.3 * l, -0.4, 0)
M:rotateZ(mat, M:clamp(20 * l * M:sin(tilting) * swing, -60, 60) * pickaxeSwing, 0.5 * l, -0.5, 0)
elseif (I:isIn(context.item, Tags:getVanillaTag("swords")) or I:isOf(context.item, Items:get("minecraft:mace")) or useAction == "trident" or useAction == "spear" or I:isIn(context.item, Tags:getVanillaTag("axes"))) then
if swordAttack and swordAttack2 and not context.blockBreaking and (I:isIn(context.item, Tags:getVanillaTag("swords"))) then
M:moveZ(mat, 0.2 * swing_sword_tilt * swordSwing)
M:moveX(mat, -0.5 * l * swing_sword_tilt * swordSwing)
M:moveY(mat, -0.5 * swing_sword_tilt * swordSwing)
M:moveZ(mat, -0.2 * swing_hit * swordSwing)
M:moveZ(mat, -0 * swing_hit_second * swordSwing)
M:moveY(mat, 0 * swing_hit * swordSwing)
M:moveY(mat, -0.1 * swingRiseS * swordSwing)
M:moveX(mat, -0.15 * l * swing_hit * swordSwing)
M:moveX(mat, 0.15 * l * swing_hit_second * swordSwing)
M:moveZ(mat, -0.3 * swingOverall * swordSwing)
M:moveY(mat, 0.2 * swingOverall * swordSwing)
M:moveX(mat, 0.15 * l * swingOverall * swordSwing)
M:rotateX(mat, 20 * swing_sword_tilt * swordSwing, 0.3 * l, -0.4, 0)
M:rotateZ(mat, 70 * l * swing_sword_tilt * swordSwing, 0.3 * l, -0.4, 0)
M:rotateX(mat, 30 * swing_sword_tilt * swordSwing, 0.3 * l, -0.4, 0)
M:rotateX(mat, 10 * swingRise * swordSwing, 0.3 * l, -0.4, 0)
M:rotateX(mat, 10 * swing_rot * swordSwing, 0.3 * l, -0.4, 0)
M:rotateX(mat, -25 * swing_hit * swordSwing, 0.3 * l, -0.4, 0)
M:rotateX(mat, -10 * swing_hit_second * swordSwing, 0.3 * l, -0.4, 0)
M:rotateX(mat, -75 * swingOverall * swordSwing, 0.3 * l, -0.4, 0)
elseif swordAttack and not context.blockBreaking and (I:isIn(context.item, Tags:getVanillaTag("swords")))  or (useAction == "trident" or useAction == "spear") then
if I:isIn(context.item, Tags:getVanillaTag("swords")) then
M:moveZ(mat, -0.2 * swing_sword_tilt * swordSwing)
M:moveZ(mat, -0.15 * swing_hit * swordSwing)
M:moveZ(mat, -0.25 * swing_hit_second * swordSwing)
M:moveY(mat, 0.2 * swing_hit * swordSwing)
M:moveY(mat, -0.1 * swingRiseS * swordSwing)
M:moveX(mat, -0.15 * l * swing_hit * swordSwing)
M:moveX(mat, 0.15 * l * swing_hit_second * swordSwing)
M:moveZ(mat, -0.45 * swingOverall * swordSwing)
M:moveY(mat, 0.2 * swingOverall * swordSwing)
M:moveX(mat, -0.15 * l * swingOverall * swordSwing)
M:rotateX(mat, 20 * swing_sword_tilt * swordSwing, 0.3 * l, -0.4, 0)
M:rotateZ(mat, -70 * l * swing_sword_tilt * swordSwing, 0.3 * l, -0.4, 0)
M:rotateX(mat, 30 * swing_sword_tilt * swordSwing, 0.3 * l, -0.4, 0)
M:rotateX(mat, 10 * swingRise * swordSwing, 0.3 * l, -0.4, 0)
M:rotateX(mat, 10 * swing_rot * swordSwing, 0.3 * l, -0.4, 0)
M:rotateX(mat, -55 * swing_hit * swordSwing, 0.3 * l, -0.4, 0)
M:rotateX(mat, -15 * swing_hit_second * swordSwing, 0.3 * l, -0.4, 0)
M:rotateX(mat, -95 * swingOverall * swordSwing, 0.3 * l, -0.4, 0)
else
    if useAction == "trident" then
        M:moveY(mat, -0.5 * swing_sword_tilt * tridentSwing)
        M:moveZ(mat, 0.1 * swing_sword_tilt * tridentSwing)
    else
        M:moveY(mat, 0 * swing_sword_tilt * tridentSwing)
        M:moveZ(mat, 0.37 * swing_sword_tilt * tridentSwing)
    end
M:moveX(mat, -0.15 * l * swing_sword_tilt * tridentSwing)

 if useAction == "trident" then
M:moveZ(mat, -0.25 * swing_hit_second * tridentSwing)
 else
M:moveZ(mat, -0.35 * swing_hit_second * tridentSwing)
M:moveZ(mat, -0.25 * Easings:easeOutQuart(swing) * tridentSwing)

 end
--M:moveZ(mat, -0.05 * swing_rot)
M:moveZ(mat, 0.14 * swingOverall * tridentSwing)
M:moveY(mat, -0.2 * swingOverall * tridentSwing)
M:moveX(mat, -0.1 * l * swing_hit * tridentSwing)
M:moveX(mat, -0.1 * l * swingOverall * tridentSwing)
M:moveX(mat, -0.1 * l * swing_hit_second * tridentSwing)

if useAction == "trident" then
M:rotateZ(mat, 80 * swing_sword_tilt * l * tridentSwing)
else
M:rotateZ(mat, 20 * swing_sword_tilt * l * tridentSwing)
M:rotateY(mat, 6 * swing_sword_tilt * l * tridentSwing)
end

M:rotateZ(mat, 5 * swingOverall * l * tridentSwing)
M:rotateX(mat, -40 * swing_sword_tilt * tridentSwing)
M:rotateX(mat, 15 * swing_hit_second * tridentSwing)
M:rotateX(mat, 15 * swing_hit * tridentSwing)
M:rotateX(mat, 20 * swingOverall * tridentSwing)
M:rotateX(mat, 10 * swingOverall * tridentSwing)
M:rotateX(mat, -10 * swingRise * tridentSwing)
M:rotateZ(mat, M:clamp(10 * l * M:sin(tilting * 2) * swing, 0, 30) * tridentSwing)
--M:rotateX(mat, 20 * swingOverall * l)

M:moveY(mat, -0.1 * M:sin(tilting * 2) * swing * tridentSwing)
end
elseif (not swordAttack or context.blockBreaking) and (I:isIn(context.item, Tags:getVanillaTag("swords"))) or I:isOf(context.item, Items:get("minecraft:mace")) or I:isIn(context.item, Tags:getVanillaTag("axes")) or (context.blockBreaking and useAction == "trident") then
M:moveZ(mat, -0.2 * swing_sword_tilt)
M:moveZ(mat, -0 * swing_hit)
M:moveZ(mat, -0 * swing_hit_second)
M:moveY(mat, 0 * swing_hit)
M:moveY(mat, -0.1 * swingRiseS)
M:moveX(mat, -0.15 * l * swing_hit)
M:moveX(mat, 0.15 * l * swing_hit_second)
M:moveZ(mat, -0.3 * swingOverall)
M:moveY(mat, 0.2 * swingOverall)
M:moveX(mat, -0.15 * l * swingOverall)
M:rotateX(mat, 20 * swing_sword_tilt, 0.3 * l, -0.4, 0)
if I:isIn(context.item, Tags:getVanillaTag("axes")) and context.blockBreaking then
M:rotateZ(mat, -60 * l * swing_sword_tilt, 0.3 * l, -0.4, 0)
else
M:rotateZ(mat, -40 * l * swing_sword_tilt, 0.3 * l, -0.4, 0)
end
M:rotateX(mat, 30 * swing_sword_tilt, 0.3 * l, -0.4, 0)
M:rotateX(mat, 10 * swingRise, 0.3 * l, -0.4, 0)
M:rotateX(mat, 10 * swing_rot, 0.3 * l, -0.4, 0)
M:rotateX(mat, -45 * swing_hit, 0.3 * l, -0.4, 0)
M:rotateX(mat, -10 * swing_hit_second, 0.3 * l, -0.4, 0)
M:rotateX(mat, -95 * swingOverall, 0.3 * l, -0.4, 0)
M:rotateZ(mat, M:clamp(30 * l * M:sin(tilting * 2) * swing, 0, 30))
M:moveY(mat, -0.2 * M:sin(tilting * 2) * swing)
else
M:moveZ(mat, -0.2 * swing)
M:moveX(mat, -0.15 * l * swing)
M:moveZ(mat, -0.1 * swingRise)
M:moveZ(mat, -0.15 * swing_hit)
M:moveY(mat, 0.1 * swing_hit)
M:moveY(mat, -0.3 * swing)
M:rotateX(mat, 20 * swing_rot, 0.3 * l, -0.4, 0)
M:rotateX(mat, -40 * swing_hit, 0.3 * l, -0.4, 0)
M:rotateX(mat, -20 * swing_hit, 0.3 * l, -0.4, 0)
M:rotateX(mat, 5 * swingRise, 0.3 * l, -0.4, 0)
M:rotateZ(mat, -5 * l * swingOverall)
M:rotateY(mat, 15 * l * swingOverall)
if I:isIn(context.item, Tags:getVanillaTag("swords")) then
-- M:moveZ(mat, -0.15 * swing_hit);
M:moveZ(mat, -0.15 * swing_hit_second)
M:moveZ(mat, -0.15 * swingOverall)
-- M:moveZ(mat, -0.15 * swing_sword_tilt);
M:rotateY(mat, -10 * l * swingOverall)
M:rotateX(mat, -25 * swing_hit_second)
M:rotateX(mat, -20 * swingOverall)
M:rotateX(mat, 20 * swing_sword_tilt)
end
if not I:isIn(context.item, Tags:getVanillaTag("swords")) then
M:rotateX(mat, -5 * swingRiseS, 0.3 * l, -0.4, 0)
end
M:rotateZ(mat, 10 * l * swingRiseS, 0.3 * l, -0.4, 0)
M:rotateY(mat, 5 * l * swingRiseS, 0.3 * l, -0.4, 0)
M:rotateZ(mat, M:clamp(30 * l * M:sin(tilting * 2) * swing, 0, 30))
M:moveY(mat, -0.2 * M:sin(tilting * 2) * swing)
end
-- M:rotateZ(mat, M:clamp(30 * l * M:sin(tilting * 2) * swing, 0, 30));
-- M:moveY(mat, -0.2 * l * M:sin(tilting * 2) * swing);
elseif I:isIn(context.item, Tags:getVanillaTag("shovels")) then
M:moveY(mat, 0.6 * swing_sword_tilt)
M:moveZ(mat, -0.5 * swing_sword_tilt)
M:moveZ(mat, -0.3 * swingOverall)
M:moveY(mat, -0.3 * swingOverall)
M:moveY(mat, -0.4 * swing_rot)
M:moveZ(mat, 0.1 * swing_rot)
M:moveY(mat, -0.1 * swing_hit_second)
M:rotateX(mat, -130 * swing_sword_tilt)
M:rotateX(mat, 70 * swingOverall)
M:rotateX(mat, 40 * swing_rot)
M:rotateX(mat, 20 * swing_hit_second)
M:rotateX(mat, 20 * swingRise)
M:rotateX(mat, -10 * swingRiseS)
else
M:moveZ(mat, -0.1 * swing)
M:moveX(mat, -0.1 * l * swing)
M:moveZ(mat, -0.1 * swingRise)
M:moveZ(mat, -0.05 * swing_hit)
M:moveY(mat, 0.25 * swing_hit)
M:moveY(mat, -0 * swing)
M:rotateX(mat, 5 * swing_rot, 0.3 * l, -0.4, 0)
M:rotateX(mat, -25 * swing_hit, 0.3 * l, -0.4, 0)
M:rotateX(mat, 5 * swingRise, 0.3 * l, -0.4, 0)
M:rotateZ(mat, -5 * l * swingOverall)
M:rotateY(mat, 15 * l * swingOverall)
M:rotateX(mat, -2 * swingRiseS, 0.3 * l, -0.4, 0)
M:rotateZ(mat, 5 * l * swingRiseS, 0.3 * l, -0.4, 0)
M:rotateY(mat, 5 * l * swingRiseS, 0.3 * l, -0.4, 0)
-- M:rotateZ(mat, M:clamp(30 * l * M:sin(tilting * 2) * swing, 0, 30));
-- M:moveY(mat, -0.2 * M:sin(tilting * 2) * swing);
end

if isUsingItem and activeHand == context.hand and useAction == "block" then
if context.mainHand then
M:moveX(mat, 0 - 0.25 * (M:sin(context.equipProgress * context.equipProgress * context.equipProgress) + 4 * M:sin(shieldDisable * shieldDisable * shieldDisable * 3.14)) * l * shieldM * shieldAnimation)
M:rotateZ(mat, 10 * l * (M:sin(context.equipProgress * context.equipProgress * context.equipProgress) + 4 * M:sin(shieldDisable * shieldDisable * shieldDisable * 3.14)) * shieldM * shieldAnimation, 0.3 * l, -0.4, 0)
else
M:moveX(mat, 0 - 0.25 * (M:sin(context.equipProgress * context.equipProgress * context.equipProgress) + 4 * M:sin(shieldDisable * shieldDisable * shieldDisable * 3.14)) * l * shieldO * shieldAnimation)
M:rotateZ(mat, 10 * l * (M:sin(context.equipProgress * context.equipProgress * context.equipProgress) + 4 * M:sin(shieldDisable * shieldDisable * shieldDisable * 3.14)) * shieldO * shieldAnimation, 0.3 * l, -0.4, 0)
end
end
if useAction == "crossbow" and crossBowM + crossBowO == 0 then
M:moveZ(mat, 0 + 0.25 * M:sin(context.equipProgress * context.equipProgress * context.equipProgress) * crossBowAnimation)
M:rotateX(mat, 20 * M:sin(context.equipProgress * context.equipProgress * context.equipProgress) * crossBowAnimation, 0.3 * l, -0.4, 0)
elseif foodCount == 0 and useAction ~= "bow" and useAction ~= "block" and (tridentM == 0 and tridentMO == 0) then
M:moveZ(mat, 0 - 0.25 * M:sin(context.equipProgress * context.equipProgress * context.equipProgress))
M:rotateX(mat, -20 * M:sin(context.equipProgress * context.equipProgress * context.equipProgress), 0.3 * l, -0.4, 0)
if I:isOf(context.item, Items:get("minecraft:mace")) then
M:moveZ(mat, 0 - 0.25 * M:sin(context.equipProgress * context.equipProgress * context.equipProgress))
M:rotateZ(mat, 6 * l  * M:sin(context.equipProgress * context.equipProgress * context.equipProgress))
M:rotateX(mat, -10 * M:sin(context.equipProgress * context.equipProgress * context.equipProgress), 0.3 * l, -0.4, 0)
end
if not I:isBlock(context.item) then
M:rotateX(mat, (pitchAngle * 0.35 * swing))
end
end
local al = 0
if P:getPitch(player) ~= 0 then
al = 90 / P:getPitch(player) / 2.5
else
al = 1
end
if al > 1 then
al = 1
end
if al < 0 then
al = 1
end

-- if(P:isClimbing(player)) then -- Crawling event detection
local multiplier = (I:isLantern(context.item) and 0.2) or 1
M:moveZ(mat, 0.2 * smoothing * climbAnimation)
M:moveY(mat, -0.2 * M:cos(crawler) * l * al * smoothing * multiplier * climbAnimation)
M:rotateX(mat, -30 * l * M:sin(crawler) * al * smoothing * multiplier * climbAnimation)
M:rotateX(mat, P:getPitch(player) * smoothing * climbAnimation)
M:moveZ(mat, 0.01 * P:getPitch(player) * smoothing * climbAnimation)
M:moveY(mat, 0.003 * P:getPitch(player) * smoothing * climbAnimation)
M:moveX(mat, -0.0025 * l * P:getPitch(player) * smoothing * climbAnimation)
if not I:isEmpty(context.item) then
M:moveX(mat, -0.05 * l * smoothing * climbAnimation)
M:moveZ(mat, -0.2 * smoothing * climbAnimation)
M:moveY(mat, -0.1 * smoothing * climbAnimation)
end
M:moveZ(mat, 0.2 * smoothingCrawl * crawlAnimation)
M:moveZ(mat, -0.2 * l * M:sin(crwl) * smoothingCrawl * al * multiplier * crawlDefaulPos * crawlAnimation)
M:rotateY(mat, 10 * M:sin(crwl) * smoothingCrawl * multiplier * crawlDefaulPos * crawlAnimation)
M:rotateX(mat, M:clamp(20 * l * M:cos(crwl) * smoothingCrawl * multiplier * crawlDefaulPos, 0, 20) * crawlAnimation)
if I:isEmpty(context.item) then
M:moveY(mat, 0.3 * smoothingCrawl * crawlAnimation)
M:moveZ(mat, -0.55 * smoothingCrawl * crawlAnimation)
M:rotateX(mat, -45 * smoothingCrawl * crawlAnimation)
M:rotateZ(mat, M:clamp(16 * M:sin(crwl) * smoothingCrawl * multiplier * crawlDefaulPos, 0, 20) * crawlAnimation, 0.3, -0.4, 0)
end
M:rotateX(mat, P:getPitch(player) * smoothingCrawl * crawlAnimation)
M:rotateX(mat, -7 * smoothingCrawl * crawlAnimation)
M:moveZ(mat, 0.01 * P:getPitch(player) * smoothingCrawl * crawlAnimation)
if I:isEmpty(context.item) then
M:moveZ(mat, 0.005 * P:getPitch(player) * smoothingCrawl * crawlAnimation)
end
M:moveY(mat, 0.003 * P:getPitch(player) * smoothingCrawl * crawlAnimation)
M:moveX(mat, -0.0025 * l * P:getPitch(player) * smoothingCrawl * crawlAnimation)
if not I:isEmpty(context.item) then
M:moveX(mat, -0.1 * l * smoothingCrawl * crawlAnimation)
M:moveZ(mat, -0.2 * smoothingCrawl * crawlAnimation)
M:moveY(mat, -0.1 * smoothingCrawl * crawlAnimation)
end

local easedBow = Easings:easeInOutBack(bowCount) * bowAnimation
local easedBowO = Easings:easeInOutBack(bowCountO) * bowAnimation
local bowShoot = M:sin(easedBow * 3.14) * M:pow((1-(easedBow / 2)), 6) * 0.2 * bowAnimation

if bowCount > 0 then
offhand = easedBow == 1  and -0.1 or 1 - easedBow
end
if useAction == "bow" and context.mainHand then
M:moveX(mat, 0.15 * l)
M:moveZ(mat, -0.085)
M:moveX(mat, 0.1 * l * easedBow)
M:moveZ(mat, 0.085 * easedBow)
M:moveZ(mat, -0.1 * easedBow)
M:moveX(mat, -0.2 * l * easedBow)
M:moveY(mat, 0.15  * easedBow)
M:rotateX(mat, 5 * M:sin(easedBow * 3.14), 0.3 * l, -0.4, 0)
M:rotateY(mat, 15 * l, 0.3 * l, -0.4, 0)
M:rotateY(mat, 0 * l * easedBow, 0.3 * l, -0.4, 0)

M:moveZ(mat, 0.0015 * M:sin(bowWiggle * 15.14) * bowCountSec)
end
if useAction == "bow" and not context.mainHand then
M:moveX(mat, 0.15 * l)
M:moveZ(mat, -0.085)
M:moveX(mat, 0.1 * l * easedBowO)
M:moveZ(mat, 0.085 * easedBowO)
M:moveZ(mat, -0.1 * easedBowO)
M:moveX(mat, -0.2 * l * easedBowO)
M:moveY(mat, 0.15 * easedBowO)
M:rotateX(mat, 5 * M:sin(easedBowO * 3.14), 0.3 * l, -0.4, 0)
M:rotateY(mat, 15 * l, 0.3 * l, -0.4, 0)
M:rotateY(mat, 0 * l * easedBowO, 0.3 * l, -0.4, 0)

M:moveZ(mat, 0.0015 * M:sin(bowWiggleO * 15.14) * bowCountSecO)
end

if not context.mainHand and isUsingItem or (not context.mainHand and I:isEmpty(context.item)) then
local easedBowSec = Easings:easeOutBack(bowCountSec) * bowAnimation
M:moveX(mat, (-xOffset - (xOffset / 1.5)) * l * easedBow)
M:moveX(mat, 0.27 * l * easedBow)
if not I:isEmpty(context.item) then
M:moveY(mat, M:sin(easedBow * 1.56 + 3.14))
end
M:moveZ(mat, -0.65 * easedBow)
M:rotateX(mat, 10 * M:sin(easedBow * 3.14) * l, 0.3 * l, -0.4, 0)

M:rotateY(mat, 70 * easedBow * l, 0.3 * l, -0.4, 0)
-- if(not I:isEmpty(renderedItem)) then
-- 	M:rotateY(mat, -15 * easedBowSec * l, 0.3 * l, -0.4, 0)
-- end
M:rotateY(mat, 25 * easedBowSec * l, 0.3 * l, -0.4, 0)
M:rotateY(mat, 0.25 * l * M:sin(bowWiggle * 15.14) * easedBowSec)

M:rotateY(mat, 25 * easedBow * l, 0.3 * l, -0.4, 0)
M:moveY(mat, -0.5 * M:sin(easedBow * 3.14))
end
if context.mainHand and isUsingItem or (context.mainHand and I:isEmpty(context.item)) then
local easedBowSecO = Easings:easeOutBack(bowCountSecO)
M:moveX(mat, (-xOffset - (xOffset / 1.5)) * l * easedBowO)
M:moveX(mat, 0.27 * l * easedBowO)
-- if(not I:isEmpty(renderedItem)) then
-- 	M:moveX(mat, 0.4 * easedBowO)
-- 	M:moveY(mat, -0.65 * easedBowO)
-- 	M:rotateX(mat, 40 * easedBowO, 0.3, -0.4, 0)
-- end
if not I:isEmpty(context.item) then
M:moveY(mat, M:sin(easedBowO * 1.56 + 3.14))
end
M:moveZ(mat, -0.65 * easedBowO)
M:rotateX(mat, 10 * M:sin(easedBowO * 3.14) * l, 0.3 * l, -0.4, 0)
M:rotateY(mat, 70 * easedBowO * l, 0.3 * l, -0.4, 0)
M:rotateY(mat, 25 * easedBowSecO * l, 0.3 * l, -0.4, 0)
M:rotateY(mat, 0.25 * l * M:sin(bowWiggleO * 15.14) * easedBowSecO)

M:rotateY(mat, 25 * easedBowO * l, 0.3 * l, -0.4, 0)
M:moveY(mat, -0.5 * M:sin(easedBowO * 3.14))
end

local easedCrossBowM = Easings:easeOutBack(crossBowM) * crossBowAnimation
local easedCrossBowSecM = Easings:easeOutBack(crossBowSecM) * crossBowAnimation
local easedCrossBowO = Easings:easeOutBack(crossBowO) * crossBowAnimation
local easedCrossBowSecO = Easings:easeOutBack(crossBowSecO) * crossBowAnimation

if useAction == "crossbow" and context.mainHand then
M:moveY(mat, -0.15 * easedCrossBowM)
M:moveZ(mat, 0.3 * easedCrossBowM)
M:rotateZ(mat, 20 * l * easedCrossBowM, -0.3 * l, -0.4, 0)
M:rotateY(mat, 15 * l * easedCrossBowM, -0.3 * l, -0.4, 0)
end
if not context.mainHand and isUsingItem or (not context.mainHand and I:isEmpty(context.item)) then
M:moveX(mat, (-xOffset - (xOffset / 1.5)) * l * easedCrossBowM)
M:moveX(mat, 0.25 * l * easedCrossBowM)
M:moveZ(mat, -0.1 * easedCrossBowM)
M:moveY(mat, 0.55 * easedCrossBowM)
if not I:isEmpty(context.item) then
M:moveY(mat, M:sin(easedCrossBowM * 1.56 + 3.14))
end
M:rotateZ(mat, 15 * l * easedCrossBowM, 0.3 * l, -0.4, 0)
M:rotateY(mat, 80 * l * easedCrossBowM, 0.3 * l, -0.4, 0)
M:rotateY(mat, 15 * l * easedCrossBowSecM, 0.3 * l, -0.4, 0)
M:rotateX(mat, -7 * easedCrossBowSecM, 0.3 * l, -0.4, 0)
end

if useAction == "crossbow" and not context.mainHand then
M:moveY(mat, -0.15 * easedCrossBowO)
M:moveZ(mat, 0.3 * easedCrossBowO)
M:rotateZ(mat, 20 * l * easedCrossBowO, -0.3 * l, -0.4, 0)
M:rotateY(mat, 15 * l * easedCrossBowO, -0.3 * l, -0.4, 0)
end
if context.mainHand and isUsingItem or (context.mainHand and I:isEmpty(context.item)) then
M:moveX(mat, (-xOffset - (xOffset / 1.5)) * l * easedCrossBowO)
M:moveX(mat, 0.25 * l * easedCrossBowO)
M:moveZ(mat, -0.1 * easedCrossBowO)
M:moveY(mat, 0.55 * easedCrossBowO)
if not I:isEmpty(context.item) then
M:moveY(mat, M:sin(easedCrossBowO * 1.56 + 3.14))
end
M:rotateZ(mat, 15 * l * easedCrossBowO, 0.3 * l, -0.4, 0)
M:rotateY(mat, 80 * l * easedCrossBowO, 0.3 * l, -0.4, 0)
M:rotateY(mat, 15 * l * easedCrossBowSecO, 0.3 * l, -0.4, 0)
M:rotateX(mat, -7 * easedCrossBowO, 0.3 * l, -0.4, 0)
end

if context.mainHand then
foodCount = foodCount * foodAnimation
foodCountO = foodCountO * foodAnimation
foodCountSecO = foodCountSecO * foodAnimation
foodCountSec = foodCountSec * foodAnimation
local easedFoodCount = foodCount * foodCount
if (useAction == "eat" or useAction == "toot_horn") and context.mainHand then
M:moveZ(mat, 0.155 * easedFoodCount)
M:moveX(mat, 0.135 * l * easedFoodCount)
M:moveY(mat, -0.27 * easedFoodCount)
M:moveY(mat, -0 * drinkCount)
-- M:moveZ(mat, 0.15 * drinkCount)
M:rotateX(mat, 30 * easedFoodCount)
M:rotateX(mat, 20 * drinkCount)
if useAction == "eat" then
M:rotateX(mat, 3 * Easings:easeInOutBack(M:abs(M:sin(foodCountSec * 3))) * easedFoodCount)
M:rotateY(mat, 4 * l * Easings:easeInOutBack(M:abs(M:sin(foodCountSec * 2))) * easedFoodCount)
M:rotateZ(mat, 6 * l * Easings:easeInOutBack(M:abs(M:sin(foodCountSec * 2))) * easedFoodCount)
else
M:rotateX(mat, 2 * Easings:easeInOutSine(M:sin(foodCountSec * 2)) * easedFoodCount)
M:rotateY(mat, 3 * l * Easings:easeInOutSine(M:sin(foodCountSec)) * easedFoodCount)
M:rotateZ(mat, 5 * l * Easings:easeInOutSine(M:sin(foodCountSec)) * easedFoodCount)
end
M:rotateY(mat, 60 * easedFoodCount * l, 0.3 * l, -0.4, 0)
M:rotateX(mat, 25 * M:sin(easedFoodCount * 3.14), 0.3 * l, -0.4, 0)
end
if (useAction == "drink" or I:isEmpty(context.item) or I:isOf(context.item, Items:get("minecraft:glass_bottle"))) and context.mainHand then
M:moveZ(mat, 0.1 * easedFoodCount)
M:moveX(mat, 0.11 * l * easedFoodCount)
M:moveY(mat, -0.5 * easedFoodCount)
M:moveY(mat, -0 * drinkCount)
-- M:moveZ(mat, 0.15 * drinkCount)
M:rotateX(mat, 50 * easedFoodCount)
M:rotateX(mat, 20 * drinkCount)
M:rotateX(mat, 2 * M:sin(foodCountSec * 6) * drinkCount)
M:rotateY(mat, l * 60 * easedFoodCount, 0.3 * l, -0.4, 0)
M:rotateX(mat, 25 * M:sin(easedFoodCount * 3.14), 0.3 * l, -0.4, 0)
end
else
local easedFoodCount = foodCountO * foodCountO
if (useAction == "eat" or useAction == "toot_horn") and not context.mainHand then
M:moveZ(mat, 0.155 * easedFoodCount)
M:moveX(mat, 0.135 * l * easedFoodCount)
M:moveY(mat, -0.27 * easedFoodCount)
M:moveY(mat, -0 * drinkCountO)
-- M:moveZ(mat, 0.15 * drinkCount)
M:rotateX(mat, 30 * easedFoodCount)
M:rotateX(mat, 20 * drinkCountO)
if useAction == "eat" then
M:rotateX(mat, 3 * Easings:easeInOutBack(M:abs(M:sin(foodCountSecO * 3))) * easedFoodCount)
M:rotateY(mat, 4 * l * Easings:easeInOutBack(M:abs(M:sin(foodCountSecO * 2))) * easedFoodCount)
M:rotateZ(mat, 6 * l * Easings:easeInOutBack(M:abs(M:sin(foodCountSecO * 2))) * easedFoodCount)
else
M:rotateX(mat, 2 * Easings:easeInOutSine(M:sin(foodCountSecO * 2)) * easedFoodCount)
M:rotateY(mat, 3 * l * Easings:easeInOutSine(M:sin(foodCountSecO)) * easedFoodCount)
M:rotateZ(mat, 5 * l * Easings:easeInOutSine(M:sin(foodCountSecO)) * easedFoodCount)
end
M:rotateY(mat, 60 * l * easedFoodCount, 0.3 * l, -0.4, 0)
M:rotateX(mat, 25 * M:sin(easedFoodCount * 3.14), 0.3 * l, -0.4, 0)
end
if (useAction == "drink") and not context.mainHand then
M:moveZ(mat, 0.1 * easedFoodCount)
M:moveX(mat, 0.11 * l * easedFoodCount)
M:moveY(mat, -0.5 * easedFoodCount)
M:moveY(mat, -0 * drinkCountO)
-- M:moveZ(mat, 0.15 * drinkCount)
M:rotateX(mat, 50 * easedFoodCount)
M:rotateX(mat, 20 * drinkCountO)
M:rotateX(mat, 2 * M:sin(foodCountSecO * 6) * drinkCountO)
M:rotateY(mat, 60 * l * easedFoodCount, 0.3 * l, -0.4, 0)
M:rotateX(mat, 25 * M:sin(easedFoodCount * 3.14), 0.3 * l, -0.4, 0)
end
end

local bsc = Easings:easeInOutBack(brushCounter)
local bscO = Easings:easeInOutBack(brushCounterO)
if useAction == "brush" and context.mainHand then
M:moveZ(mat, -0.2 * bsc)
M:moveX(mat, -0.2 * l * bsc)
M:moveY(mat, -0.3 * bsc)
M:moveX(mat, -0.2 * l * M:sin(foodCountSec * 4.14) * bsc)
M:moveY(mat, -0.3 * M:sin(foodCountSec * 4.14) * bsc)
M:rotateX(mat, 10 * M:sin(bsc * 3.14))
M:rotateY(mat, 20 * l * bsc)
M:rotateY(mat, 10 * l * M:sin(foodCountSec * 4.14) * bsc)
M:rotateZ(mat, 30 * l * bsc)
M:rotateZ(mat, 30 * l * M:sin(foodCountSec * 4.14) * bsc)
end
if useAction == "brush" and not context.mainHand then
M:moveZ(mat, -0.2 * bscO)
M:moveX(mat, -0.2 * l * bscO)
M:moveY(mat, -0.3 * bscO)
M:moveX(mat, -0.2 * l * M:sin(foodCountSecO * 4.14) * bscO)
M:moveY(mat, -0.3 * M:sin(foodCountSecO * 4.14) * bscO)
M:rotateX(mat, 10 * M:sin(bscO * 3.14))
M:rotateY(mat, 20 * l * bscO)
M:rotateY(mat, 10 * l * M:sin(foodCountSecO * 4.14) * bscO)
M:rotateZ(mat, 30 * l * bscO)
M:rotateZ(mat, 30 * l * M:sin(foodCountSecO * 4.14) * bscO)
end
if I:isIn(context.item, Tags:getVanillaTag("doors")) then
M:moveX(mat, 0.2 * l)
M:rotateX(mat, 6, 0.3 * l, -0.4, 0)
M:rotateY(mat, 20 * l, 0.3 * l, -0.4, 0)
end

if P:isItemCoolingDown(context.item, player) and useAction == 'block' then
shieldDisable = shieldDisable + 0.04 * context.deltaTime * 30
elseif useAction == "block" then
shieldDisable = shieldDisable - 0.06 * context.deltaTime * 30
end
shieldDisable = M:clamp(shieldDisable, 0, 1)

local easedDisable = shieldDisable * shieldDisable
if useAction == "block" then
M:moveZ(mat, -0.4 * easedDisable)
M:moveY(mat, 0.15 * easedDisable)
M:moveX(mat, -0.1 * l * easedDisable)
M:rotateX(mat, -30 * easedDisable)
M:rotateX(mat, -10 * M:sin(easedDisable * 3.14))
M:rotateY(mat, -20 * l * easedDisable)
M:rotateZ(mat, -6 * l * easedDisable)
end

prevSwingM = context.swingMHand

local sinalFoodSpeed = M:sin(M:clamp(foodCount, 0.80041, 1) * 3.14 * 5) * 0.45
foodSpeed = foodSpeed + sinalFoodSpeed * context.deltaTime * 30
foodSpeed = foodSpeed * M:pow(0.8, context.deltaTime * 30)
local foodCamera = ((0.25 * M:sin(foodCountSec * 3) * foodCount) + (0.25 * M:sin(foodCountSecO * 3) * foodCountO) + (foodSpeed * 1.5))
local drinkCamera = (0.25 * M:sin(foodCountSec * 3) * drinkCount + (drinkCount * drinkCount * 2.75)) + (0.25 * M:sin(foodCountSecO * 3) * drinkCountO + (drinkCountO * drinkCountO * 2.75))
-- C.setCamRot(foodCamera + drinkCamera, 0, 0);
-- C.setCamRot((-0.8 * walkSmoother) + fall + ptAngle * 0.02 + (0.2 * M:sin(foodCountSecO * 3) * drinkCountO + (drinkCountO * 4)) + (0.2 * M:sin(foodCountSec * 3) * drinkCount + (drinkCount * 4)) + foodCamera, 0, (ywAngle * 0.08) + (0.2 * M:cos(foodCountSecO * 4) * drinkCountO + (drinkCountO * 4)) + (0.2 * M:cos(foodCountSec * 4) * drinkCount + foodCamera))
-- C.setCamPos(0.002 * ywAngle * walkSmoother, 0.05 * math.abs(M:pow(M:sin(walk * 0.8), 3)) * walkSmoother, 0)

-- local switchAnimationVariable = Easings:easeInBack(M:sin(M:clamp(mainHandSwitch,0.09723, 0.60632) * 3.24 * 1.65 - 0.1));
-- if(I:isIn(renderedItem, Tags:getVanillaTag("bundles"))) then
-- 	M:rotateX(mat, 10 * switchAnimationVariable);
-- end

local musicDiscHandTilt
if mainHandSwitch < 0.65245 then
musicDiscHandTilt = M:sin(M:clamp(mainHandSwitch, 0, 0.16675) * 3.14 * 3)
else
musicDiscHandTilt = M:sin(M:clamp(mainHandSwitch, 0.65245, 1) * 4.4 - 1.3)
end
local musicDiscHandJump = M:sin(M:clamp(mainHandSwitch, 0.52459, 0.85809) * 3.14 * 3 - 1.8)
-- if(I:isIn(renderedItem, ConventionalItemTags.MUSIC_DISCS)) then
-- 	M:rotateX(mat, 45 * musicDiscHandTilt);
-- end

if I:isEmpty(context.item) and drinkCount > 0 then
M:rotateZ(mat, -6 * l)
M:moveY(mat, -0.35)
-- M:moveZ(mat, -0.2);
end

local easedMapTransition = Easings:easeInOutBack(mapTransition)
local easedMapSmoother = Easings:easeInOutBack(mapSmoother)
local easedMapZoomer = Easings:easeOutBack(mapZoomer)

if I:isOf(context.item, Items:get("minecraft:filled_map")) then --[[ and context.mainHand and I:isEmpty(P:getOffhandItem(player))]]
M:moveX(mat, (0.3 - (0.1 * easedMapZoomer)) * l * easedMapSmoother)
M:moveY(mat, 0.18 * easedMapSmoother)
M:moveZ(mat, 0.12 * easedMapZoomer * easedMapSmoother)
M:rotateX(mat, M:clamp(P:getPitch(player), 0, 50) * easedMapSmoother)
M:rotateX(mat, -40 * easedMapSmoother)
M:rotateY(mat, (40 + (30 * easedMapZoomer)) * l * easedMapSmoother, 0.3 * l, -0.4, 0)
end

if I:isOf(context.item, Items:get("minecraft:filled_map")) then
local smoother = 1 - easedMapSmoother
M:moveX(mat, 0.1 * l * smoother)
M:moveY(mat, -0.35 * smoother)
M:moveZ(mat, 0.22 * smoother)
M:rotateX(mat, 24 * smoother)
M:rotateY(mat, 10 * l * smoother)
end

if useAction == "crossbow" then
M:moveX(mat, 0.1 * l)
M:moveZ(mat, 0.2)
M:rotateX(mat, -5, 0.3 * l, -0.4, 0)
M:rotateY(mat, 20 * l, 0.3 * l, -0.4, 0)
end

if KeyBindManager:isKeyPressed(${inspectKeybind} ~= 0 and ${inspectKeybind} or 67) then
inspectionCounter = inspectionCounter + 0.04 * context.deltaTime * 30
else
inspectionCounter = inspectionCounter - 0.04 * context.deltaTime * 30
end
inspectionCounter = M:clamp(inspectionCounter, 0, 1)

if (I:isIn(context.item, Tags:getVanillaTag("swords")) or I:isIn(context.item, Tags:getVanillaTag("pickaxes")) or I:isIn(context.item, Tags:getVanillaTag("axes")) or useAction == "trident") and context.mainHand then
M:moveX(mat, 0.35 * l * Easings:easeInOutBack(inspectionCounter))
M:moveZ(mat, -0.15 * Easings:easeInOutBack(inspectionCounter))
M:rotateY(mat, 40 * Easings:easeInOutBack(inspectionCounter) * l, 0.3 * l, -0.4, 0)
M:rotateX(mat, 13 * M:clamp(M:sin(inspectionCounter * 4.14), 0, 1), 0.3 * l, -0.4, 0)
-- M:rotateX(mat, -15 * Easings:easeInOutBack(inspectionCounter), 0.3 * l, -0.4, 0);
M:rotateX(mat, 10 * M:sin(Easings:easeInOutBack(inspectionSpin) * 6.28))
end

if I:isOf(context.item, Items:get("minecraft:mace")) and context.mainHand then
M:moveX(mat, 0.35 * l * Easings:easeInOutBack(inspectionCounter))
M:moveZ(mat, -0.15 * Easings:easeInOutBack(inspectionCounter))
M:rotateY(mat, 40 * Easings:easeInOutBack(inspectionCounter) * l, 0.3 * l, -0.4, 0)
M:rotateX(mat, 17 * M:clamp(M:sin(inspectionCounter * 3.14), 0, 1), 0.3 * l, -0.4, 0)
-- M:rotateX(mat, -15 * Easings:easeInOutBack(inspectionCounter), 0.3 * l, -0.4, 0);
end





if context.mainHand then
isChargedM = I:isChargedCrossbow(context.item)
else
isChargedO = I:isChargedCrossbow(context.item)
end


if P:isUsingRiptide(player) and useAction == "trident" and activeHand == context.hand then
if context.mainHand then
riptideCounter = riptideCounter + 0.08 * context.deltaTime * 30
else
riptideCounterO = riptideCounterO + 0.08 * context.deltaTime * 30
end
else
if context.mainHand then
riptideCounter = riptideCounter - 0.025 * context.deltaTime * 30
else
riptideCounterO = riptideCounterO - 0.025 * context.deltaTime * 30
end
end


riptideCounter = M:clamp(riptideCounter, 0, 1)
riptideCounterO = M:clamp(riptideCounterO, 0, 1)
riptideCounter = riptideCounter * M:pow(0.95, context.deltaTime * 30) * (1 - M:clamp(context.swingProgress * 4, 0, 1))  * (1 - M:clamp(tridentM * 6, 0, 1))
riptideCounterO = riptideCounterO * M:pow(0.95, context.deltaTime * 30) * (1 - M:clamp(context.swingProgress * 4, 0, 1))  * M:clamp(1 - tridentMO * 6, 0, 1)


if useAction == "trident" then
local easedRiptide = context.mainHand and Easings:easeOutBack(riptideCounter) or Easings:easeOutBack(riptideCounterO)
local rp = context.mainHand and M:clamp(riptideCounter * 3, 0, 1) or M:clamp(riptideCounterO * 3, 0, 1)
M:moveZ(mat, 0.2 * easedRiptide * rp)
M:moveX(mat, -1.25 * l * easedRiptide * rp)
M:moveY(mat, -0 * easedRiptide * rp)
M:rotateY(mat, 5 * l * easedRiptide * rp)
M:rotateX(mat, -30 * Easings:easeOutBack(M:sin(context.mainHand and riptideCounter or riptideCounterO * 3.14))  * rp)
M:rotateZ(mat, 70 * Easings:easeOutBack(M:sin(context.mainHand and riptideCounter or riptideCounterO * 3.14)) * rp * l, 0.7 * l, 0.7, 0)
end


local sc = context.mainHand and spearCounterM or spearCounterO
local scd = context.mainHand and canDismountCounter or canDismountCounterO
local sck = context.mainHand and canKnockbackCounter or canKnockbackCounterO
local sw = context.mainHand and mainHandSwitch or offHandSwitch
local hic = context.mainHand and Easings:easeInOutSine(hitImpactCounter) or hitImpactCounterO
if useAction == "spear" then
    M:moveZ(mat, -0.25)
    M:moveY(mat, 0.1)
    M:rotateX(mat, -20)
M:moveZ(mat, 0.75 * M:sin(Easings:easeInOutSine(hic) * 3.14))

M:moveZ(mat, -0.25 * Easings:easeInOutBack(sc))
M:moveZ(mat, 0.25 * Easings:easeOutBack(sck) * sck)

M:rotateY(mat, 8 * Easings:easeInOutBack(sc) * l)

M:rotateX(mat, 40 * M:sin(sc * 3.14), 0, -0.2, -0.35)
M:rotateX(mat, -8 * Easings:easeInOutBack(scd), 0.5 * l, -0.5, -0.35)
M:rotateY(mat, -1.5 * M:sin(a * 1.5) * Easings:easeInOutBack(scd) * l, 0.5 * l, -0.5, -0)
M:rotateX(mat, -1.5 * M:sin(a * 3) * Easings:easeInOutBack(scd), 0.5 * l, -0.5, -0)

M:rotateX(mat, 5 * M:sin(hic * 3.14))
--M:rotateZ(mat, 10 * M:sin(hic * 3.14) * l, 0.5 * l, -0.5, -0.35)


M:rotateZ(mat, -30 * Easings:easeOutBack(sck) * sck * l, 0.5 * l, -0.5, -0.35)
--M:rotateX(mat, -10 * Easings:easeOutBack(canKnockbackCounter) * canKnockbackCounter * l, 0.5 * l, -0.5, -0.35)

    M:rotateZ(mat, -8 * M:sin(M:clamp(sw * 2, 0, 1) * 6.28) * M:sin(M:clamp(sw * 2, 0, 1) * 6.28) * l, 0.5 * l, -0.5, 0)
    M:rotateX(mat, 20 * M:sin(M:clamp(sw * 2, 0, 1) * 3.14) * M:sin(M:clamp(sw * 2, 0, 1) * 3.14), 0.5 * l, -0.5, 0)
end

-- if(useAction == "spear") then
-- debugger:out("hitImpact: " .. tostring(I:getSpearData(context.item).hitImpact))
-- end
package dev.FoxTeam;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;

public class MovementHandler {
    private static final double MAX_HORIZONTAL_SPEED = 2.0;

    private static double manualBonus = 0.0;
    private static int groundTicks = 0;
    private static boolean wasOnGround = false;
    private static float prevYaw = 0.0F;
    private static double prevAirSpeed = 0.0;

    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null) {
            return;
        }

        handleFirstJoin(mc);
        handleKeyInputs();
        handleMovement(mc, player);
    }

    private static void handleFirstJoin(Minecraft mc) {
        if (Config.FIRST_JOIN.get() && mc.screen == null) {
            Config.setFirstJoin(false);
            mc.setScreen(new HopScreen());
        }
    }

    private static void handleKeyInputs() {
        while (KeyBindings.SPEED_UP.consumeClick()) {
            HopMode.increaseSpeed();
            sendStatusMessage();
        }

        while (KeyBindings.SPEED_DOWN.consumeClick()) {
            HopMode.decreaseSpeed();
            sendStatusMessage();
        }

        while (KeyBindings.TOGGLE_AUTO_BHOP.consumeClick()) {
            HopMode.toggleAutoBhop();
            sendStatusMessage();
        }
    }

    private static void handleMovement(Minecraft mc, LocalPlayer player) {
        if (player.isSpectator() || player.getAbilities().flying || player.isFallFlying() || player.isInWater() || player.isInLava() || player.isPassenger()) {
            groundTicks = player.onGround() ? 1 : 0;
            wasOnGround = player.onGround();
            prevYaw = player.getYRot();
            prevAirSpeed = 0.0;
            manualBonus = 0.0;
            return;
        }

        boolean onGround = player.onGround();

        if (HopMode.isAutoBhop()) {
            manualBonus = 0.0;
            if (onGround) {
                groundTicks++;
                if (mc.options.keyJump.isDown()) {
                    boolean hasMoveInput = mc.options.keyUp.isDown() || mc.options.keyDown.isDown() || mc.options.keyLeft.isDown() || mc.options.keyRight.isDown();
                    player.jumpFromGround();
                    Vec3 motion = player.getDeltaMovement();
                    double mult = HopMode.getSpeedMultiplier();
                    if (mult > 0.0 && hasMoveInput) {
                        double targetSpeed = Math.max(Math.hypot(motion.x, motion.z), prevAirSpeed) * mult;
                        applyHorizontalSpeed(player, motion, targetSpeed);
                    }
                }
            } else {
                groundTicks = 0;
                prevAirSpeed = Math.hypot(player.getDeltaMovement().x, player.getDeltaMovement().z);
            }
        } else {
            if (onGround) {
                groundTicks++;
                if (groundTicks > 2) {
                    manualBonus = 0.0;
                }
            } else {
                if (wasOnGround && player.getDeltaMovement().y > 0.05) {
                    if (groundTicks >= 1 && groundTicks <= 2) {
                        manualBonus += 0.1;
                        Vec3 motion = player.getDeltaMovement();
                        double effectiveMult = HopMode.getSpeedMultiplier() + manualBonus;
                        double targetSpeed = Math.max(Math.hypot(motion.x, motion.z), prevAirSpeed) * (effectiveMult / Math.max(1.0, HopMode.getSpeedMultiplier()));
                        applyHorizontalSpeed(player, motion, targetSpeed);
                    } else {
                        manualBonus = 0.0;
                    }
                }
                groundTicks = 0;
                prevAirSpeed = Math.hypot(player.getDeltaMovement().x, player.getDeltaMovement().z);
            }
        }

        if (HopMode.getCurrentMode() == HopMode.STRAFE && !onGround) {
            float currentYaw = player.getYRot();
            float deltaYaw = Mth.wrapDegrees(currentYaw - prevYaw);

            boolean keyLeft = mc.options.keyLeft.isDown();
            boolean keyRight = mc.options.keyRight.isDown();
            boolean keyForward = mc.options.keyUp.isDown();

            boolean strafeLeft = keyLeft && deltaYaw < -0.01F;
            boolean strafeRight = keyRight && deltaYaw > 0.01F;

            if (strafeLeft || strafeRight) {
                Vec3 motion = player.getDeltaMovement();
                double currentSpeed = Math.hypot(motion.x, motion.z);
                if (currentSpeed > 0.02) {
                    float targetYaw = currentYaw + (strafeLeft ? -90.0F : 90.0F);
                    if (keyForward) {
                        targetYaw = currentYaw + (strafeLeft ? -45.0F : 45.0F);
                    }
                    double targetRad = Math.toRadians(targetYaw);
                    double dirX = -Math.sin(targetRad);
                    double dirZ = Math.cos(targetRad);

                    double blend = 0.15;
                    double blendedX = motion.x + dirX * currentSpeed * blend;
                    double blendedZ = motion.z + dirZ * currentSpeed * blend;
                    double blendedSpeed = Math.hypot(blendedX, blendedZ);

                    if (blendedSpeed > 0.0001) {
                        double absDeltaYaw = Math.min(Math.abs(deltaYaw), 15.0F);
                        double accel = 0.015 * (absDeltaYaw / 10.0) * HopMode.getSpeedMultiplier();
                        if (keyForward) {
                            accel *= 0.35;
                        }
                        double newSpeed = currentSpeed + accel;
                        if (newSpeed > MAX_HORIZONTAL_SPEED) {
                            newSpeed = MAX_HORIZONTAL_SPEED;
                        }
                        double finalX = (blendedX / blendedSpeed) * newSpeed;
                        double finalZ = (blendedZ / blendedSpeed) * newSpeed;
                        player.setDeltaMovement(finalX, motion.y, finalZ);
                    }
                }
            }
        }

        prevYaw = player.getYRot();
        wasOnGround = onGround;
    }

    private static void applyHorizontalSpeed(LocalPlayer player, Vec3 motion, double targetSpeed) {
        double currentSpeed = Math.hypot(motion.x, motion.z);
        if (currentSpeed > 0.0001 && targetSpeed > 0.0) {
            if (targetSpeed > MAX_HORIZONTAL_SPEED) {
                targetSpeed = MAX_HORIZONTAL_SPEED;
            }
            double factor = targetSpeed / currentSpeed;
            player.setDeltaMovement(motion.x * factor, motion.y, motion.z * factor);
        }
    }

    public static void sendStatusMessage() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }
        Component message = Component.translatable(
                "foxhop.actionbar.status",
                String.format(java.util.Locale.ROOT, "%.1fx", HopMode.getSpeedMultiplier()),
                Component.translatable(HopMode.isAutoBhop() ? "foxhop.status.on" : "foxhop.status.off"),
                HopMode.getCurrentMode().getDisplayName()
        );
        mc.player.displayClientMessage(message, true);
    }
}

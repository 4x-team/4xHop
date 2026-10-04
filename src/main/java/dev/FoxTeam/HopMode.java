package dev.FoxTeam;

import net.minecraft.network.chat.Component;

public enum HopMode {
    DEFAULT("foxhop.mode.default"),
    STRAFE("foxhop.mode.strafe");

    private final String translationKey;

    HopMode(String translationKey) {
        this.translationKey = translationKey;
    }

    public Component getDisplayName() {
        return Component.translatable(translationKey);
    }

    private static HopMode currentMode = DEFAULT;
    private static double speedMultiplier = 1.0;
    private static boolean autoBhop = false;

    public static HopMode getCurrentMode() {
        return currentMode;
    }

    public static void setCurrentMode(HopMode mode) {
        currentMode = mode;
        Config.setBhopMode(mode.name());
    }

    public static double getSpeedMultiplier() {
        return speedMultiplier;
    }

    public static void setSpeedMultiplier(double multiplier) {
        speedMultiplier = Math.max(0.0, Math.round(multiplier * 10.0) / 10.0);
        Config.setSpeedMultiplier(speedMultiplier);
    }

    public static void increaseSpeed() {
        setSpeedMultiplier(speedMultiplier + 0.1);
    }

    public static void decreaseSpeed() {
        setSpeedMultiplier(speedMultiplier - 0.1);
    }

    public static boolean isAutoBhop() {
        return autoBhop;
    }

    public static void setAutoBhop(boolean enabled) {
        autoBhop = enabled;
        Config.setAutoBhop(enabled);
    }

    public static void toggleAutoBhop() {
        setAutoBhop(!autoBhop);
    }

    public static void cycleMode() {
        if (currentMode == DEFAULT) {
            setCurrentMode(STRAFE);
        } else {
            setCurrentMode(DEFAULT);
        }
    }

    public static void loadFromConfig() {
        try {
            currentMode = HopMode.valueOf(Config.BHOP_MODE.get().toUpperCase());
        } catch (Exception e) {
            currentMode = DEFAULT;
        }
        speedMultiplier = Math.max(0.0, Config.SPEED_MULTIPLIER.get());
        autoBhop = Config.AUTO_BHOP.get();
    }
}

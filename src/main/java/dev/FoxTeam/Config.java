package dev.FoxTeam;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue FIRST_JOIN;
    public static final ForgeConfigSpec.DoubleValue SPEED_MULTIPLIER;
    public static final ForgeConfigSpec.BooleanValue AUTO_BHOP;
    public static final ForgeConfigSpec.ConfigValue<String> BHOP_MODE;

    public static final ForgeConfigSpec SPEC;

    static {
        BUILDER.push("general");

        FIRST_JOIN = BUILDER.define("firstJoin", true);
        SPEED_MULTIPLIER = BUILDER.defineInRange("speedMultiplier", 1.0, 0.0, 10.0);
        AUTO_BHOP = BUILDER.define("autoBhop", false);
        BHOP_MODE = BUILDER.define("bhopMode", "DEFAULT");

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public static void setFirstJoin(boolean value) {
        FIRST_JOIN.set(value);
        SPEC.save();
    }

    public static void setSpeedMultiplier(double value) {
        SPEED_MULTIPLIER.set(value);
        SPEC.save();
    }

    public static void setAutoBhop(boolean value) {
        AUTO_BHOP.set(value);
        SPEC.save();
    }

    public static void setBhopMode(String modeName) {
        BHOP_MODE.set(modeName);
        SPEC.save();
    }
}
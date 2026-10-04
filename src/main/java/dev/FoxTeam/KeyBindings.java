package dev.FoxTeam;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final String CATEGORY = "key.categories.foxhop";

    public static final KeyMapping SPEED_UP = new KeyMapping(
            "key.foxhop.speed_up",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_BRACKET,
            CATEGORY
    );

    public static final KeyMapping SPEED_DOWN = new KeyMapping(
            "key.foxhop.speed_down",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_BRACKET,
            CATEGORY
    );

    public static final KeyMapping TOGGLE_AUTO_BHOP = new KeyMapping(
            "key.foxhop.toggle_autobhop",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_I,
            CATEGORY
    );

    public static void register(RegisterKeyMappingsEvent event) {
        event.register(SPEED_UP);
        event.register(SPEED_DOWN);
        event.register(TOGGLE_AUTO_BHOP);
    }
}

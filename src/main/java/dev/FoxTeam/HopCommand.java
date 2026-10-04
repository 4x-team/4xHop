package dev.FoxTeam;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;

public class HopCommand {
    public static void register(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(
                Commands.literal("4xhop")
                        .executes(HopCommand::openGui)
                        .then(Commands.literal("bhop")
                                .executes(HopCommand::cycleMode))
        );
    }

    private static int openGui(CommandContext<CommandSourceStack> context) {
        Minecraft mc = Minecraft.getInstance();
        mc.tell(() -> {
            mc.setScreen(new HopScreen());
            if (mc.player != null) {
                mc.player.sendSystemMessage(Component.translatable("foxhop.command.opened_gui"));
            }
        });
        return 1;
    }

    private static int cycleMode(CommandContext<CommandSourceStack> context) {
        HopMode.cycleMode();
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            Component message = Component.translatable(
                    "foxhop.command.mode_changed",
                    HopMode.getCurrentMode().getDisplayName()
            );
            mc.player.sendSystemMessage(message);
        }
        MovementHandler.sendStatusMessage();
        return 1;
    }
}

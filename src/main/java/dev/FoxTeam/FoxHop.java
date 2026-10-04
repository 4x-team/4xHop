package dev.FoxTeam;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkConstants;

@Mod(FoxHop.MODID)
public class FoxHop {
    public static final String MODID = "foxhop";

    public FoxHop() {
        ModLoadingContext.get().registerExtensionPoint(
                IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(
                        () -> NetworkConstants.IGNORESERVERONLY,
                        (remote, isServer) -> true
                )
        );

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.SPEC);

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(KeyBindings::register);
        modBus.addListener(this::onConfigLoad);

        MinecraftForge.EVENT_BUS.addListener(MovementHandler::onClientTick);
        MinecraftForge.EVENT_BUS.addListener(HopCommand::register);
    }

    private void onConfigLoad(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == Config.SPEC) {
            HopMode.loadFromConfig();
        }
    }
}
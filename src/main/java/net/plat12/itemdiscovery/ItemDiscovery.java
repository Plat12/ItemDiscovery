package net.plat12.itemdiscovery;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.jarjar.nio.util.Lazy;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.plat12.itemdiscovery.util.packet.client.ClientPayloadHandler;
import net.plat12.itemdiscovery.util.packet.client.EffectNameMapPacket;
import net.plat12.itemdiscovery.util.packet.client.ItemNameMapPacket;
import net.plat12.itemdiscovery.util.packet.server.EffectNamePacket;
import net.plat12.itemdiscovery.util.packet.server.ItemNamePacket;
import net.plat12.itemdiscovery.util.packet.server.ServerPayloadHandler;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ItemDiscovery.MOD_ID)
public class ItemDiscovery {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "itemdiscovery";
    public static final String PACKET_PROTOCOL = MOD_ID + ".protocol";
    public static final Lazy<KeyMapping> NAME_CHANGE_KEY_MAPPING = Lazy.of(() -> new KeyMapping(
            "key.itemdiscovery.change_name",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "key.categories.misc"
    ));
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public ItemDiscovery(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);


        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ItemDiscovery) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);
        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");


    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber
    public static class ModRegistrationEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }


        @SubscribeEvent
        public static void registerBindings(RegisterKeyMappingsEvent event) {
            event.register(NAME_CHANGE_KEY_MAPPING.get());
        }

        @SubscribeEvent
        public static void registerPayloads(RegisterPayloadHandlersEvent event) {
            event.registrar(PACKET_PROTOCOL)
                    .playToClient(ItemNameMapPacket.TYPE,
                            ItemNameMapPacket.STREAM_CODEC,
                            ClientPayloadHandler::handleItemNameMap);

            event.registrar(PACKET_PROTOCOL)
                    .playToServer(ItemNamePacket.TYPE,
                            ItemNamePacket.STREAM_CODEC,
                            ServerPayloadHandler::handleItemName);

            event.registrar(PACKET_PROTOCOL)
                    .playToClient(EffectNameMapPacket.TYPE,
                            EffectNameMapPacket.STREAM_CODEC,
                            ClientPayloadHandler::handleEffectNameMap);

            event.registrar(PACKET_PROTOCOL)
                    .playToServer(EffectNamePacket.TYPE,
                            EffectNamePacket.STREAM_CODEC,
                            ServerPayloadHandler::handleEffectName);

        }


    }
}

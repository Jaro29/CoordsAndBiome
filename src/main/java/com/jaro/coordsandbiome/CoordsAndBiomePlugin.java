package com.jaro.coordsandbiome;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.logger.HytaleLogger;

import com.jaro.coordsandbiome.commands.CoordsAndBiomePluginCommand;
import com.jaro.coordsandbiome.listeners.PlayerListener;
import com.jaro.coordsandbiome.systems.CoordsHudTickingSystem;

import javax.annotation.Nonnull;
import java.util.logging.Level;

/**
 * CoordsAndBiome - A Hytale server plugin.
 */
public class CoordsAndBiomePlugin extends JavaPlugin {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static CoordsAndBiomePlugin instance;

    public CoordsAndBiomePlugin(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
    }

    /**
     * Get the plugin instance.
     * 
     * @return The plugin instance
     */
    public static CoordsAndBiomePlugin getInstance() {
        return instance;
    }

    @Override
    protected void setup() {
        LOGGER.at(Level.INFO).log("[CoordsAndBiome] Setting up...");

        // Register commands
        registerCommands();

        // Register event listeners
        registerListeners();

        // Register ECS system for coords/biome HUD (tick every 0.5s)
        getEntityStoreRegistry().registerSystem(new CoordsHudTickingSystem());

        LOGGER.at(Level.INFO).log("[CoordsAndBiome] Setup complete!");
    }

    /**
     * Register plugin commands.
     */
    private void registerCommands() {
        try {
            getCommandRegistry().registerCommand(new CoordsAndBiomePluginCommand());
            LOGGER.at(Level.INFO).log("[CoordsAndBiome] Registered /coo command");
        } catch (Exception e) {
            LOGGER.at(Level.WARNING).withCause(e).log("[CoordsAndBiome] Failed to register commands");
        }
    }

    /**
     * Register event listeners.
     */
    private void registerListeners() {
        EventRegistry eventBus = getEventRegistry();

        try {
            new PlayerListener().register(eventBus);
            LOGGER.at(Level.INFO).log("[CoordsAndBiome] Registered player event listeners");
        } catch (Exception e) {
            LOGGER.at(Level.WARNING).withCause(e).log("[CoordsAndBiome] Failed to register listeners");
        }
    }

    @Override
    protected void start() {
        LOGGER.at(Level.INFO).log("[CoordsAndBiome] Started!");
        LOGGER.at(Level.INFO).log("[CoordsAndBiome] Use /coo help for commands");
    }

    @Override
    protected void shutdown() {
        LOGGER.at(Level.INFO).log("[CoordsAndBiome] Shutting down...");
        instance = null;
    }
}
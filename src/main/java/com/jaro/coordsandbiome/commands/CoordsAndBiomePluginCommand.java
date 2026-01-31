package com.jaro.coordsandbiome.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

/**
 * Main command for CoordsAndBiome plugin.
 *
 * Usage:
 * - /coo help - Show available commands
 * - /coo info - Show plugin information
 * - /coo reload - Reload plugin configuration
 */
public class CoordsAndBiomePluginCommand extends AbstractCommandCollection {

    public CoordsAndBiomePluginCommand() {
        super("coo", "CoordsAndBiome plugin commands");

        // Add subcommands
        this.addSubCommand(new HelpSubCommand());
        this.addSubCommand(new InfoSubCommand());
        this.addSubCommand(new ReloadSubCommand());
    }

    @Override
    protected boolean canGeneratePermission() {
        return false; // No permission required for base command
    }
}
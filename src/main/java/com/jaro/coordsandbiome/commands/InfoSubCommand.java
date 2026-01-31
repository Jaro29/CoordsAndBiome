package com.jaro.coordsandbiome.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;

import com.jaro.coordsandbiome.CoordsAndBiomePlugin;

import javax.annotation.Nonnull;

/**
 * /coo info - Show plugin information
 */
public class InfoSubCommand extends CommandBase {

    public InfoSubCommand() {
        super("info", "Show plugin information");
        this.setPermissionGroup(null);
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        CoordsAndBiomePlugin plugin = CoordsAndBiomePlugin.getInstance();

        context.sendMessage(Message.raw(""));
        context.sendMessage(Message.raw("=== CoordsAndBiome Info ==="));
        context.sendMessage(Message.raw("Name: CoordsAndBiome"));
        context.sendMessage(Message.raw("Version: 1.0.0"));
        context.sendMessage(Message.raw("Author: jaro"));
        context.sendMessage(Message.raw("Status: " + (plugin != null ? "Running" : "Not loaded")));
        context.sendMessage(Message.raw("===================="));
    }
}
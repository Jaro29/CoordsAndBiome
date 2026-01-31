package com.jaro.coordsandbiome.ui;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import javax.annotation.Nonnull;

/**
 * HUD wyświetlający koordynaty i biom gracza nad hotbarem.
 */
public class CoordsHud extends CustomUIHud {

    private static final String UI_PATH = "coordsandbiome/coords_hud.ui";

    public CoordsHud(@Nonnull PlayerRef playerRef) {
        super(playerRef);
    }

    @Override
    protected void build(@Nonnull UICommandBuilder commandBuilder) {
        commandBuilder.append(UI_PATH);
    }

    /**
     * Aktualizuje tekst wyświetlany na HUD.
     */
    public void updateInfo(@Nonnull String text) {
        UICommandBuilder commandBuilder = new UICommandBuilder();
        commandBuilder.set("#CoordsLabel.TextSpans", Message.raw(text));
        update(false, commandBuilder);
    }
}

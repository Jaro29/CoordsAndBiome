package com.jaro.coordsandbiome.systems;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.entity.entities.player.hud.HudManager;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.universe.world.worldgen.IWorldGen;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.worldgen.biome.Biome;
import com.hypixel.hytale.server.worldgen.chunk.ChunkGenerator;
import com.hypixel.hytale.server.worldgen.chunk.ZoneBiomeResult;
import com.jaro.coordsandbiome.ui.CoordsHud;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * System aktualizujący HUD z koordynatami i biomem co 0.5 sekundy.
 */
public class CoordsHudTickingSystem extends DelayedEntitySystem<EntityStore> {

    private static final float TICK_INTERVAL = 0.5F;

    public CoordsHudTickingSystem() {
        super(TICK_INTERVAL);
    }

    @Override
    public void tick(
            float dt,
            int index,
            @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
            @Nonnull Store<EntityStore> store,
            @Nonnull CommandBuffer<EntityStore> commandBuffer
    ) {
        World world = store.getExternalData().getWorld();
        IWorldGen worldGen = world.getChunkStore().getGenerator();

        Player playerComponent = archetypeChunk.getComponent(index, Player.getComponentType());
        PlayerRef playerRef = archetypeChunk.getComponent(index, PlayerRef.getComponentType());
        TransformComponent transformComponent = archetypeChunk.getComponent(index, TransformComponent.getComponentType());

        if (playerComponent == null || playerRef == null || transformComponent == null) {
            return;
        }

        Vector3d position = transformComponent.getPosition();
        int x = (int) position.getX();
        int y = (int) position.getY();
        int z = (int) position.getZ();

        String biomeName = "—";
        if (worldGen instanceof ChunkGenerator generator) {
            int seed = (int) world.getWorldConfig().getSeed();
            ZoneBiomeResult result = generator.getZoneBiomeResultAt(seed, x, z);
            Biome biome = result.getBiome();
            if (biome != null) {
                biomeName = biome.getName();
            }
        }

        String text = String.format("Pozycja: %d, %d, %d | Biom: %s", x, y, z, biomeName);

        HudManager hudManager = playerComponent.getHudManager();
        CustomUIHud customHud = hudManager.getCustomHud();
        CoordsHud coordsHud;

        if (customHud instanceof CoordsHud existing) {
            coordsHud = existing;
        } else {
            coordsHud = new CoordsHud(playerRef);
            hudManager.setCustomHud(playerRef, coordsHud);
        }

        coordsHud.updateInfo(text);
    }

    @Nullable
    @Override
    public com.hypixel.hytale.component.query.Query<EntityStore> getQuery() {
        return Archetype.of(
                Player.getComponentType(),
                PlayerRef.getComponentType(),
                TransformComponent.getComponentType()
        );
    }
}

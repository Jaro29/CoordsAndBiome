package com.jaro.coordsandbiome.systems;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldMapTracker;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
import com.hypixel.hytale.protocol.packets.worldmap.UpdateWorldMap;
import com.hypixel.hytale.protocol.Transform;
import com.hypixel.hytale.protocol.Position;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * System updating distance on map markers and compass.
 */
public class MarkerDistanceSystem extends DelayedEntitySystem<EntityStore> {

    private static final float TICK_INTERVAL = 0.2F; // 5 times per second
    private static final Pattern DISTANCE_PATTERN = Pattern.compile(" \\(\\d+m\\)$");

    public MarkerDistanceSystem() {
        super(TICK_INTERVAL);
    }

    @Override
    public void tick(
            float dt,
            int index,
            @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
            @Nonnull Store<EntityStore> store,
            @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        Player player = archetypeChunk.getComponent(index, Player.getComponentType());
        TransformComponent transform = archetypeChunk.getComponent(index, TransformComponent.getComponentType());

        if (player == null || transform == null) {
            return;
        }

        World world = store.getExternalData().getWorld();

        // Execute on world thread for safety
        world.execute(() -> updateMarkers(player, transform.getPosition()));
    }

    private void updateMarkers(Player player, Vector3d playerPos) {
        WorldMapTracker tracker = player.getWorldMapTracker();
        // Use getSentMarkers() which returns Map<String, MapMarker>
        Collection<MapMarker> markers = tracker.getSentMarkers().values();

        if (markers == null || markers.isEmpty())
            return;

        List<MapMarker> updatedMarkers = new ArrayList<>();

        for (MapMarker marker : markers) {
            // Check transform and position
            Transform trans = marker.transform;
            if (trans == null)
                continue;

            Position pos = trans.position;
            if (pos == null)
                continue;

            // Calculate distance
            double dx = playerPos.x - pos.x;
            double dy = playerPos.y - pos.y;
            double dz = playerPos.z - pos.z;
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            int distInt = (int) distance;

            String currentName = marker.name;
            if (currentName == null)
                currentName = "Marker";

            // Strip existing distance
            String originalName = DISTANCE_PATTERN.matcher(currentName).replaceAll("");
            String newName = String.format("%s (%dm)", originalName, distInt);

            if (!newName.equals(currentName)) {
                marker.name = newName;
                updatedMarkers.add(marker);
            }
        }

        if (!updatedMarkers.isEmpty()) {
            // Send update packet
            // Constructor: UpdateWorldMap(MapChunk[] chunks, MapMarker[] addedMarkers,
            // String[] removedMarkers)
            // We use addedMarkers to overwrite
            UpdateWorldMap packet = new UpdateWorldMap(
                    null,
                    updatedMarkers.toArray(new MapMarker[0]),
                    null);

            // Use getPlayerConnection() (deprecated but available) or
            // getPlayerRef().getPacketHandler()
            player.getPlayerConnection().write(packet);
        }
    }

    @Nullable
    @Override
    public com.hypixel.hytale.component.query.Query<EntityStore> getQuery() {
        return Archetype.of(
                Player.getComponentType(),
                TransformComponent.getComponentType());
    }
}

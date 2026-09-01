package org.worldloom.manager.map;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.worldloom.serialize.data.CompMirror;
import org.worldloom.serialize.data.EntityData;
import org.worldloom.serialize.data.EntityDatum;
import org.worldloom.serialize.data.Properties;
import org.worldloom.serialize.data.Property;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameSnapshotMigrationTest {
    @AfterEach
    void resetManager() {
        GameSnapshotManager.setNull();
    }

    @Test
    void migratesLegacySystemAndPropertyTypeNames() {
        GameSnapshot snapshot = new GameSnapshot();
        snapshot.saveFormatVersion = 0;
        snapshot.curtMap = "island";

        Properties systemProperties = new Properties();
        systemProperties.add(property("state", "org.ltae.camera.CameraTarget"));
        snapshot.systemProps.put("org.ltae.system.CameraSystem", systemProperties);

        EntityDatum entity = new EntityDatum();
        entity.compMirrors = new Array<>();
        CompMirror component = new CompMirror();
        component.simpleName = "StateComp";
        component.properties = new Properties();
        component.properties.add(property(
            "state", "class org.ltae.component.StateComp"));
        entity.compMirrors.add(component);
        EntityData entities = new EntityData();
        entities.add(entity);
        snapshot.entityData = new ObjectMap<>();
        snapshot.entityData.put("island", entities);

        GameSnapshotManager manager = GameSnapshotManager.getInstance();
        manager.setSnapshot(snapshot);

        assertEquals(GameSnapshotManager.SAVE_FORMAT_VERSION,
            manager.getWorldState().saveFormatVersion);
        assertTrue(snapshot.systemProps.containsKey(
            "org.worldloom.system.CameraSystem"));
        assertFalse(snapshot.systemProps.containsKey(
            "org.ltae.system.CameraSystem"));
        assertEquals("org.worldloom.camera.CameraTarget",
            snapshot.systemProps.get("org.worldloom.system.CameraSystem")
                .first().type);
        assertEquals("class org.worldloom.component.StateComp",
            component.properties.first().type);
    }

    private Property property(String key, String type) {
        Property property = new Property();
        property.key = key;
        property.type = type;
        return property;
    }
}

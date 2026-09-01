package org.worldloom.loader;

import com.badlogic.gdx.utils.Json;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PropertyTypesBoundaryTest {
    @Test
    void containsOnlyUniqueEngineTypes() throws IOException {
        PropertyType[] types = loadTypes();
        Set<Integer> ids = new HashSet<>();
        Set<String> names = new HashSet<>();

        for (PropertyType type : types) {
            assertTrue(ids.add(type.id), "duplicate property type id: " + type.id);
            assertTrue(names.add(type.name),
                "duplicate property type name: " + type.name);
        }

        assertTrue(names.containsAll(Set.of(
            "Interactive", "Owner", "User", "Slice", "PointLight",
            "TopDownPointLight", "TopDownShadow")));
        assertTrue(Set.of(
            "RPGProp", "Inventory", "Item", "ShipProp",
            "WaterReflection", "Broadcaster", "Prefabricated",
            "OnInteractive").stream().noneMatch(names::contains));
    }

    private PropertyType[] loadTypes() throws IOException {
        try (InputStream stream = getClass().getResourceAsStream(
            "/propertytypes.json")) {
            assertNotNull(stream);
            String content = new String(stream.readAllBytes(),
                StandardCharsets.UTF_8);
            return new Json().fromJson(PropertyType[].class, content);
        }
    }
}

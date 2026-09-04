package org.worldloom.light;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TopDownShadowConfigTest {
    @Test
    void enablesSunByDefaultForBackwardCompatibility() {
        TopDownShadowConfig config = new TopDownShadowConfig();

        assertTrue(config.isSunEnabled(null));
        assertTrue(config.isSunEnabled("world"));
    }

    @Test
    void appliesMapOverrideBeforeDefaultValue() {
        TopDownShadowConfig config = new TopDownShadowConfig()
            .setDefaultSunEnabled(false)
            .setMapSunEnabled("world", true);

        assertTrue(config.isSunEnabled("world"));
        assertFalse(config.isSunEnabled("indoor"));

        config.removeMapSunEnabled("world");
        assertFalse(config.isSunEnabled("world"));
    }

    @Test
    void rejectsBlankMapName() {
        TopDownShadowConfig config = new TopDownShadowConfig();

        assertThrows(IllegalArgumentException.class,
            () -> config.setMapSunEnabled(" ", true));
    }
}

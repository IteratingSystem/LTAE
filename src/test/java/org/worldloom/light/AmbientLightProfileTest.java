package org.worldloom.light;

import com.badlogic.gdx.graphics.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class AmbientLightProfileTest {
    @Test
    void interpolatesBetweenHours() {
        Color[] colors = new Color[AmbientLightProfile.HOURS_PER_DAY];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = new Color(Color.BLACK);
        }
        colors[1] = new Color(0f, 0f, 0f, 1f);
        colors[2] = new Color(1f, 0.5f, 0.25f, 1f);

        Color output = new Color();
        AmbientLightProfile.hourly(colors).sample(1, 30, output);

        assertEquals(0.5f, output.r, 0.0001f);
        assertEquals(0.25f, output.g, 0.0001f);
        assertEquals(0.125f, output.b, 0.0001f);
        assertEquals(1f, output.a, 0.0001f);
    }

    @Test
    void usesDefaultProfileUnlessMapOverridesIt() {
        AmbientLightProfile defaultProfile = AmbientLightProfile.constant(Color.BLACK);
        AmbientLightProfile indoorProfile = AmbientLightProfile.constant(Color.WHITE);
        AmbientLightConfig config = new AmbientLightConfig(defaultProfile)
            .setMapProfile("indoor", indoorProfile);

        assertSame(defaultProfile, config.getProfile("world"));
        assertSame(indoorProfile, config.getProfile("indoor"));
    }
}

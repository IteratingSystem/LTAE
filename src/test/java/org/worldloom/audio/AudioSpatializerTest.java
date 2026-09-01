package org.worldloom.audio;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AudioSpatializerTest {
    @Test
    void sourceMovesFromLeftToRight() {
        AudioSpatializer.SpatialResult left = AudioSpatializer.calculate(
            0f, 0f, -100f, 0f, 10f, 200f, 1f, 1f);
        AudioSpatializer.SpatialResult right = AudioSpatializer.calculate(
            0f, 0f, 100f, 0f, 10f, 200f, 1f, 1f);

        assertTrue(left.pan() < 0f);
        assertTrue(right.pan() > 0f);
        assertEquals(left.attenuation(), right.attenuation(), 0.0001f);
    }

    @Test
    void distanceUsesConfiguredBoundaries() {
        AudioSpatializer.SpatialResult near = AudioSpatializer.calculate(
            0f, 0f, 10f, 0f, 20f, 100f, 1f, 1f);
        AudioSpatializer.SpatialResult far = AudioSpatializer.calculate(
            0f, 0f, 100f, 0f, 20f, 100f, 1f, 1f);
        AudioSpatializer.SpatialResult middle = AudioSpatializer.calculate(
            0f, 0f, 60f, 0f, 20f, 100f, 1f, 1f);

        assertEquals(1f, near.attenuation());
        assertEquals(0f, far.attenuation());
        assertEquals(0.5f, middle.attenuation(), 0.0001f);
    }

    @Test
    void higherRolloffReducesMiddleVolume() {
        float linear = AudioSpatializer.calculate(
            0f, 0f, 50f, 0f, 0f, 100f, 1f, 1f).attenuation();
        float quadratic = AudioSpatializer.calculate(
            0f, 0f, 50f, 0f, 0f, 100f, 2f, 1f).attenuation();

        assertTrue(quadratic < linear);
    }

    @Test
    void rejectsInvalidDistanceRange() {
        assertThrows(IllegalArgumentException.class,
            () -> AudioSpatializer.calculate(
                0f, 0f, 0f, 0f, 10f, 10f, 1f, 1f));
    }
}

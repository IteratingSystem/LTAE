package org.worldloom.audio;

import org.junit.jupiter.api.Test;
import org.worldloom.event.AudioEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AudioConfigTest {
    @Test
    void storesIndependentBusVolumes() {
        AudioConfig config = new AudioConfig()
            .setMasterVolume(0.8f)
            .setBusVolume(AudioBus.MUSIC, 0.4f);

        assertEquals(0.8f, config.getMasterVolume());
        assertEquals(0.4f, config.getBusVolume(AudioBus.MUSIC));
        assertEquals(1f, config.getBusVolume(AudioBus.SFX));
    }

    @Test
    void validatesEventParametersBeforeDispatch() {
        assertThrows(IllegalArgumentException.class,
            () -> AudioEvent.playSound("test.ogg").volume(1.1f));
        assertThrows(IllegalArgumentException.class,
            () -> AudioEvent.playSound("test.ogg").distances(100f, 10f));
        assertThrows(IllegalArgumentException.class,
            () -> AudioEvent.followListener(-1));
    }

    @Test
    void expandsShortAudioPaths() {
        assertEquals("audio/sounds/ui/click.ogg",
            AudioEvent.playSound("ui/click").path);
        assertEquals("audio/music/island_day.ogg",
            AudioEvent.playMusic("island_day").path);
        assertEquals("audio/sounds/world/door.wav",
            AudioEvent.playSound("world/door.wav").path);
    }

    @Test
    void keepsMatchingFullPathAndRejectsWrongDirectory() {
        assertEquals("audio/music/island_day.mp3",
            AudioEvent.playMusic("audio/music/island_day.mp3").path);
        assertThrows(IllegalArgumentException.class,
            () -> AudioEvent.playSound("audio/music/island_day.ogg"));
    }

    @Test
    void configuresMusicIntervalLoop() {
        AudioEvent event = AudioEvent.playMusic("town_theme.mp3")
            .fadeIn(1.5f)
            .loop(5f);

        assertEquals(AudioPlayMode.INTERVAL_LOOP, event.playMode);
        assertEquals(5f, event.loopIntervalSeconds);
        assertEquals(1.5f, event.fadeSeconds);
        assertThrows(IllegalArgumentException.class,
            () -> AudioEvent.playMusic("town_theme.mp3").loop(-1f));
    }
}

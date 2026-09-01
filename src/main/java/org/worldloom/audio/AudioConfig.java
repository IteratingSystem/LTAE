package org.worldloom.audio;

import java.util.EnumMap;

/**
 * 声音系统的全局配置。该对象应在创建 {@code WorldloomEngine} 时传入。
 */
public class AudioConfig {
    private float masterVolume = 1f;
    private int maxSoundInstances = 48;
    private int maxInstancesPerSound = 6;
    private float defaultMinDistance = 32f;
    private float defaultMaxDistance = 480f;
    private float defaultRolloff = 1f;
    private float defaultPanningStrength = 1f;
    private float oneShotTrackingSeconds = 15f;
    private final EnumMap<AudioBus, Float> busVolumes =
        new EnumMap<>(AudioBus.class);

    public AudioConfig() {
        for (AudioBus bus : AudioBus.values()) {
            busVolumes.put(bus, 1f);
        }
    }

    public float getMasterVolume() {
        return masterVolume;
    }

    public AudioConfig setMasterVolume(float masterVolume) {
        this.masterVolume = checkUnit(masterVolume, "masterVolume");
        return this;
    }

    public float getBusVolume(AudioBus bus) {
        return busVolumes.get(requireBus(bus));
    }

    public AudioConfig setBusVolume(AudioBus bus, float volume) {
        busVolumes.put(requireBus(bus), checkUnit(volume, "volume"));
        return this;
    }

    public int getMaxSoundInstances() {
        return maxSoundInstances;
    }

    public AudioConfig setMaxSoundInstances(int value) {
        maxSoundInstances = checkPositive(value, "maxSoundInstances");
        return this;
    }

    public int getMaxInstancesPerSound() {
        return maxInstancesPerSound;
    }

    public AudioConfig setMaxInstancesPerSound(int value) {
        maxInstancesPerSound = checkPositive(value, "maxInstancesPerSound");
        return this;
    }

    public float getDefaultMinDistance() {
        return defaultMinDistance;
    }

    public AudioConfig setDefaultMinDistance(float value) {
        defaultMinDistance = checkNonNegative(value, "defaultMinDistance");
        checkDistanceRange(defaultMinDistance, defaultMaxDistance);
        return this;
    }

    public float getDefaultMaxDistance() {
        return defaultMaxDistance;
    }

    public AudioConfig setDefaultMaxDistance(float value) {
        checkDistanceRange(defaultMinDistance, value);
        defaultMaxDistance = value;
        return this;
    }

    public float getDefaultRolloff() {
        return defaultRolloff;
    }

    public AudioConfig setDefaultRolloff(float value) {
        defaultRolloff = checkPositive(value, "defaultRolloff");
        return this;
    }

    public float getDefaultPanningStrength() {
        return defaultPanningStrength;
    }

    public AudioConfig setDefaultPanningStrength(float value) {
        defaultPanningStrength = checkNonNegative(value, "defaultPanningStrength");
        return this;
    }

    public float getOneShotTrackingSeconds() {
        return oneShotTrackingSeconds;
    }

    public AudioConfig setOneShotTrackingSeconds(float value) {
        oneShotTrackingSeconds = checkPositive(value, "oneShotTrackingSeconds");
        return this;
    }

    private static AudioBus requireBus(AudioBus bus) {
        if (bus == null) {
            throw new IllegalArgumentException("bus cannot be null");
        }
        return bus;
    }

    private static float checkUnit(float value, String name) {
        if (!Float.isFinite(value) || value < 0f || value > 1f) {
            throw new IllegalArgumentException(name + " must be between 0 and 1");
        }
        return value;
    }

    private static float checkPositive(float value, String name) {
        if (!Float.isFinite(value) || value <= 0f) {
            throw new IllegalArgumentException(name + " must be greater than 0");
        }
        return value;
    }

    private static int checkPositive(int value, String name) {
        if (value <= 0) {
            throw new IllegalArgumentException(name + " must be greater than 0");
        }
        return value;
    }

    private static float checkNonNegative(float value, String name) {
        if (!Float.isFinite(value) || value < 0f) {
            throw new IllegalArgumentException(name + " cannot be negative");
        }
        return value;
    }

    private static void checkDistanceRange(float minDistance,
                                           float maxDistance) {
        if (!Float.isFinite(maxDistance) || maxDistance <= minDistance) {
            throw new IllegalArgumentException(
                "maxDistance must be greater than minDistance");
        }
    }
}

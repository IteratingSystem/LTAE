package org.worldloom.system;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.event.common.Subscribe;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import org.worldloom.audio.AudioBus;
import org.worldloom.audio.AudioConfig;
import org.worldloom.audio.AudioPlayMode;
import org.worldloom.audio.AudioSpatializer;
import org.worldloom.component.Pos;
import org.worldloom.event.AudioEvent;
import org.worldloom.manager.AssetManager;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 统一管理短音效、流式音乐、音频总线与俯视角二维空间声音。
 */
public class AudioSystem extends BaseSystem {
    private static final String TAG = AudioSystem.class.getSimpleName();

    public M<Pos> mPos;

    private final AudioConfig config;
    private final EnumMap<AudioBus, Float> busVolumes =
        new EnumMap<>(AudioBus.class);
    private final EnumSet<AudioBus> pausedBuses =
        EnumSet.noneOf(AudioBus.class);
    private final LinkedHashMap<Long, AudioInstance> instances =
        new LinkedHashMap<>();
    private final Map<String, List<Long>> soundHandlesByPath = new HashMap<>();
    private final ConcurrentLinkedQueue<Long> completedMusicHandles =
        new ConcurrentLinkedQueue<>();
    private CameraSystem cameraSystem;
    private long nextHandle = 1L;
    private float masterVolume;
    private boolean fixedListener;
    private int listenerEntityId = -1;
    private float listenerX;
    private float listenerY;

    public AudioSystem() {
        this(new AudioConfig());
    }

    public AudioSystem(AudioConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("config cannot be null");
        }
        this.config = config;
        masterVolume = config.getMasterVolume();
        for (AudioBus bus : AudioBus.values()) {
            busVolumes.put(bus, config.getBusVolume(bus));
        }
    }

    @Override
    protected void processSystem() {
        Long completedHandle;
        while ((completedHandle = completedMusicHandles.poll()) != null) {
            removeInstance(completedHandle, false);
        }
        updateListener();
        float delta = world.getDelta();
        List<Long> toRemove = new ArrayList<>();
        for (AudioInstance instance : instances.values()) {
            if (instance.sourceEntityId >= 0) {
                if (!world.getEntityManager().isActive(instance.sourceEntityId)
                    || !mPos.has(instance.sourceEntityId)) {
                    if (instance.looping || instance.music != null) {
                        instance.stop();
                        toRemove.add(instance.handle);
                    }
                    continue;
                }
                Pos pos = mPos.get(instance.sourceEntityId);
                instance.x = pos.x;
                instance.y = pos.y;
            }

            updateFade(instance, delta, toRemove);
            if (!instance.paused && !instance.pausedByBus) {
                instance.age += delta;
            }
            if (instance.sound != null && !instance.looping
                && instance.age >= config.getOneShotTrackingSeconds()) {
                toRemove.add(instance.handle);
                continue;
            }
            applyInstanceVolume(instance);
        }
        for (long handle : toRemove) {
            removeInstance(handle, false);
        }
    }

    @Subscribe
    public void onEvent(AudioEvent event) {
        if (event == null) {
            return;
        }
        try {
            switch (event.type) {
                case AudioEvent.PLAY_SOUND -> playSound(event);
                case AudioEvent.PLAY_MUSIC -> playMusic(event);
                case AudioEvent.PAUSE -> pause(event.handle);
                case AudioEvent.RESUME -> resume(event.handle);
                case AudioEvent.STOP -> removeInstance(event.handle, true);
                case AudioEvent.MOVE_SOURCE -> move(event);
                case AudioEvent.FADE_TO -> fade(event);
                case AudioEvent.SET_MASTER_VOLUME -> setMasterVolume(event.volume);
                case AudioEvent.SET_BUS_VOLUME -> setBusVolume(event.bus, event.volume);
                case AudioEvent.PAUSE_BUS -> pauseBus(event.bus);
                case AudioEvent.RESUME_BUS -> resumeBus(event.bus);
                case AudioEvent.STOP_BUS -> stopBus(event.bus);
                case AudioEvent.SET_LISTENER_POSITION -> setListener(event.x, event.y);
                case AudioEvent.FOLLOW_LISTENER_ENTITY ->
                    followListener(event.listenerEntityId);
                case AudioEvent.USE_CAMERA_LISTENER -> useCameraListener();
                default -> logError("Unknown audio event type: " + event.type);
            }
        } catch (RuntimeException exception) {
            logError("Audio event failed: " + exception.getMessage(), exception);
        }
    }

    /**
     * 返回句柄当前是否仍由声音系统跟踪。
     */
    public boolean isTracked(long handle) {
        return instances.containsKey(handle);
    }

    private void playSound(AudioEvent event) {
        validatePlayEvent(event);
        trimSoundInstances(event.path);
        Sound sound = getAsset(event.path, Sound.class);
        AudioInstance instance = createInstance(event);
        instance.sound = sound;
        instance.looping = event.playMode == AudioPlayMode.LOOP;
        if (!updateSourceFromEntity(instance)) {
            logError("Audio source entity has no active Pos: "
                + instance.sourceEntityId);
            return;
        }
        VolumePan value = calculateVolumePan(instance);
        float initialVolume = event.fadeSeconds > 0f ? 0f : value.volume;
        long soundId = instance.looping
            ? sound.loop(initialVolume, event.pitch, value.pan)
            : sound.play(initialVolume, event.pitch, value.pan);
        if (soundId < 0L) {
            logError("Sound instance limit reached: " + event.path);
            return;
        }
        instance.nativeId = soundId;
        configureFadeIn(instance, event);
        addInstance(instance);
        applyInitialBusPause(instance);
        event.handle = instance.handle;
    }

    private void playMusic(AudioEvent event) {
        validatePlayEvent(event);
        stopMusicUsingPath(event.path);
        if (event.replaceMusic) {
            replaceMusic(event.fadeSeconds);
        }
        Music music = getAsset(event.path, Music.class);
        AudioInstance instance = createInstance(event);
        instance.music = music;
        instance.looping = event.playMode == AudioPlayMode.LOOP;
        if (!updateSourceFromEntity(instance)) {
            logError("Audio source entity has no active Pos: "
                + instance.sourceEntityId);
            return;
        }
        music.setLooping(instance.looping);
        music.setOnCompletionListener(completed ->
            completedMusicHandles.add(instance.handle));
        configureFadeIn(instance, event);
        addInstance(instance);
        applyInstanceVolume(instance);
        music.play();
        applyInitialBusPause(instance);
        event.handle = instance.handle;
    }

    private AudioInstance createInstance(AudioEvent event) {
        AudioInstance instance = new AudioInstance();
        instance.handle = nextHandle++;
        instance.path = event.path;
        instance.bus = event.bus;
        instance.baseVolume = event.volume;
        instance.pitch = event.pitch;
        instance.spatial = event.spatial;
        instance.x = event.x;
        instance.y = event.y;
        instance.sourceEntityId = event.sourceEntityId;
        instance.minDistance = event.minDistance >= 0f
            ? event.minDistance : config.getDefaultMinDistance();
        instance.maxDistance = event.maxDistance >= 0f
            ? event.maxDistance : config.getDefaultMaxDistance();
        instance.rolloff = event.rolloff >= 0f
            ? event.rolloff : config.getDefaultRolloff();
        instance.panningStrength = event.panningStrength >= 0f
            ? event.panningStrength : config.getDefaultPanningStrength();
        return instance;
    }

    private void configureFadeIn(AudioInstance instance, AudioEvent event) {
        if (event.fadeSeconds <= 0f) {
            return;
        }
        instance.fadeStart = 0f;
        instance.fadeTarget = instance.baseVolume;
        instance.baseVolume = 0f;
        instance.fadeDuration = event.fadeSeconds;
        instance.fadeElapsed = 0f;
    }

    private void addInstance(AudioInstance instance) {
        instances.put(instance.handle, instance);
        if (instance.sound != null) {
            soundHandlesByPath.computeIfAbsent(instance.path,
                ignored -> new ArrayList<>()).add(instance.handle);
        }
    }

    private void trimSoundInstances(String path) {
        List<Long> sameSound = soundHandlesByPath.get(path);
        while (sameSound != null
            && sameSound.size() >= config.getMaxInstancesPerSound()) {
            removeInstance(sameSound.get(0), true);
            sameSound = soundHandlesByPath.get(path);
        }
        while (countSoundInstances() >= config.getMaxSoundInstances()) {
            Long oldest = firstSoundHandle();
            if (oldest == null) {
                break;
            }
            removeInstance(oldest, true);
        }
    }

    private int countSoundInstances() {
        int count = 0;
        for (AudioInstance instance : instances.values()) {
            if (instance.sound != null) {
                count++;
            }
        }
        return count;
    }

    private Long firstSoundHandle() {
        for (AudioInstance instance : instances.values()) {
            if (instance.sound != null) {
                return instance.handle;
            }
        }
        return null;
    }

    private void replaceMusic(float seconds) {
        List<AudioInstance> musicInstances = new ArrayList<>();
        for (AudioInstance instance : instances.values()) {
            if (instance.music != null && instance.bus == AudioBus.MUSIC) {
                musicInstances.add(instance);
            }
        }
        for (AudioInstance instance : musicInstances) {
            if (seconds <= 0f) {
                removeInstance(instance.handle, true);
            } else {
                startFade(instance, 0f, seconds, true);
            }
        }
    }

    private void stopMusicUsingPath(String path) {
        List<Long> handles = new ArrayList<>();
        for (AudioInstance instance : instances.values()) {
            if (instance.music != null && instance.path.equals(path)) {
                handles.add(instance.handle);
            }
        }
        for (long handle : handles) {
            removeInstance(handle, true);
        }
    }

    private void pause(long handle) {
        AudioInstance instance = instances.get(handle);
        if (instance == null || instance.paused) {
            return;
        }
        if (!instance.pausedByBus) {
            instance.pause();
        }
        instance.paused = true;
    }

    private void resume(long handle) {
        AudioInstance instance = instances.get(handle);
        if (instance == null || !instance.paused
            || pausedBuses.contains(instance.bus)) {
            return;
        }
        instance.resume();
        instance.paused = false;
        applyInstanceVolume(instance);
    }

    private void move(AudioEvent event) {
        AudioInstance instance = instances.get(event.handle);
        if (instance == null) {
            return;
        }
        instance.spatial = true;
        instance.sourceEntityId = -1;
        instance.x = event.x;
        instance.y = event.y;
        applyInstanceVolume(instance);
    }

    private void fade(AudioEvent event) {
        AudioInstance instance = instances.get(event.handle);
        if (instance == null) {
            return;
        }
        startFade(instance, event.targetVolume, event.fadeSeconds,
            event.targetVolume == 0f);
    }

    private void startFade(AudioInstance instance, float target,
                           float seconds, boolean stopAtEnd) {
        if (seconds <= 0f) {
            instance.baseVolume = target;
            applyInstanceVolume(instance);
            if (stopAtEnd) {
                removeInstance(instance.handle, true);
            }
            return;
        }
        instance.fadeStart = instance.baseVolume;
        instance.fadeTarget = target;
        instance.fadeDuration = seconds;
        instance.fadeElapsed = 0f;
        instance.stopAfterFade = stopAtEnd;
    }

    private void updateFade(AudioInstance instance, float delta,
                            List<Long> toRemove) {
        if (instance.fadeDuration <= 0f) {
            return;
        }
        instance.fadeElapsed = Math.min(instance.fadeElapsed + delta,
            instance.fadeDuration);
        float progress = instance.fadeElapsed / instance.fadeDuration;
        instance.baseVolume = MathUtils.lerp(instance.fadeStart,
            instance.fadeTarget, progress);
        if (progress >= 1f) {
            instance.fadeDuration = 0f;
            if (instance.stopAfterFade) {
                instance.stop();
                toRemove.add(instance.handle);
            }
        }
    }

    private void setMasterVolume(float volume) {
        masterVolume = MathUtils.clamp(volume, 0f, 1f);
        updateAllVolumes();
    }

    private void setBusVolume(AudioBus bus, float volume) {
        busVolumes.put(bus, MathUtils.clamp(volume, 0f, 1f));
        updateAllVolumes();
    }

    private void pauseBus(AudioBus bus) {
        if (!pausedBuses.add(bus)) {
            return;
        }
        for (AudioInstance instance : instances.values()) {
            if (instance.bus == bus && !instance.paused) {
                instance.pause();
                instance.pausedByBus = true;
            }
        }
    }

    private void resumeBus(AudioBus bus) {
        if (!pausedBuses.remove(bus)) {
            return;
        }
        for (AudioInstance instance : instances.values()) {
            if (instance.bus == bus && instance.pausedByBus) {
                if (!instance.paused) {
                    instance.resume();
                }
                instance.pausedByBus = false;
                applyInstanceVolume(instance);
            }
        }
    }

    private void stopBus(AudioBus bus) {
        List<Long> handles = new ArrayList<>();
        for (AudioInstance instance : instances.values()) {
            if (instance.bus == bus) {
                handles.add(instance.handle);
            }
        }
        for (long handle : handles) {
            removeInstance(handle, true);
        }
    }

    private void setListener(float x, float y) {
        listenerEntityId = -1;
        fixedListener = true;
        listenerX = x;
        listenerY = y;
        updateAllVolumes();
    }

    private void followListener(int entityId) {
        listenerEntityId = entityId;
        fixedListener = false;
        updateListener();
        updateAllVolumes();
    }

    private void useCameraListener() {
        listenerEntityId = -1;
        fixedListener = false;
        updateListener();
        updateAllVolumes();
    }

    private void updateListener() {
        if (listenerEntityId >= 0 && world.getEntityManager().isActive(listenerEntityId)
            && mPos.has(listenerEntityId)) {
            Pos pos = mPos.get(listenerEntityId);
            listenerX = pos.x;
            listenerY = pos.y;
            return;
        }
        if (!fixedListener && cameraSystem != null && cameraSystem.camera != null) {
            listenerX = cameraSystem.camera.position.x;
            listenerY = cameraSystem.camera.position.y;
        }
    }

    private boolean updateSourceFromEntity(AudioInstance instance) {
        if (instance.sourceEntityId < 0) {
            return true;
        }
        if (world.getEntityManager().isActive(instance.sourceEntityId)
            && mPos.has(instance.sourceEntityId)) {
            Pos pos = mPos.get(instance.sourceEntityId);
            instance.x = pos.x;
            instance.y = pos.y;
            return true;
        }
        return false;
    }

    private void applyInitialBusPause(AudioInstance instance) {
        if (!pausedBuses.contains(instance.bus)) {
            return;
        }
        instance.pause();
        instance.pausedByBus = true;
    }

    private void updateAllVolumes() {
        for (AudioInstance instance : instances.values()) {
            applyInstanceVolume(instance);
        }
    }

    private void applyInstanceVolume(AudioInstance instance) {
        if (instance.paused || instance.pausedByBus) {
            return;
        }
        VolumePan value = calculateVolumePan(instance);
        if (instance.sound != null) {
            instance.sound.setPan(instance.nativeId, value.pan, value.volume);
        } else if (instance.music != null) {
            instance.music.setPan(value.pan, value.volume);
        }
    }

    private VolumePan calculateVolumePan(AudioInstance instance) {
        float attenuation = 1f;
        float pan = 0f;
        if (instance.spatial) {
            AudioSpatializer.SpatialResult result = AudioSpatializer.calculate(
                listenerX, listenerY, instance.x, instance.y,
                instance.minDistance, instance.maxDistance,
                instance.rolloff, instance.panningStrength);
            attenuation = result.attenuation();
            pan = result.pan();
        }
        float volume = MathUtils.clamp(instance.baseVolume * masterVolume
            * busVolumes.get(instance.bus) * attenuation, 0f, 1f);
        return new VolumePan(volume, pan);
    }

    private void removeInstance(long handle, boolean stop) {
        AudioInstance instance = instances.remove(handle);
        if (instance == null) {
            return;
        }
        if (stop) {
            instance.stop();
        }
        if (instance.sound != null) {
            List<Long> handles = soundHandlesByPath.get(instance.path);
            if (handles != null) {
                handles.remove(handle);
                if (handles.isEmpty()) {
                    soundHandlesByPath.remove(instance.path);
                }
            }
        }
    }

    private <T> T getAsset(String path, Class<T> type) {
        com.badlogic.gdx.assets.AssetManager assets =
            AssetManager.getInstance().getGdxAssetManager();
        if (!assets.isLoaded(path, type)) {
            throw new IllegalArgumentException("audio asset is not loaded: " + path);
        }
        return assets.get(path, type);
    }

    private static void validatePlayEvent(AudioEvent event) {
        if (event.path == null || event.path.isBlank()) {
            throw new IllegalArgumentException("audio path cannot be blank");
        }
        if (event.bus == null || event.playMode == null) {
            throw new IllegalArgumentException("audio bus and play mode are required");
        }
    }

    private static void logError(String message) {
        if (Gdx.app != null) {
            Gdx.app.error(TAG, message);
        }
    }

    private static void logError(String message, Throwable throwable) {
        if (Gdx.app != null) {
            Gdx.app.error(TAG, message, throwable);
        }
    }

    @Override
    protected void dispose() {
        for (AudioInstance instance : instances.values()) {
            instance.stop();
        }
        instances.clear();
        soundHandlesByPath.clear();
        completedMusicHandles.clear();
    }

    private static final class AudioInstance {
        private long handle;
        private long nativeId;
        private String path;
        private AudioBus bus;
        private Sound sound;
        private Music music;
        private float baseVolume;
        private float pitch;
        private boolean looping;
        private boolean spatial;
        private int sourceEntityId;
        private float x;
        private float y;
        private float minDistance;
        private float maxDistance;
        private float rolloff;
        private float panningStrength;
        private float age;
        private boolean paused;
        private boolean pausedByBus;
        private float fadeStart;
        private float fadeTarget;
        private float fadeDuration;
        private float fadeElapsed;
        private boolean stopAfterFade;

        private void pause() {
            if (sound != null) {
                sound.pause(nativeId);
            } else if (music != null) {
                music.pause();
            }
        }

        private void resume() {
            if (sound != null) {
                sound.resume(nativeId);
            } else if (music != null) {
                music.play();
            }
        }

        private void stop() {
            if (sound != null) {
                sound.stop(nativeId);
            } else if (music != null) {
                music.stop();
            }
        }
    }

    private record VolumePan(float volume, float pan) {
    }
}

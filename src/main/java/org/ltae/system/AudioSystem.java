package org.ltae.system;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.ObjectMap;
import org.ltae.manager.asset.AssetLoader;

/**
 * 音频系统
 * 
 * 直接使用LoadingManager加载的Music和Sound资源
 * 享受libGDX AssetManager的引用计数机制
 */
public class AudioSystem extends BaseSystem {
    private static final String TAG = AudioSystem.class.getSimpleName();
    
    private Music currentBgm;
    private String currentBgmName;
    private Music currentAmbient;
    private boolean fading = false;
    private float fadeDuration = 1.0f;
    private float fadeTimer = 0f;
    private float fadeFromVolume = 0f;
    private float fadeToVolume = 1.0f;
    private String targetBgmName;
    
    private float listenerX = 0;
    private float listenerY = 0;
    private float maxDistance = 500f;
    private boolean enableSpatialAudio = true;
    
    // 音量控制
    private float masterVolume = 1.0f;
    private float bgmVolume = 1.0f;
    private float seVolume = 1.0f;
    private float uiVolume = 1.0f;
    private float ambientVolume = 1.0f;
    private boolean muted = false;
    
    public AudioSystem() {}
    
    @Override
    protected void initialize() {
        // 音频资源由AssetLoader管理
    }
    
    @Override
    protected void processSystem() {
        processFade();
    }
    
    private void processFade() {
        if (!fading) return;
        
        fadeTimer += Gdx.graphics.getDeltaTime();
        float progress = fadeTimer / fadeDuration;
        
        if (progress >= 1.0f) {
            progress = 1.0f;
            fading = false;
            
            if (currentBgm != null) {
                currentBgm.setVolume(getBgmVolume() * fadeToVolume);
            }
            
            if (fadeToVolume == 0 && currentBgm != null) {
                currentBgm.stop();
            }
            
            if (targetBgmName != null && fadeToVolume > 0) {
                playBgm(targetBgmName, false);
            }
            return;
        }
        
        float volume = fadeFromVolume + (fadeToVolume - fadeFromVolume) * progress;
        if (currentBgm != null) {
            currentBgm.setVolume(getBgmVolume() * volume);
        }
    }
    
    // ==================== BGM ====================
    
    public void playBgm(String name) {
        playBgm(name, true);
    }
    
    public void playBgm(String name, boolean fade) {
        Music music = findMusic(name);
        if (music == null) {
            Gdx.app.error(TAG, "BGM not found: " + name);
            return;
        }
        
        if (fade && currentBgm != null && currentBgm.isPlaying()) {
            fadeOutAndPlay(name, 1.0f);
            return;
        }
        
        if (currentBgm != null) {
            currentBgm.stop();
        }
        
        currentBgm = music;
        currentBgmName = name;
        currentBgm.setLooping(true);
        currentBgm.setVolume(getBgmVolume());
        currentBgm.play();
    }
    
    public void fadeOutAndPlay(String name, float duration) {
        targetBgmName = name;
        fadeFromVolume = bgmVolume;
        fadeToVolume = 0;
        fadeDuration = duration;
        fadeTimer = 0;
        fading = true;
    }
    
    public void fadeIn(String name, float duration) {
        Music music = findMusic(name);
        if (music == null) {
            Gdx.app.error(TAG, "Music not found: " + name);
            return;
        }
        
        if (currentBgm != null) {
            currentBgm.stop();
        }
        
        currentBgm = music;
        currentBgmName = name;
        currentBgm.setLooping(true);
        currentBgm.setVolume(0);
        currentBgm.play();
        
        fadeFromVolume = 0;
        fadeToVolume = 1;
        fadeDuration = duration;
        fadeTimer = 0;
        fading = true;
    }
    
    public void stopBgm() {
        if (currentBgm != null) {
            currentBgm.stop();
        }
    }
    
    public void pauseBgm() {
        if (currentBgm != null) {
            currentBgm.pause();
        }
    }
    
    public void resumeBgm() {
        if (currentBgm != null) {
            currentBgm.play();
        }
    }
    
    public boolean isBgmPlaying() {
        return currentBgm != null && currentBgm.isPlaying();
    }
    
    public String getCurrentBgmName() {
        return currentBgmName;
    }
    
    // ==================== Ambient ====================
    
    public void playAmbient(String name) {
        Music ambient = findMusic(name);
        if (ambient == null) {
            Gdx.app.error(TAG, "Ambient not found: " + name);
            return;
        }
        
        if (currentAmbient != null) {
            currentAmbient.stop();
        }
        
        currentAmbient = ambient;
        currentAmbient.setLooping(true);
        currentAmbient.setVolume(getAmbientVolume());
        currentAmbient.play();
    }
    
    public void stopAmbient() {
        if (currentAmbient != null) {
            currentAmbient.stop();
        }
    }
    
    // ==================== Sound Effects ====================
    
    public void playSe(String name) {
        playSe(name, 1.0f, 1.0f, 0);
    }
    
    public void playSe(String name, float volume) {
        playSe(name, volume, 1.0f, 0);
    }
    
    public void playSe(String name, float volume, float pitch) {
        playSe(name, volume, pitch, 0);
    }
    
    public long playSe(String name, float volume, float pitch, float pan) {
        Sound sound = findSound(name);
        if (sound == null) {
            Gdx.app.error(TAG, "Sound effect not found: " + name);
            return -1;
        }
        
        float finalVolume = volume * masterVolume * seVolume;
        return sound.play(finalVolume, pitch, pan);
    }
    
    public void playUiSound(String name) {
        playUiSound(name, 1.0f);
    }
    
    public void playUiSound(String name, float volume) {
        Sound sound = findSound(name);
        if (sound == null) {
            Gdx.app.error(TAG, "UI sound not found: " + name);
            return;
        }
        
        float finalVolume = volume * masterVolume * uiVolume;
        sound.play(finalVolume);
    }
    
    public void stopSound(long soundId) {
        if (AssetLoader.getManager() == null) return;
        
        for (Sound sound : AssetLoader.getAllSounds().values()) {
            sound.stop(soundId);
        }
    }
    
    public void stopAllSounds() {
        if (AssetLoader.getManager() == null) return;
        
        for (Sound sound : AssetLoader.getAllSounds().values()) {
            sound.stop();
        }
    }
    
    // ==================== 3D Spatial Audio ====================
    
    public void setListenerPosition(float x, float y) {
        this.listenerX = x;
        this.listenerY = y;
    }
    
    public float getListenerX() {
        return listenerX;
    }
    
    public float getListenerY() {
        return listenerY;
    }
    
    public void setMaxDistance(float distance) {
        this.maxDistance = distance;
    }
    
    public float getMaxDistance() {
        return maxDistance;
    }
    
    public void setSpatialAudioEnabled(boolean enabled) {
        this.enableSpatialAudio = enabled;
    }
    
    public boolean isSpatialAudioEnabled() {
        return enableSpatialAudio;
    }
    
    private float calculatePan(float sourceX) {
        float relativeX = sourceX - listenerX;
        float pan = relativeX / (maxDistance * 0.5f);
        return Math.max(-1f, Math.min(1f, pan));
    }
    
    private float calculateVolumeAttenuation(float sourceX, float sourceY, float baseVolume) {
        if (!enableSpatialAudio) return baseVolume;
        
        float dx = sourceX - listenerX;
        float dy = sourceY - listenerY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        if (distance >= maxDistance) return 0;
        if (distance <= 0) return baseVolume;
        
        float attenuation = 1f - (distance / maxDistance);
        attenuation = attenuation * attenuation;
        
        return baseVolume * attenuation;
    }
    
    public long playSeAt(String name, float x, float y) {
        return playSeAt(name, x, y, 1.0f, 1.0f);
    }
    
    public long playSeAt(String name, float x, float y, float volume) {
        return playSeAt(name, x, y, volume, 1.0f);
    }
    
    public long playSeAt(String name, float x, float y, float volume, float pitch) {
        Sound sound = findSound(name);
        if (sound == null) {
            Gdx.app.error(TAG, "Sound effect not found: " + name);
            return -1;
        }
        
        float finalVolume = calculateVolumeAttenuation(x, y, volume) * masterVolume * seVolume;
        float pan = enableSpatialAudio ? calculatePan(x) : 0f;
        
        return sound.play(finalVolume, pitch, pan);
    }
    
    public long playSeAt(String name, float x, float y, float volume, float pitch, float pan) {
        Sound sound = findSound(name);
        if (sound == null) {
            Gdx.app.error(TAG, "Sound effect not found: " + name);
            return -1;
        }
        
        float finalVolume = calculateVolumeAttenuation(x, y, volume) * masterVolume * seVolume;
        
        return sound.play(finalVolume, pitch, pan);
    }
    
    public void updateSoundPosition(long soundId, float x, float y) {
        updateSoundPosition(soundId, x, y, 1.0f);
    }
    
    public void updateSoundPosition(long soundId, float x, float y, float volume) {
        if (AssetLoader.getManager() == null) return;
        
        for (Sound sound : AssetLoader.getAllSounds().values()) {
            float finalVolume = calculateVolumeAttenuation(x, y, volume) * masterVolume * seVolume;
            float pan = enableSpatialAudio ? calculatePan(x) : 0f;
            sound.setVolume(soundId, finalVolume);
            sound.setPan(soundId, pan, 1.0f);
        }
    }
    
    // ==================== Volume Control ====================
    
    private float getBgmVolume() {
        return muted ? 0 : masterVolume * bgmVolume;
    }
    
    private float getAmbientVolume() {
        return muted ? 0 : masterVolume * ambientVolume;
    }
    
    public void setMasterVolume(float volume) {
        this.masterVolume = Math.max(0, Math.min(1, volume));
        updateVolumes();
    }
    
    public void setBgmVolume(float volume) {
        this.bgmVolume = Math.max(0, Math.min(1, volume));
        if (currentBgm != null) {
            currentBgm.setVolume(getBgmVolume());
        }
    }
    
    public void setSeVolume(float volume) {
        this.seVolume = Math.max(0, Math.min(1, volume));
    }
    
    public void setUiVolume(float volume) {
        this.uiVolume = Math.max(0, Math.min(1, volume));
    }
    
    public void setAmbientVolume(float volume) {
        this.ambientVolume = Math.max(0, Math.min(1, volume));
        if (currentAmbient != null) {
            currentAmbient.setVolume(getAmbientVolume());
        }
    }
    
    public float getMasterVolume() {
        return masterVolume;
    }
    
    public float getBgmVolumeValue() {
        return bgmVolume;
    }
    
    public float getSeVolumeValue() {
        return seVolume;
    }
    
    public float getUiVolumeValue() {
        return uiVolume;
    }
    
    public float getAmbientVolumeValue() {
        return ambientVolume;
    }
    
    private void updateVolumes() {
        if (currentBgm != null) {
            currentBgm.setVolume(getBgmVolume());
        }
        if (currentAmbient != null) {
            currentAmbient.setVolume(getAmbientVolume());
        }
    }
    
    public void setMuted(boolean muted) {
        this.muted = muted;
        updateVolumes();
    }
    
    public boolean isMuted() {
        return muted;
    }
    
    public void toggleMute() {
        setMuted(!muted);
    }
    
    // ==================== Helpers ====================
    
    private Music findMusic(String name) {
        if (AssetLoader.getManager() == null) return null;
        
        // 先尝试完整路径
        String path = "audio/bgm/" + name + ".bgm.mp3";
        if (AssetLoader.isLoaded(path)) {
            return AssetLoader.get(path, Music.class);
        }
        
        // 尝试从已加载的Music中查找
        ObjectMap<String, Music> allMusic = AssetLoader.getAllMusic();
        return allMusic.get(name);
    }
    
    private Sound findSound(String name) {
        if (AssetLoader.getManager() == null) return null;
        
        // 尝试从已加载的Sound中查找
        ObjectMap<String, Sound> allSounds = AssetLoader.getAllSounds();
        return allSounds.get(name);
    }
    
    // ==================== Lifecycle ====================
    
    @Override
    protected void dispose() {
        if (currentBgm != null) {
            currentBgm.stop();
        }
        if (currentAmbient != null) {
            currentAmbient.stop();
        }
    }
}

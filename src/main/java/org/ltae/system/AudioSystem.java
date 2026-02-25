package org.ltae.system;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.ObjectMap;
import org.ltae.enums.AudioType;
import org.ltae.manager.AudioManager;

public class AudioSystem extends BaseSystem {
    private static final String TAG = AudioSystem.class.getSimpleName();
    
    private AudioManager audioManager;
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
    
    public AudioSystem() {
        audioManager = AudioManager.getInstance();
    }
    
    @Override
    protected void initialize() {
        audioManager.loadAllAudio();
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
                currentBgm.setVolume(audioManager.getMasterVolume() * audioManager.getBgmVolume() * fadeToVolume);
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
            currentBgm.setVolume(audioManager.getMasterVolume() * audioManager.getBgmVolume() * volume);
        }
    }
    
    public void playBgm(String name) {
        playBgm(name, true);
    }
    
    public void playBgm(String name, boolean fade) {
        Music music = audioManager.getMusic(name);
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
        currentBgm.setVolume(audioManager.getMasterVolume() * audioManager.getBgmVolume());
        currentBgm.play();
    }
    
    public void fadeOutAndPlay(String name, float duration) {
        targetBgmName = name;
        fadeFromVolume = audioManager.getBgmVolume();
        fadeToVolume = 0;
        fadeDuration = duration;
        fadeTimer = 0;
        fading = true;
    }
    
    public void fadeIn(String name, float duration) {
        Music music = audioManager.getMusic(name);
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
    
    public void playBgmVol(float volume) {
        if (currentBgm != null) {
            currentBgm.setVolume(volume);
        }
    }
    
    public boolean isBgmPlaying() {
        return currentBgm != null && currentBgm.isPlaying();
    }
    
    public String getCurrentBgmName() {
        return currentBgmName;
    }
    
    public void playAmbient(String name) {
        Music ambient = audioManager.getMusic(name);
        if (ambient == null) {
            Gdx.app.error(TAG, "Ambient not found: " + name);
            return;
        }
        
        if (currentAmbient != null) {
            currentAmbient.stop();
        }
        
        currentAmbient = ambient;
        currentAmbient.setLooping(true);
        currentAmbient.setVolume(audioManager.getMasterVolume() * audioManager.getAmbientVolume());
        currentAmbient.play();
    }
    
    public void stopAmbient() {
        if (currentAmbient != null) {
            currentAmbient.stop();
        }
    }
    
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
        Sound sound = audioManager.getSound(name);
        if (sound == null) {
            Gdx.app.error(TAG, "Sound effect not found: " + name);
            return -1;
        }
        
        float finalVolume = volume * audioManager.getMasterVolume() * audioManager.getSeVolume();
        return sound.play(finalVolume, pitch, pan);
    }
    
    public void playUiSound(String name) {
        playUiSound(name, 1.0f);
    }
    
    public void playUiSound(String name, float volume) {
        Sound sound = audioManager.getSound(name);
        if (sound == null) {
            Gdx.app.error(TAG, "UI sound not found: " + name);
            return;
        }
        
        float finalVolume = volume * audioManager.getMasterVolume() * audioManager.getUiVolume();
        sound.play(finalVolume);
    }
    
    public void stopSound(long soundId) {
        for (Sound sound : audioManager.getAllSounds().values()) {
            sound.stop(soundId);
        }
    }
    
    public void stopAllSounds() {
        for (Sound sound : audioManager.getAllSounds().values()) {
            sound.stop();
        }
    }
    
    public void setMasterVolume(float volume) {
        audioManager.setMasterVolume(volume);
    }
    
    public void setBgmVolume(float volume) {
        audioManager.setBgmVolume(volume);
    }
    
    public void setSeVolume(float volume) {
        audioManager.setSeVolume(volume);
    }
    
    public void setUiVolume(float volume) {
        audioManager.setUiVolume(volume);
    }
    
    public void setAmbientVolume(float volume) {
        audioManager.setAmbientVolume(volume);
    }
    
    public void setMuted(boolean muted) {
        audioManager.setMuted(muted);
    }
    
    public boolean isMuted() {
        return audioManager.isMuted();
    }
    
    public void toggleMute() {
        audioManager.toggleMute();
    }
    
    public ObjectMap<String, Music> getAllBgm() {
        return audioManager.getAllMusics();
    }
    
    public ObjectMap<String, Sound> getAllSe() {
        return audioManager.getAllSounds();
    }
    
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
        Sound sound = audioManager.getSound(name);
        if (sound == null) {
            Gdx.app.error(TAG, "Sound effect not found: " + name);
            return -1;
        }
        
        float finalVolume = calculateVolumeAttenuation(x, y, volume) * audioManager.getMasterVolume() * audioManager.getSeVolume();
        float pan = enableSpatialAudio ? calculatePan(x) : 0f;
        
        return sound.play(finalVolume, pitch, pan);
    }
    
    public long playSeAt(String name, float x, float y, float volume, float pitch, float pan) {
        Sound sound = audioManager.getSound(name);
        if (sound == null) {
            Gdx.app.error(TAG, "Sound effect not found: " + name);
            return -1;
        }
        
        float finalVolume = calculateVolumeAttenuation(x, y, volume) * audioManager.getMasterVolume() * audioManager.getSeVolume();
        
        return sound.play(finalVolume, pitch, pan);
    }
    
    public void updateSoundPosition(long soundId, float x, float y) {
        updateSoundPosition(soundId, x, y, 1.0f);
    }
    
    public void updateSoundPosition(long soundId, float x, float y, float volume) {
        for (Sound sound : audioManager.getAllSounds().values()) {
            float finalVolume = calculateVolumeAttenuation(x, y, volume) * audioManager.getMasterVolume() * audioManager.getSeVolume();
            float pan = enableSpatialAudio ? calculatePan(x) : 0f;
            sound.setVolume(soundId, finalVolume);
            sound.setPan(soundId, pan, 1.0f);
        }
    }
    
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

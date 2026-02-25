package org.ltae.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ObjectMap;

public class AudioManager {
    private static final String TAG = AudioManager.class.getSimpleName();
    
    public static final String BGM_EXT = ".bgm.mp3";
    public static final String SE_EXT = ".se.mp3";
    public static final String SE_OGG_EXT = ".se.ogg";
    public static final String SE_WAV_EXT = ".se.wav";
    public static final String BGM_OGG_EXT = ".bgm.ogg";
    public static final String UI_EXT = ".ui.mp3";
    public static final String AMBIENT_EXT = ".ambient.mp3";
    
    private static AudioManager instance;
    private ObjectMap<String, Music> musics;
    private ObjectMap<String, Sound> sounds;
    private FileHandle assetsHandle;
    private String[] assetPathList;
    
    private float masterVolume = 1.0f;
    private float bgmVolume = 1.0f;
    private float seVolume = 1.0f;
    private float uiVolume = 1.0f;
    private float ambientVolume = 1.0f;
    
    private boolean muted = false;
    
    private AudioManager() {
        musics = new ObjectMap<>();
        sounds = new ObjectMap<>();
        loadAssetList();
    }
    
    private static void initialize() {
        instance = new AudioManager();
    }
    
    public static AudioManager getInstance() {
        if (instance == null) {
            initialize();
        }
        return instance;
    }
    
    private void loadAssetList() {
        assetsHandle = Gdx.files.internal("assets.txt");
        if (!assetsHandle.exists()) {
            Gdx.app.error(TAG, "assets.txt not found!");
            return;
        }
        String assetsPath = assetsHandle.readString();
        assetPathList = assetsPath.split("\n");
    }
    
    public void loadAllAudio() {
        if (assetPathList == null) return;
        
        for (String path : assetPathList) {
            if (path.endsWith(BGM_EXT) || path.endsWith(BGM_OGG_EXT)) {
                loadMusic(path);
            } else if (path.endsWith(SE_EXT) || path.endsWith(SE_OGG_EXT) || path.endsWith(SE_WAV_EXT)) {
                loadSound(path);
            } else if (path.endsWith(UI_EXT)) {
                loadSound(path);
            } else if (path.endsWith(AMBIENT_EXT)) {
                loadMusic(path);
            }
        }
        
        Gdx.app.log(TAG, "Loaded " + musics.size + " musics and " + sounds.size + " sounds");
    }
    
    public Music loadMusic(String path) {
        if (musics.containsKey(path)) {
            return musics.get(path);
        }
        
        FileHandle file = Gdx.files.internal(path);
        if (!file.exists()) {
            Gdx.app.error(TAG, "Music file not found: " + path);
            return null;
        }
        
        Music music = Gdx.audio.newMusic(file);
        musics.put(path, music);
        return music;
    }
    
    public Sound loadSound(String path) {
        if (sounds.containsKey(path)) {
            return sounds.get(path);
        }
        
        FileHandle file = Gdx.files.internal(path);
        if (!file.exists()) {
            Gdx.app.error(TAG, "Sound file not found: " + path);
            return null;
        }
        
        Sound sound = Gdx.audio.newSound(file);
        sounds.put(path, sound);
        return sound;
    }
    
    public Music getMusic(String name) {
        return musics.get(name);
    }
    
    public Sound getSound(String name) {
        return sounds.get(name);
    }
    
    public ObjectMap<String, Music> getAllMusics() {
        return musics;
    }
    
    public ObjectMap<String, Sound> getAllSounds() {
        return sounds;
    }
    
    public void setMasterVolume(float volume) {
        this.masterVolume = Math.max(0, Math.min(1, volume));
        updateAllVolumes();
    }
    
    public void setBgmVolume(float volume) {
        this.bgmVolume = Math.max(0, Math.min(1, volume));
        updateBgmVolume();
    }
    
    public void setSeVolume(float volume) {
        this.seVolume = Math.max(0, Math.min(1, volume));
    }
    
    public void setUiVolume(float volume) {
        this.uiVolume = Math.max(0, Math.min(1, volume));
    }
    
    public void setAmbientVolume(float volume) {
        this.ambientVolume = Math.max(0, Math.min(1, volume));
    }
    
    public float getMasterVolume() {
        return masterVolume;
    }
    
    public float getBgmVolume() {
        return bgmVolume;
    }
    
    public float getSeVolume() {
        return seVolume;
    }
    
    public float getUiVolume() {
        return uiVolume;
    }
    
    public float getAmbientVolume() {
        return ambientVolume;
    }
    
    public void setMuted(boolean muted) {
        this.muted = muted;
        if (muted) {
            pauseAllMusic();
        }
    }
    
    public boolean isMuted() {
        return muted;
    }
    
    public void toggleMute() {
        setMuted(!muted);
    }
    
    private void updateAllVolumes() {
        updateBgmVolume();
    }
    
    private void updateBgmVolume() {
        for (Music music : musics.values()) {
            if (!muted) {
                music.setVolume(masterVolume * bgmVolume);
            } else {
                music.setVolume(0);
            }
        }
    }
    
    private void pauseAllMusic() {
        for (Music music : musics.values()) {
            music.pause();
        }
    }
    
    public void dispose() {
        for (Music music : musics.values()) {
            if (music != null) {
                music.dispose();
            }
        }
        musics.clear();
        
        for (Sound sound : sounds.values()) {
            if (sound != null) {
                sound.dispose();
            }
        }
        sounds.clear();
        
        instance = null;
    }
}

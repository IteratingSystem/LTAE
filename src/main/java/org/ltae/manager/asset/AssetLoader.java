package org.ltae.manager.asset;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;

/**
 * 资源加载器 - 基于libGDX AssetManager的便捷封装
 * 
 * 本类仅提供：
 * 1. 初始化配置（TiledMapLoader等自定义加载器）
 * 2. 静态便捷方法
 * 3. 进度追踪辅助
 * 
 * 实际资源管理由AssetManager负责
 */
public class AssetLoader {
    private static final String TAG = AssetLoader.class.getSimpleName();
    
    public static final String DEFAULT_SKIN_PATH = "skin/uiskin.json";
    public static final String PROPERTY_TYPES_PATH = "propertytypes.json";
    
    // ==================== 后缀常量 ====================
    
    // 地图
    public static final String TMX_EXT = ".tmx";
    
    // 纹理
    public static final String PNG_EXT = ".png";
    public static final String JPG_EXT = ".jpg";
    public static final String JPEG_EXT = ".jpeg";
    public static final String NOISE_EXT = ".noise.png";
    
    // 行为树
    public static final String TREE_EXT = ".tree";
    
    // 剧本
    public static final String STORY_EXT = ".ink.json";
    
    // 皮肤
    public static final String SKIN_EXT = ".skin.json";
    
    // 音频
    public static final String BGM_EXT = ".bgm.mp3";
    public static final String BGM_OGG_EXT = ".bgm.ogg";
    public static final String SE_EXT = ".se.mp3";
    public static final String SE_OGG_EXT = ".se.ogg";
    public static final String SE_WAV_EXT = ".se.wav";
    public static final String UI_EXT = ".ui.mp3";
    public static final String AMBIENT_EXT = ".ambient.mp3";
    
    private static AssetManager assetManager;
    private static boolean initialized = false;
    
    // 进度追踪
    private static int totalResources = 0;
    private static int loadedResources = 0;
    private static float progress = 0f;
    
    // ==================== 初始化 ====================
    
    /**
     * 初始化AssetManager（只需调用一次）
     * 内部自动注册自定义加载器
     */
    public static void initialize() {
        if (initialized) return;
        
        assetManager = new AssetManager(new InternalFileHandleResolver());
        
        // 注册自定义加载器
        assetManager.setLoader(TiledMap.class, 
            new org.ltae.loader.EcsMapLoader(
                new InternalFileHandleResolver(), 
                PROPERTY_TYPES_PATH
            ));
        
        initialized = true;
        Gdx.app.log(TAG, "AssetLoader initialized");
    }
    
    /**
     * 获取AssetManager实例
     */
    public static AssetManager getManager() {
        if (assetManager == null) {
            initialize();
        }
        return assetManager;
    }
    
    // ==================== 便捷加载方法 ====================
    
    /**
     * 同步加载Skin（用于加载界面UI）
     */
    public static Skin loadSkinSync() {
        return loadSkinSync(DEFAULT_SKIN_PATH);
    }
    
    public static Skin loadSkinSync(String skinPath) {
        initialize();
        
        assetManager.load(skinPath, Skin.class);
        assetManager.finishLoading();
        
        Skin skin = assetManager.get(skinPath);
        configureSkin(skin);
        
        return skin;
    }
    
    /**
     * 配置Skin（最近邻采样）
     */
    private static void configureSkin(Skin skin) {
        ObjectSet<Texture> textures = skin.getAtlas().getTextures();
        for (Texture texture : textures) {
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
    }
    
    /**
     * 队列所有资源
     * 读取assets.txt，根据后缀自动加载
     */
    public static void queueAssets() {
        queueAssets("assets.txt");
    }
    
    public static void queueAssets(String assetListFile) {
        initialize();
        
        if (!Gdx.files.internal(assetListFile).exists()) {
            Gdx.app.error(TAG, assetListFile + " not found!");
            return;
        }
        
        String content = Gdx.files.internal(assetListFile).readString();
        String[] paths = content.split("\n");
        
        for (String path : paths) {
            if (path.isEmpty() || path.startsWith("#")) continue;
            path = path.trim();
            
            // 跳过Skin（已同步加载）
            if (path.endsWith(SKIN_EXT)) continue;
            
            queueByExtension(path);
        }
        
        totalResources = assetManager.getQueuedAssets();
        Gdx.app.log(TAG, "Queued " + totalResources + " assets");
    }
    
    /**
     * 根据后缀队列资源
     */
    private static void queueByExtension(String path) {
        String lower = path.toLowerCase();
        
        if (lower.endsWith(TMX_EXT)) {
            assetManager.load(path, TiledMap.class);
        } else if (lower.endsWith(PNG_EXT) || lower.endsWith(JPG_EXT) || lower.endsWith(JPEG_EXT)) {
            assetManager.load(path, Texture.class);
        } else if (lower.endsWith(NOISE_EXT)) {
            assetManager.load(path, Texture.class);
        } else if (lower.endsWith(TREE_EXT)) {
            assetManager.load(path, com.badlogic.gdx.ai.btree.BehaviorTree.class);
        } else if (lower.endsWith(STORY_EXT)) {
            assetManager.load(path, com.bladecoder.ink.runtime.Story.class);
        } else if (lower.endsWith(BGM_EXT) || lower.endsWith(BGM_OGG_EXT)) {
            assetManager.load(path, com.badlogic.gdx.audio.Music.class);
        } else if (lower.endsWith(SE_EXT) || lower.endsWith(SE_OGG_EXT) || lower.endsWith(SE_WAV_EXT) || lower.endsWith(UI_EXT)) {
            assetManager.load(path, com.badlogic.gdx.audio.Sound.class);
        } else if (lower.endsWith(AMBIENT_EXT)) {
            assetManager.load(path, com.badlogic.gdx.audio.Music.class);
        } else {
            Gdx.app.debug(TAG, "Unknown resource type: " + path);
        }
    }
    
    // ==================== 进度追踪 ====================
    
    /**
     * 更新加载进度（需要在渲染循环中调用）
     * @return 是否加载完成
     */
    public static boolean update() {
        if (assetManager == null) return true;
        
        boolean complete = assetManager.update();
        
        if (complete) {
            progress = 1f;
            loadedResources = totalResources;
        } else {
            loadedResources = assetManager.getLoadedAssets();
            progress = totalResources > 0 ? (float) loadedResources / totalResources : 0f;
        }
        
        return complete;
    }
    
    /**
     * 阻塞直到所有资源加载完成
     */
    public static void finishLoading() {
        if (assetManager != null) {
            assetManager.finishLoading();
            progress = 1f;
            loadedResources = totalResources;
        }
    }
    
    public static float getProgress() {
        return progress;
    }
    
    public static int getLoadedCount() {
        return loadedResources;
    }
    
    public static int getTotalCount() {
        return totalResources;
    }
    
    public static boolean isLoading() {
        return !update();
    }
    
    // ==================== 资源获取 ====================
    
    /**
     * 获取已加载的资源
     */
    public static <T> T get(String fileName, Class<T> type) {
        return assetManager.get(fileName, type);
    }
    
    /**
     * 检查资源是否已加载
     */
    public static boolean isLoaded(String path) {
        return assetManager != null && assetManager.isLoaded(path);
    }
    
    /**
     * 卸载资源
     */
    public static void unload(String path) {
        if (assetManager != null) {
            assetManager.unload(path);
        }
    }
    
    // ==================== 便捷方法 ====================
    
    /**
     * 获取所有指定类型的已加载资源
     * @param type 资源类型
     * @param extension 文件后缀
     */
    public static <T> ObjectMap<String, T> getAll(Class<T> type, String extension) {
        ObjectMap<String, T> result = new ObjectMap<>();
        
        if (!Gdx.files.internal("assets.txt").exists()) {
            return result;
        }
        
        String content = Gdx.files.internal("assets.txt").readString();
        String[] paths = content.split("\n");
        
        for (String path : paths) {
            if (path.isEmpty() || !path.endsWith(extension)) continue;
            
            if (assetManager.isLoaded(path)) {
                T obj = assetManager.get(path, type);
                String name = Gdx.files.internal(path).nameWithoutExtension();
                result.put(name, obj);
            }
        }
        
        return result;
    }
    
    /**
     * 获取所有已加载的Music
     */
    public static ObjectMap<String, com.badlogic.gdx.audio.Music> getAllMusic() {
        ObjectMap<String, com.badlogic.gdx.audio.Music> result = new ObjectMap<>();
        
        if (!Gdx.files.internal("assets.txt").exists()) return result;
        
        String content = Gdx.files.internal("assets.txt").readString();
        String[] paths = content.split("\n");
        
        for (String path : paths) {
            String lower = path.toLowerCase();
            if (lower.isEmpty()) continue;
            if (!lower.endsWith(BGM_EXT) && !lower.endsWith(BGM_OGG_EXT) 
                && !lower.endsWith(AMBIENT_EXT)) continue;
            
            if (assetManager.isLoaded(path)) {
                com.badlogic.gdx.audio.Music music = assetManager.get(path, com.badlogic.gdx.audio.Music.class);
                String name = Gdx.files.internal(path).nameWithoutExtension();
                result.put(name, music);
            }
        }
        
        return result;
    }
    
    /**
     * 获取所有已加载的Sound
     */
    public static ObjectMap<String, com.badlogic.gdx.audio.Sound> getAllSounds() {
        ObjectMap<String, com.badlogic.gdx.audio.Sound> result = new ObjectMap<>();
        
        if (!Gdx.files.internal("assets.txt").exists()) return result;
        
        String content = Gdx.files.internal("assets.txt").readString();
        String[] paths = content.split("\n");
        
        for (String path : paths) {
            String lower = path.toLowerCase();
            if (lower.isEmpty()) continue;
            if (!lower.endsWith(SE_EXT) && !lower.endsWith(SE_OGG_EXT) 
                && !lower.endsWith(SE_WAV_EXT) && !lower.endsWith(UI_EXT)) continue;
            
            if (assetManager.isLoaded(path)) {
                com.badlogic.gdx.audio.Sound sound = assetManager.get(path, com.badlogic.gdx.audio.Sound.class);
                String name = Gdx.files.internal(path).nameWithoutExtension();
                result.put(name, sound);
            }
        }
        
        return result;
    }
    
    /**
     * 释放所有资源
     */
    public static void dispose() {
        if (assetManager != null) {
            assetManager.dispose();
            assetManager = null;
            initialized = false;
        }
    }
}

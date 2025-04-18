package org.ltae.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeLoader;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.AbsoluteFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.ObjectMap;
import org.ltae.tiled.loader.DefMapLoader;


/**
 * @Auther WenLong
 * @Date 2025/2/11 16:23
 * @Description
 **/
public class AssetManager {
    private static final String TAG = AssetManager.class.getSimpleName();
    private static AssetManager instance;
    private static FileHandleResolver resolver;
    private static com.badlogic.gdx.assets.AssetManager gdxAssetManager;

    private AssetManager() {
        // 私有构造器，防止外部直接实例化
    }

    /**
     * 获取单例实例
     * @return AssetManager 实例
     */
    public static AssetManager getInstance() {
        if (instance == null) {
            resolver = new AbsoluteFileHandleResolver();
            instance = new AssetManager();
        }
        return instance;
    }


    public void setLoaders(String propTypePath) {
        gdxAssetManager = new com.badlogic.gdx.assets.AssetManager(resolver);
        gdxAssetManager.setLoader(TiledMap.class, new DefMapLoader(propTypePath));
        gdxAssetManager.setLoader(BehaviorTree.class, new BehaviorTreeLoader(resolver));
    }

    public com.badlogic.gdx.assets.AssetManager getGdxAssetManager(){
        return gdxAssetManager;
    }

    /**
     * 加载指定路径下所有指定后缀的文件为指定的类
     * @param path 路径
     * @param suffix 文件后缀
     * @param aClass 类型
     */
    public <T> void loadAssets(String path, String suffix, Class<T> aClass) {
        FileHandle fileHandle = Gdx.files.internal(path);
        FileHandle[] fileHandles = fileHandle.list(suffix);
        for (FileHandle handle : fileHandles) {
            String completePath = handle.path();
            gdxAssetManager.load(completePath, aClass);
        }
    }

    /**
     * 将路径下的文件加载为指定类型的对象，文件名作为键（不包含后缀）
     * @param path 路径
     * @param suffix 文件后缀
     * @param aClass 类型
     * @return 加载的对象映射
     */
    public <T> ObjectMap<String, T> getData(String path, String suffix, Class<T> aClass) {
        ObjectMap<String, T> objectMap = new ObjectMap<>();
        FileHandle fileHandle = Gdx.files.internal(path);
        FileHandle[] fileHandles = fileHandle.list(suffix);
        for (FileHandle file : fileHandles) {
            String fileName = file.nameWithoutExtension();
            String completePath = file.path();
            T asset = gdxAssetManager.get(completePath, aClass);
            objectMap.put(fileName, asset);
        }
        return objectMap;
    }

    public void update(){
        gdxAssetManager.update();
    }
    public float getProgress(){
        return gdxAssetManager.getProgress();
    }
    /**
     * 释放资源
     */
    public void dispose() {

        if (gdxAssetManager != null) {
            gdxAssetManager.dispose();
        }
    }
}

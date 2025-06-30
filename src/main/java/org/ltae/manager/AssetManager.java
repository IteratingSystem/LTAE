package org.ltae.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeLoader;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectMap;
import com.bladecoder.ink.runtime.Story;
import org.ltae.loader.EcsMapLoader;
import org.ltae.loader.InkStoryLoader;


/**
 * @Auther WenLong
 * @Date 2025/2/11 16:23
 * @Description
 **/
public class AssetManager {
    private static final String TAG = AssetManager.class.getSimpleName();
    public static final String TILED_EXT = ".tmx";
    public static final String TREE_EXT = ".tree";
    public static final String STORY_EXT = ".ink.json";
    private static AssetManager instance;
    private static FileHandleResolver resolver;
    private static FileHandle assetsHandle;
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
            assetsHandle = Gdx.files.internal("assets.txt");
            resolver = new InternalFileHandleResolver();
            instance = new AssetManager();
            gdxAssetManager = new com.badlogic.gdx.assets.AssetManager(resolver);
        }
        return instance;
    }


    public void setLoaders(String propTypePath) {
        gdxAssetManager.setLoader(TiledMap.class, new EcsMapLoader(resolver,propTypePath));
        gdxAssetManager.setLoader(BehaviorTree.class, new BehaviorTreeLoader(resolver));
        gdxAssetManager.setLoader(Story.class, new InkStoryLoader(resolver));
    }
    public <T, P extends AssetLoaderParameters<T>> void setLoader(Class<T> type, AssetLoader<T, P> loader) {
        gdxAssetManager.setLoader(type,loader);
    }

    public com.badlogic.gdx.assets.AssetManager getGdxAssetManager(){
        return gdxAssetManager;
    }

    /**
     * 加载载指定文件到类型
     * @param path
     * @param aClass
     * @param <T>
     */
    public <T> void loadAsset(String path, Class<T> aClass) {
        gdxAssetManager.load(path, aClass);
    }


    /**
     * 自动加载默认资源,目前包含地图与行为树文件,逻辑是靠识别特定的文件后缀,因此没有特别后缀的文件需要手动加载
     * 依赖于assets模块中的assets.txt
     */
    public void loadAssets() {
        if (!assetsHandle.exists()) {
            Gdx.app.error(TAG,"assets.txt is not exists!");
            return;
        }
        String assetsPath = assetsHandle.readString();
        String[] assetPath = assetsPath.split("\n");
        for (String path : assetPath) {
            if (path.endsWith(TILED_EXT)) {
                loadAsset(path,TiledMap.class);
                continue;
            }
            if (path.endsWith(TREE_EXT)) {
                loadAsset(path,BehaviorTree.class);
                continue;
            }
            if (path.endsWith(STORY_EXT)) {
                loadAsset(path,Story.class);
                continue;
            }
        }
    }


    /**
     * gdxAssetManager.get的套娃,用于获取已经加载过的资源得到对象
     * @param path
     * @param aClass
     * @return
     * @param <T>
     */
    public <T> T getObejct(String path,Class<T> aClass) {
        return gdxAssetManager.get(path, aClass);
    }

    /**
     * 传入类型,获取已加载的所有此类型的对象,最后返回为ObjectMap<name, object>,其中name是文件名,非路径也无后缀
     * @param aClass
     * @return
     * @param <T>
     */
    public <T> ObjectMap<String, T> getObjects(String ext,Class<T> aClass) {
        if (!assetsHandle.exists()) {
            Gdx.app.error(TAG,"assets.txt is not exists!");
            return null;
        }
        String assetsPath = assetsHandle.readString();
        String[] assetPath = assetsPath.split("\n");
        ObjectMap<String, T> objectMap = new ObjectMap<>();
        FileHandle fileHandle;
        for (String path : assetPath) {
            if (path.endsWith(ext)) {
                fileHandle = Gdx.files.internal(path);
                if (!fileHandle.exists()){
                    Gdx.app.log(TAG,"getObjects continue: fileHandle is exists,path: "+path);
                    continue;
                }
                T obejct = getObejct(path, aClass);
                String name = fileHandle.name().replace(ext,"");
                objectMap.put(name,obejct);
            }
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

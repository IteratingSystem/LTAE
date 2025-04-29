package org.ltae.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeLoader;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.AbsoluteFileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.ClasspathFileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.sun.tools.javac.Main;
import org.ltae.tiled.loader.DefMapLoader;

import java.util.Objects;


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
//            //以jar包运行时
//            if(AssetManager.class.getResource("").getProtocol().equals("jar")){
//                resolver = new ClasspathFileHandleResolver();
//            }else {
//                resolver = new AbsoluteFileHandleResolver();
//            }
//            resolver = new ClasspathFileHandleResolver();
//            resolver = new AbsoluteFileHandleResolver();
            resolver = new InternalFileHandleResolver();
            instance = new AssetManager();
            gdxAssetManager = new com.badlogic.gdx.assets.AssetManager(resolver);
        }
        return instance;
    }


    public void setLoaders(String propTypePath) {
        gdxAssetManager.setLoader(TiledMap.class, new DefMapLoader(propTypePath));
        gdxAssetManager.setLoader(BehaviorTree.class, new BehaviorTreeLoader(resolver));
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
     * 加载指定路径下所有指定后缀的文件为指定的类
     * @param path 路径,不允许传入带空格的路径
     * @param suffix 文件后缀
     * @param aClass 类型
     */
    public <T> void loadAssets(String path, String suffix, Class<T> aClass) {
        FileHandle fileHandle = Gdx.files.classpath(path);
        FileHandle[] fileHandles = fileHandle.list(suffix);
        if (fileHandles.length == 0){
            Gdx.app.error(TAG,"loadAssets fileHandle: "+path+",fileHandle list length: "+fileHandles.length);
        }
        for (FileHandle handle : fileHandles) {
            String completePath = handle.path();
            Gdx.app.log(TAG,"loadAssets filehandles list: "+completePath);
            loadAsset(completePath, aClass);
        }
    }

    /**
     * 将路径下的文件加载为指定类型的对象，文件名作为键（不包含后缀）
     * @param path 路径
     * @param suffix 文件后缀
     * @param aClass 类型
     * @return 加载的对象映射
     */
    public <T> ObjectMap<String, T> getDatas(String path, String suffix, Class<T> aClass) {

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
    public <T> T getData(String path,Class<T> aClass) {
        FileHandle fileHandle = Gdx.files.internal(path);
        if (!fileHandle.exists()) {
            Gdx.app.error(TAG,"Failed to getData,fileHandle not is exists!Path: "+path);
            return null;
        }
        T asset = gdxAssetManager.get(path, aClass);
        return asset;
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

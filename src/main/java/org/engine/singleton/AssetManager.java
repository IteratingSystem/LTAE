package org.engine.singleton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.AbsoluteFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.ObjectMap;
import org.engine.tiled.loader.DefMapLoader;


/**
 * @Auther WenLong
 * @Date 2025/2/11 16:23
 * @Description
 **/
public class AssetManager {
    private static final String TAG = AssetManager.class.getSimpleName();
    private static com.badlogic.gdx.assets.AssetManager instance;

    private AssetManager() {}

    /**
     * 此单例类在使用前需要先初始化
     *
     * @param propTypePath
     */
    public static void initialize(String propTypePath){
        FileHandleResolver resolver = new AbsoluteFileHandleResolver();
        instance = new com.badlogic.gdx.assets.AssetManager(resolver);
        instance.setLoader(TiledMap.class, new DefMapLoader(propTypePath));
    }
    public static com.badlogic.gdx.assets.AssetManager getInstance() {
        if (instance == null) {
            Gdx.app.log(TAG,"Not initialized, please execute the method:initialize(String propTypePath)");
            return null;
        }
        return instance;
    }

    //将文件夹下所有传入后缀名的文件加载为指定的类
    public static <T> void loadAssets(String path,String suffix,Class<T> aClass){
        FileHandle fileHandle = Gdx.files.internal(path);
        FileHandle[] fileHandles = fileHandle.list(suffix);
        for (FileHandle handle : fileHandles) {
            String completePath = handle.path();
            getInstance().load(completePath,aClass);
        }
    }
    //将路径载入为对象,文件名就是key(不包含后缀)
    public static <T> ObjectMap<String, T> getData(String path, String suffix, Class<T> aClass){
        ObjectMap<String,T> objectMap = new ObjectMap<>();

        FileHandle fileHandle = Gdx.files.internal(path);
        FileHandle[] fileHandles = fileHandle.list(suffix);
        for (FileHandle file : fileHandles) {
            String fileName = file.name();
            String name = fileName.replace("."+suffix,"");
            String completePath = file.path();
            objectMap.put(name,getInstance().get(completePath,aClass));
        }
        return objectMap;
    }

    public static void dispose(){
        getInstance().dispose();
    }
}

package org.ltae.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 * @Auther WenLong
 * @Date 2025/4/28 11:20
 * @Description shader着色器管理器
 **/
public class ShaderManager {
    private final static String TAG = ShaderManager.class.getSimpleName();
    private static ShaderManager instance;
    public static final String VERTEX_EXT = ".vert";
    public static final String FRAGMENT_EXT = ".frag";
    public String[] assetPathList;

    private FileHandle assetsHandle;
    private ShaderManager(){
        assetPathList = AssetManager.getInstance().assetPathList;
    }
    private static void initialize(){
        instance = new ShaderManager();
    }
    public static ShaderManager getInstance(){
        if (instance == null){
            initialize();
        }
        return instance;
    }
    public String getVertexContext(String name) {
        String vertexFileName = name + VERTEX_EXT;
        for (String assetPath : assetPathList) {
            if (!assetPath.endsWith(vertexFileName)) {
                continue;
            }
            assetsHandle = Gdx.files.internal(assetPath);
            return assetsHandle.readString();
        }
        Gdx.app.error(TAG,"Failed to getVertexContext,No shader with this name was found: "+name);
        return null;
    }
    public String getFragmentContext(String name) {
        String fragmentFileName = name + FRAGMENT_EXT;
        for (String assetPath : assetPathList) {
            if (!assetPath.endsWith(fragmentFileName)) {
                continue;
            }
            assetsHandle = Gdx.files.internal(assetPath);
            return assetsHandle.readString();
        }
        Gdx.app.error(TAG,"Failed to getFragmentContext,No shader with this name was found: "+name);
        return null;
    }

}

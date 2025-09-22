package org.ltae.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectSet;

/**
 * @Auther WenLong
 * @Date 2025/4/28 11:20
 * @Description skin管理器,用于控制skin的单例,单独拎出来是因为在资源加载页面需要使用到
 **/
public class ShaderManage {
    private final static String TAG = ShaderManage.class.getSimpleName();
    private static ShaderManage instance;
    public static final String VERTEX_EXT = ".vertex";
    public static final String FRAGMENT_EXT = ".fragment";
    public String[] assetPathList;

    private FileHandle assetsHandle;
    private ShaderManage(){
        assetPathList = AssetManager.getInstance().assetPathList;
    }
    private static void initialize(){
        instance = new ShaderManage();
    }
    public static ShaderManage getInstance(){
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

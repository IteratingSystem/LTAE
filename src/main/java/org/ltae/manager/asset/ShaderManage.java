package org.ltae.manager.asset;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 * @Auther WenLong
 * @Date 2025/4/28 11:20
 * @Description 着色器管理器
 **/
public class ShaderManage {
    private final static String TAG = ShaderManage.class.getSimpleName();
    private static ShaderManage instance;
    public static final String VERTEX_EXT = ".vert";
    public static final String FRAGMENT_EXT = ".frag";

    private String[] assetPathList;

    private ShaderManage(){
        String assetsTxt = "assets.txt";
        if (Gdx.files.internal(assetsTxt).exists()) {
            String content = Gdx.files.internal(assetsTxt).readString();
            assetPathList = content.split("\n");
        }
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
        return findShaderContent(vertexFileName);
    }
    public String getFragmentContext(String name) {
        String fragmentFileName = name + FRAGMENT_EXT;
        return findShaderContent(fragmentFileName);
    }

    private String findShaderContent(String filename) {
        if (assetPathList == null) {
            FileHandle handle = Gdx.files.internal(filename);
            if (handle.exists()) {
                return handle.readString();
            }
            Gdx.app.error(TAG, "Shader file not found: " + filename);
            return null;
        }

        for (String assetPath : assetPathList) {
            if (!assetPath.endsWith(filename)) {
                continue;
            }
            FileHandle handle = Gdx.files.internal(assetPath);
            return handle.readString();
        }
        Gdx.app.error(TAG, "Shader not found: " + filename);
        return null;
    }
}

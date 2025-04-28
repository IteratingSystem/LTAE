package org.ltae.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectSet;

/**
 * @Auther WenLong
 * @Date 2025/4/28 11:20
 * @Description skin管理器,用于控制skin的单例
 **/
public class SkinManage {
    private final static String TAG = SkinManage.class.getSimpleName();
    private static Skin instance;
    private SkinManage(){}

    public static Skin getInstance() {
        if (instance == null){
            Gdx.app.error(TAG,"Instance is null,please use SkinManage.initialize(String skinPath)");
            return null;
        }
        return instance;
    }
    public static Skin initialize(String skinPath){
        AssetManager assetManager = AssetManager.getInstance();
        assetManager.loadAsset(skinPath,Skin.class);
        assetManager.getGdxAssetManager().finishLoading();
        instance = assetManager.getData(skinPath,Skin.class);
        //将皮肤中的内容改为临近采样
        ObjectSet<Texture> textures = instance.getAtlas().getTextures();
        for (Texture texture : textures) {
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
        return instance;
    }
}

package org.ltae.manager;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectSet;

/**
 * @Auther WenLong
 * @Date 2025/4/28 11:20
 * @Description skin管理器,用于控制skin的单例,单独拎出来是因为在资源加载页面需要使用到
 **/
public class SkinManage {
    private final static String TAG = SkinManage.class.getSimpleName();
    private static Skin skin;
    private SkinManage(){}

    public static Skin getSkin(String skinPath) {
        if (skin == null){
            initialize(skinPath);
        }
        return skin;
    }
    private static void initialize(String skinPath){
        AssetManager assetManager = AssetManager.getInstance();
        assetManager.loadAsset(skinPath,Skin.class);
        assetManager.getGdxAssetManager().finishLoading();
        skin = assetManager.getObejct(skinPath,Skin.class);
        //将皮肤中的内容改为临近采样
        ObjectSet<Texture> textures = skin.getAtlas().getTextures();
        for (Texture texture : textures) {
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
    }
}

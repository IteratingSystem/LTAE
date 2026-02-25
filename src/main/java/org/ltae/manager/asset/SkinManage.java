package org.ltae.manager.asset;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Skin管理器
 * 单独拎出来是因为在资源加载页面需要使用
 */
public class SkinManage {
    private final static String TAG = SkinManage.class.getSimpleName();
    private static Skin skin;
    private SkinManage(){}

    public static Skin getSkin(String skinPath) {
        if (skin == null){
            skin = AssetLoader.loadSkinSync(skinPath);
        }
        return skin;
    }
}

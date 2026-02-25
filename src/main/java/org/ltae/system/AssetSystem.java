package org.ltae.system;

import com.artemis.BaseSystem;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectMap;
import org.ltae.manager.asset.AssetLoader;
import org.ltae.manager.asset.SkinManage;

/**
 * 资源系统 - 加载和管理游戏资源
 * 已整合到LtaePlugin中，自动初始化
 */
public class AssetSystem extends BaseSystem {
    private final static String TAG = AssetSystem.class.getSimpleName();
    private String skinPath;
    public ObjectMap<String, BehaviorTree> bTreeData;
    public ObjectMap<String, Texture> noiseData;
    public Skin skin;
    
    public AssetSystem(String skinPath) {
        this.skinPath = skinPath;
    }

    @Override
    protected void initialize() {
        skin = SkinManage.getSkin(skinPath);
        bTreeData = AssetLoader.getAll(BehaviorTree.class, ".tree");
        noiseData = AssetLoader.getAll(Texture.class, ".noise.png");
    }

    @Override
    protected void processSystem() {
    }
}

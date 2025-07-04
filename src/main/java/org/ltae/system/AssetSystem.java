package org.ltae.system;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.bladecoder.ink.runtime.Story;
import org.ltae.manager.AssetManager;
import org.ltae.manager.SkinManage;

import javax.xml.crypto.dsig.spec.XPathType;

/**
 * @Author: WenLong
 * @Date: 2024-09-09-16:36
 * @Description: 资源管理系统,将各种资源载入为对象
 */
public class AssetSystem extends BaseSystem {
    private final static String TAG = AssetSystem.class.getSimpleName();
    private String skinPath;
    //行为树数据
    public ObjectMap<String, BehaviorTree> bTreeData;
    public Skin skin;
    public AssetSystem (String skinPath){
        this.skinPath = skinPath;
    }

    @Override
    protected void initialize() {
        //加载skin
        skin = SkinManage.getSkin(skinPath);
        //行为树
        bTreeData = AssetManager.getInstance().getObjects(AssetManager.TREE_EXT,BehaviorTree.class);
    }

    @Override
    protected void processSystem() {

    }
}

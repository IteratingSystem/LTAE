package org.ltae.system;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectMap;
import org.ltae.manager.AssetManager;

/**
 * @Author: WenLong
 * @Date: 2024-09-09-16:36
 * @Description: 资源管理系统,将各种资源载入为对象
 */
public class AssetSystem extends BaseSystem {
    private final static String TAG = AssetSystem.class.getSimpleName();
    private String tiledMapPath;
    private String bTreePath;
    private String skinPath;
    //地图数据
    public ObjectMap<String,TiledMap> tiledData;
    //行为树数据
    public ObjectMap<String, BehaviorTree> bTreeData;
    public Skin skin;
    public AssetSystem (String tiledMapPath, String bTreePath,String skinPath){
        this.tiledMapPath = tiledMapPath;
        this.bTreePath = bTreePath;
        this.skinPath = skinPath;
    }

    @Override
    protected void initialize() {
        skin = AssetManager.getInstance().getData(skinPath,Skin.class);
        tiledData = AssetManager.getInstance().getData(tiledMapPath, "tmx", TiledMap.class);
        bTreeData = AssetManager.getInstance().getData(bTreePath,"tree",BehaviorTree.class);
        if (tiledData.isEmpty()) {
            Gdx.app.log(TAG,"tiledData is empty,Please load the resources first!");
        }else {
            Gdx.app.log(TAG,"Loading resource object completed!");
        }

    }

    @Override
    protected void processSystem() {

    }
}

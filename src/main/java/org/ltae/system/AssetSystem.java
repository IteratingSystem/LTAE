package org.ltae.system;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.ObjectMap;
import org.ltae.singleton.AssetManager;

/**
 * @Author: WenLong
 * @Date: 2024-09-09-16:36
 * @Description: 资源管理系统,将各种资源载入为对象
 */
public class AssetSystem extends BaseSystem {
    private final static String TAG = AssetSystem.class.getSimpleName();
    private final String tiledMapPath;
    //地图数据
    public ObjectMap<String,TiledMap> tiledData;
    public AssetSystem (String tiledMapPath){
        this.tiledMapPath = tiledMapPath;
    }

    @Override
    protected void initialize() {
        tiledData = AssetManager.getData(tiledMapPath, "tmx", TiledMap.class);
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

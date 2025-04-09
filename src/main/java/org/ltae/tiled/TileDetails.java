package org.ltae.tiled;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import org.ltae.system.AssetSystem;


/**
 * @Auther WenLong
 * @Date 2025/2/17 14:30
 * @Description 在将地图中对象创建为实体的过程中, 用于传参的对象
 **/
public class TileDetails {
    public int entityId;
    public Entity entity;
    public float worldScale;
    public World world;

    public AssetSystem assetSystem;

    public com.badlogic.gdx.physics.box2d.World b2dWorld;

    public TiledMap tiledMap;
    public MapObject mapObject;
    public TiledMapTile tiledMapTile;

    public String statePackage;
    public String contactListenerPackage;
}

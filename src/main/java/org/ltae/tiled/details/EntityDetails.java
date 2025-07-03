package org.ltae.tiled.details;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import org.ltae.system.AssetSystem;

import java.util.Set;


/**
 * @Auther WenLong
 * @Date 2025/2/17 14:30
 * @Description 在将地图中对象创建为实体的过程中, 用于传参的对象
 **/
public class EntityDetails {
    public int entityId;
    public MapObject mapObject;
    public TiledMapTile tiledMapTile;
}

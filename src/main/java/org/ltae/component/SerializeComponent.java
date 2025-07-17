package org.ltae.component;


import com.artemis.Component;
import com.artemis.World;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import org.ltae.LtaePluginRule;
import org.ltae.manager.map.MapManager;
import org.ltae.manager.map.serialize.json.EntityData;
import org.ltae.system.TiledMapSystem;

//组件继承于它可以自定义加载逻辑
public abstract class SerializeComponent extends Component {
    public World world;
    public int entityId;
    public MapObject mapObject;
    public TiledMapTile tiledMapTile;
    public void reload(World world, EntityData entityData){
        this.world = world;
        entityId = entityData.entityId;
        int mapObjectId = entityData.mapObjectId;
        TiledMapSystem tiledMapSystem = world.getSystem(TiledMapSystem.class);
        String current = tiledMapSystem.getCurrent();

        MapManager mapManager = MapManager.getInstance();
        mapObject = mapManager.getMapObject(entityData.fromMap,mapObjectId);

        if (mapObject instanceof TiledMapTileMapObject tileMapObject) {
            tiledMapTile = tileMapObject.getTile();
        }
    }
    public String getTag(){
        return getClass().getSimpleName();
    }
}

package org.ltae.component;


import com.artemis.Component;
import com.artemis.World;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import org.ltae.LtaePluginRule;
import org.ltae.manager.map.MapManager;
import org.ltae.manager.map.serialize.json.EntityJson;

//组件继承于它可以自定义加载逻辑
public abstract class SerializeComponent extends Component {
    public World world;
    public int entityId;
    public MapObject mapObject;
    public TiledMapTile tiledMapTile;
    public void reload(World world, EntityJson entityJson){
        this.world = world;
        entityId = entityJson.entityId;
        int mapObjectId = entityJson.mapObjectId;
        MapManager mapManager = MapManager.getInstance();
        MapObjects mapObjects = mapManager.getMapObjects(LtaePluginRule.MAP_NAME);
        for (MapObject mapObject_ : mapObjects) {
            Integer id = mapObject_.getProperties().get("id", -1, Integer.class);
            if (mapObjectId == id){
                mapObject = mapObject_;
            }
        }
        if (mapObject instanceof TiledMapTileMapObject tileMapObject) {
            tiledMapTile = tileMapObject.getTile();
        }
    }
}

package org.ltae.component;


import com.artemis.Component;
import com.artemis.World;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import net.mostlyoriginal.api.event.common.EventSystem;
import org.ltae.manager.map.MapManager;
import org.ltae.manager.map.serialize.json.EntityData;

//组件继承于它可以自定义加载逻辑
public abstract class SerializeComponent extends Component {
    public World world;
    public EventSystem eventSystem;
    public int entityId;
    public MapObject mapObject;
    public TiledMapTile tiledMapTile;
    public String fromMap;
    public void reload(World world, EntityData entityData){
        this.world = world;
        eventSystem = world.getSystem(EventSystem.class);
        entityId = entityData.entityId;
        int mapObjectId = entityData.mapObjectId;

        MapManager mapManager = MapManager.getInstance();
        fromMap = entityData.fromMap;
        mapObject = mapManager.getMapObject(fromMap,mapObjectId);

        if (mapObject instanceof TiledMapTileMapObject tileMapObject) {
            tiledMapTile = tileMapObject.getTile();
        }
    }
    public String getTag(){
        return getClass().getSimpleName();
    }
}

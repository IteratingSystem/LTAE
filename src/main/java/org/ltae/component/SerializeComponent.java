package org.ltae.component;


import com.artemis.Component;
import com.artemis.World;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import org.ltae.serialize.json.EntityJson;

//组件继承于它可以自定义加载逻辑
public abstract class SerializeComponent extends Component {
    public World world;
    public int entityId;
    public MapObject mapObject;
    public TiledMapTile tiledMapTile;
    public void reload(World world,EntityJson entityJson){
        this.world = world;
        entityId = entityJson.entityId;
        mapObject = entityJson.mapObject;
        if (mapObject instanceof TiledMapTileMapObject tileMapObject) {
            tiledMapTile = tileMapObject.getTile();
        }
    }
}

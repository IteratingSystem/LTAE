package org.ltae.component;

import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import org.ltae.system.AssetSystem;
import org.ltae.tiled.TileCompLoader;
import org.ltae.tiled.TileDetails;
import org.ltae.tiled.TileParam;
import org.ltae.tiled.TiledToECS;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * @Auther WenLong
 * @Date 2025/5/12 11:15
 * @Description 预制件实体,其主要用处是挂载了此组件的实体不会自动创建,提供手动创建的方法,比如子弹之类的实体
 **/
public class Prefabricated extends Component implements TileCompLoader {
    private final static String TAG = Prefabricated.class.getSimpleName();
    private TileDetails tileDetails;
    @Override
    public void loader(TileDetails tileDetails) {
        this.tileDetails = tileDetails;
    }

    public Entity createEntity(){
        return TiledToECS.createEntity(tileDetails.mapObject, TiledToECS.CreateMode.PREFABRICATED);
    }
}

package org.ltae.serialize;

import com.artemis.Component;
import com.artemis.World;
import com.artemis.utils.Bag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import org.ltae.manager.TiledMapManager;
import org.ltae.serialize.json.EntitiesJson;

/**
 * @Auther WenLong
 * @Date 2025/5/13 10:14
 * @Description
 **/
public class EntityBuilder {
    private final static String TAG = EntityBuilder.class.getSimpleName();

    public static void createAll(World world, String mapName, String entityLayer, String[] compPackages, Bag<Class<? extends Component>> autoComps){
        TiledMap tiledMap = TiledMapManager.getTiledMap(mapName);
        if (tiledMap == null){
            Gdx.app.error(TAG,"tiledMap is null!name:"+mapName);
            return;
        }
        EntitiesJson entitiesJson = SerializeUtils.getEntitiesJson(tiledMap, entityLayer, compPackages,autoComps);
        SerializeUtils.createEntities(world,entitiesJson,compPackages);
    }
}

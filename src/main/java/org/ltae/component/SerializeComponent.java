package org.ltae.component;

import com.artemis.ArchetypeBuilder;
import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.Bag;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import net.mostlyoriginal.api.event.common.EventSystem;
import org.ltae.LtaePluginRule;
import org.ltae.system.TiledMapManager;
import org.ltae.tiled.ComponentLoader;
import org.ltae.tiled.EntityBuilder;
import org.ltae.tiled.details.EntityDetails;
import org.ltae.tiled.details.SystemDetails;
import org.ltae.utils.serialize.json.ComponentJson;
import org.ltae.utils.serialize.json.EntityJson;
import org.reflections.Reflections;

import java.util.HashSet;
import java.util.Set;

/**
 * @Auther WenLong
 * @Date 2025/7/4 11:46
 * @Description
 **/
public class SerializeComponent extends Component {
    public void reset(World world, EntityJson entityJson){

        if (this instanceof ComponentLoader componentLoader){
            SystemDetails systemDetails = new SystemDetails();
            systemDetails.world = world;
            systemDetails.eventSystem = world.getSystem(EventSystem.class);
            TiledMapManager tiledMapManager = world.getSystem(TiledMapManager.class);
            systemDetails.tiledMap = tiledMapManager.currentMap;

            //systemDetails还有部分赋值会在System.initialize()中,因为其依赖System像个属性
            systemDetails = new SystemDetails();
            systemDetails.worldScale = LtaePluginRule.WORLD_SCALE;
            systemDetails.entityLayer = LtaePluginRule.ENTITY_LAYER;
            systemDetails.statePkg = LtaePluginRule.STATE_PKG;
            systemDetails.b2dListenerPkg = LtaePluginRule.B2D_LISTENER_PKG;
            systemDetails.componentPkg = LtaePluginRule.COMPONENT_PKG;
            systemDetails.onEventPkg = LtaePluginRule.ON_EVENT_PKG;
            Bag<Class<? extends Component>> autoCompClasses = new Bag<>();
            autoCompClasses.add(Pos.class);
            autoCompClasses.add(Render.class);
            autoCompClasses.add(ZIndex.class);
            systemDetails.autoCompClasses = autoCompClasses;



            EntityDetails entityDetails = new EntityDetails();
            entityDetails.entityId = entityJson.entityId;
            entityDetails.mapObject = entityJson.mapObject;
            if (entityJson.mapObject instanceof TiledMapTileMapObject tileMapObject) {
                entityDetails.tiledMapTile = tileMapObject.getTile();
            }

            componentLoader.loader(systemDetails,entityDetails);
        }
    }
}

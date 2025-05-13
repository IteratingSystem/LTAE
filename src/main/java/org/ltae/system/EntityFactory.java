package org.ltae.system;

import com.artemis.BaseSystem;
import com.artemis.Component;
import com.artemis.World;
import com.artemis.utils.Bag;
import com.badlogic.gdx.maps.tiled.TiledMap;
import org.ltae.component.Pos;
import org.ltae.component.Render;
import org.ltae.component.ZIndex;
import org.ltae.tiled.EntityBuilder;
import org.ltae.tiled.details.SystemDetails;

/**
 * @Auther WenLong
 * @Date 2025/2/12 16:29
 * @Description
 **/
public class EntityFactory extends BaseSystem {
    private TiledMapManager tiledMapManager;
    public SystemDetails systemDetails;
    public EntityBuilder entityBuilder;

    public EntityFactory(String componentPkg, String statePkg, String b2dListenerPkg, String entityLayer, float worldScale){
        Bag<Class<? extends Component>> autoCompClasses = new Bag<>();
        autoCompClasses.add(Pos.class);
        autoCompClasses.add(Render.class);
        autoCompClasses.add(ZIndex.class);

        systemDetails = new SystemDetails();
        systemDetails.worldScale = worldScale;
        systemDetails.world = world;

        systemDetails.entityLayer = entityLayer;
        systemDetails.statePkg = statePkg;
        systemDetails.b2dListenerPkg = b2dListenerPkg;
        systemDetails.componentPkg = componentPkg;
        systemDetails.autoCompClasses = autoCompClasses;

    }
    @Override
    protected void initialize() {
        systemDetails.tiledMap = tiledMapManager.currentMap;
        entityBuilder = new EntityBuilder(systemDetails);
        entityBuilder.createAllEntity();
    }

    @Override
    protected void processSystem() {

    }
}

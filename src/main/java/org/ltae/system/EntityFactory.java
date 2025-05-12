package org.ltae.system;

import com.artemis.BaseSystem;
import org.ltae.component.Pos;
import org.ltae.component.Render;
import org.ltae.component.ZIndex;
import org.ltae.tiled.TiledToECS;

/**
 * @Auther WenLong
 * @Date 2025/2/12 16:29
 * @Description
 **/
public class EntityFactory extends BaseSystem {


    private float worldScale;
    private String compPackage;
    private String statePackage;
    private String contactListenerPackage;
    private String entityLayerName;
    private B2dSystem b2DSystem;
    private TiledMapManager tiledMapManager;

    public EntityFactory(String compPackage, String statePackage, String contactListenerPackage, String entityLayerName, float worldScale){
        this.worldScale = worldScale;
        this.compPackage = compPackage;
        this.statePackage = statePackage;
        this.entityLayerName = entityLayerName;
        this.contactListenerPackage = contactListenerPackage;
    }
    @Override
    protected void initialize() {
        TiledToECS
            .builder()
            .scale(worldScale)
            .world(world)
            .box2DWorld(b2DSystem.box2DWorld)
            .tiledMap(tiledMapManager.currentMap)
            .entityLayerName(entityLayerName)
            .addAutoInitComp(Pos.class)
            .addAutoInitComp(ZIndex.class)
            .addAutoInitComp(Render.class)
            .compPackage(compPackage)
            .statePackage(statePackage)
            .contactListenerPackage(contactListenerPackage)
            .build()
            .parse();
    }

    @Override
    protected void processSystem() {

    }
}

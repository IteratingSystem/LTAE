package org.ltae.system;

import com.artemis.BaseSystem;
import org.ltae.component.Pos;
import org.ltae.component.Render;
import org.ltae.component.ZIndex;
import org.ltae.tiled.TiledObjectToArtemis;

/**
 * @Auther WenLong
 * @Date 2025/2/12 16:29
 * @Description
 **/
public class TiledEntityFactory extends BaseSystem {


    private float worldScale;
    private String compPackage;
    private String statePackage;
    private String entityLayerName;
    private B2dSystem b2DSystem;
    private TiledMapManager tiledMapManager;

    public TiledEntityFactory(String compPackage,String statePackage,String entityLayerName,float worldScale){
        this.worldScale = worldScale;
        this.compPackage = compPackage;
        this.statePackage = statePackage;
        this.entityLayerName = entityLayerName;
    }
    @Override
    protected void initialize() {
        TiledObjectToArtemis
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
            .build()
            .parse();
    }

    @Override
    protected void processSystem() {

    }
}

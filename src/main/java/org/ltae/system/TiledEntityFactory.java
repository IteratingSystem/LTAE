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
    private String compPackagePath;
    private B2dSystem b2DSystem;
    private TiledMapManager tiledMapManager;

    public TiledEntityFactory(float worldScale,String compPackagePath){
        this.worldScale = worldScale;
    }
    @Override
    protected void initialize() {
        TiledObjectToArtemis
            .builder()
            .scale(worldScale)
            .world(world)
            .box2DWorld(b2DSystem.box2DWorld)
            .tiledMap(tiledMapManager.curMap)
            .entityLayerName("ENTITIES")
            .addAutoInitComp(Pos.class)
            .addAutoInitComp(ZIndex.class)
            .addAutoInitComp(Render.class)
            .compPackage(compPackagePath)
            .build()
            .parse();
    }

    @Override
    protected void processSystem() {

    }
}

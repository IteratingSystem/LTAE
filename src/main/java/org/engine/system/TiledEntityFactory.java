package org.engine.system;

import com.artemis.BaseSystem;
import org.engine.component.Pos;
import org.engine.component.Render;
import org.engine.component.ZIndex;
import org.engine.tiled.TiledObjectToArtemis;

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

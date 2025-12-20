package org.ltae.system;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import net.mostlyoriginal.api.event.common.Subscribe;

import org.ltae.event.MapEvent;

/**
 * @Author: WenLong
 * @Date: 2024-09-09-17:06
 * @Description: 渲染瓦片地图系统
 */

public class RenderTiledSystem extends BaseSystem {
    private final static String TAG = RenderTiledSystem.class.getSimpleName();

    //其它世界
    private TiledMapSystem tiledMapSystem;
    private B2dSystem b2dSystem;
    private CameraSystem cameraSystem;
    private RenderFrameSystem renderFrameSystem;
    private TiledMap tiledMap;
    public OrthogonalTiledMapRenderer mapRenderer;
    private float worldScale;

    public RenderTiledSystem(float worldScale){
        this.worldScale = worldScale;
    }

    public void changeMap(){
        tiledMap = tiledMapSystem.getTiledMap();
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap,worldScale);
    }

    @Override
    protected void initialize() {
        changeMap();
    }

    @Override
    protected void processSystem() {
        if (tiledMap == null){
            Gdx.app.error(TAG,"The variable tiledMap is null!");
            return;
        }

        ScreenUtils.clear(0.0f,0.0f,0.0f,1);
        mapRenderer.setView(cameraSystem.camera);
        mapRenderer.render();
    }
}

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
    private TiledMapSystem tiledMapSystem;
    private CameraSystem cameraSystem;
    private TiledMap tiledMap;
    public OrthogonalTiledMapRenderer mapRenderer;
    public OrthographicCamera camera;
    private float worldScale;

    public RenderTiledSystem(float worldScale){
        this.worldScale = worldScale;
    }

    public void changeMap(String mapName){
        tiledMapSystem.changeCurrent(mapName);
        tiledMap = tiledMapSystem.getTiledMap();
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap,worldScale);
    }
    @Override
    protected void initialize() {
        tiledMap = tiledMapSystem.getTiledMap();
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap,worldScale);
        camera = cameraSystem.camera;
    }

    @Override
    protected void processSystem() {
        if (tiledMap == null){
            Gdx.app.error(TAG,"The variable tiledMap is null!");
            return;
        }

        ScreenUtils.clear(0.0f,0.7f,0.2f,1);
        mapRenderer.setView(camera);
        mapRenderer.render();
    }
    @Subscribe
    public void onEvent(MapEvent event){
        if (event.type == MapEvent.CHANGE_MAP) {
            changeMap(event.mapName);
            return;
        }
    }
}

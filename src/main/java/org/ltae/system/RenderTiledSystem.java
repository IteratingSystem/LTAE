package org.ltae.system;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * @Author: WenLong
 * @Date: 2024-09-09-17:06
 * @Description: 渲染瓦片地图系统
 */

public class RenderTiledSystem extends BaseSystem {
    private final static String TAG = RenderTiledSystem.class.getSimpleName();



    private CameraSystem cameraSystem;
    private TiledMapManager tiledMapManager;


    private TiledMap tiledMap;
    public OrthogonalTiledMapRenderer mapRenderer;
    public OrthographicCamera camera;
    private float worldScale;

    public RenderTiledSystem(float worldScale){
        this.worldScale = worldScale;
    }
    @Override
    protected void initialize() {
        tiledMap = tiledMapManager.curMap;
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
}

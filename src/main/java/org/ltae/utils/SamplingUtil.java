package org.ltae.utils;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * 图层采样工具
 */
public class SamplingUtil {
    private static SamplingUtil instance;
    private FrameBuffer fbo;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;

    private SamplingUtil(){
        camera = new OrthographicCamera();
        mapRenderer = new OrthogonalTiledMapRenderer(null,1);

    }

    public static SamplingUtil getInstance(){
        if (instance == null){
            instance = new SamplingUtil();
        }
        return instance;
    }

    public TextureRegion samplingLayer(TiledMap tiledMap,String layerName){
        return samplingLayer(tiledMap,layerName,Texture.TextureFilter.Nearest,Texture.TextureFilter.Nearest);
    }
    public TextureRegion samplingLayer(TiledMap tiledMap,String layerName,Texture.TextureFilter minFilter, Texture.TextureFilter magFilter){
        TiledMapTileLayer mapLayer = (TiledMapTileLayer)tiledMap.getLayers().get(layerName);
        int width = mapLayer.getWidth()*mapLayer.getTileWidth();
        int height = mapLayer.getHeight()*mapLayer.getTileHeight();
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
        camera.setToOrtho(true, width, height);
        camera.update();
        mapRenderer.setMap(tiledMap);
        mapRenderer.setView(camera);


        fbo.begin();
//        ScreenUtils.clear(0.0f,0.0f,0.0f,0);
        mapRenderer.getBatch().begin();
        mapRenderer.renderTileLayer(mapLayer);
        mapRenderer.getBatch().end();
        fbo.end();

        Texture texture = fbo.getColorBufferTexture();
        texture.setFilter(minFilter, magFilter);
        fbo.dispose();
        return new TextureRegion(texture);
    }
}

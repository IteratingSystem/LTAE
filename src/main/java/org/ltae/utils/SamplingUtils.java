package org.ltae.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

/**
 * 图层采样工具
 */
public class SamplingUtils {
    private static final String TAG = SamplingUtils.class.getSimpleName();
    private static SamplingUtils instance;
    private FrameBuffer fbo;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;

    private SamplingUtils(){
        camera = new OrthographicCamera();
        mapRenderer = new OrthogonalTiledMapRenderer(null,1);

    }

    public static SamplingUtils getInstance(){
        if (instance == null){
            instance = new SamplingUtils();
        }
        return instance;
    }

    public TextureRegion samplingLayer(TiledMap tiledMap,String layerName){
        return samplingLayer(tiledMap,layerName,Texture.TextureFilter.Nearest,Texture.TextureFilter.Nearest);
    }
    public TextureRegion samplingLayer(TiledMap tiledMap,String layerName,Texture.TextureFilter minFilter, Texture.TextureFilter magFilter){
        TiledMapTileLayer mapLayer = null;
        try{
            mapLayer = (TiledMapTileLayer)tiledMap.getLayers().get(layerName);
        } catch (Exception e) {
            Gdx.app.error(TAG,"Failed to samplingLayer,layerName is '"+layerName+"'");
            return null;
        }


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
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        mapRenderer.renderTileLayer(mapLayer);
        mapRenderer.getBatch().end();
        fbo.end();

        Texture texture = fbo.getColorBufferTexture();
        texture.setFilter(minFilter, magFilter);
        return new TextureRegion(texture);
    }
}

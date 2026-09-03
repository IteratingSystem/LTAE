package org.worldloom.system;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import org.worldloom.manager.ShaderManager;
import org.worldloom.shader.TileLayerShaderConfig;

/**
 * 使用自定义Shader直接绘制指定的瓦片层。
 */
public final class ShaderTileLayerRenderSystem extends BaseSystem {
    private static final String TAG =
        ShaderTileLayerRenderSystem.class.getSimpleName();

    private final TileLayerShaderConfig config;
    private TiledMapSystem tiledMapSystem;
    private RenderTiledSystem renderTiledSystem;
    private CameraSystem cameraSystem;
    private PixelPerfectRenderSystem pixelPerfectRenderSystem;
    private ShaderProgram shaderProgram;
    private TiledMap warnedMap;

    public ShaderTileLayerRenderSystem(TileLayerShaderConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("config cannot be null");
        }
        this.config = config;
    }

    @Override
    protected void initialize() {
        ShaderManager shaderManager = ShaderManager.getInstance();
        String vertex = shaderManager.getVertexContext(config.getVertexName());
        String fragment = shaderManager.getFragmentContext(
            config.getFragmentName());
        if (vertex == null || fragment == null) {
            throw new IllegalStateException(
                "Tile layer shader source is missing: " + config.getLayerName());
        }
        shaderProgram = new ShaderProgram(vertex, fragment);
        if (!shaderProgram.isCompiled()) {
            String log = shaderProgram.getLog();
            shaderProgram.dispose();
            shaderProgram = null;
            Gdx.app.error(TAG, "Tile layer shader compile failed: " + log);
            throw new IllegalStateException(
                "Tile layer shader compile failed: " + config.getLayerName());
        }
        config.getUniforms().initialize(world, shaderProgram);
        Gdx.app.log(TAG, "Tile layer shader initialized: "
            + config.getLayerName());
    }

    @Override
    protected void processSystem() {
        TiledMap tiledMap = tiledMapSystem.getTiledMap();
        MapLayer layer = tiledMap.getLayers().get(config.getLayerName());
        if (layer == null) {
            return;
        }
        if (!(layer instanceof TiledMapTileLayer tileLayer)) {
            if (warnedMap != tiledMap) {
                warnedMap = tiledMap;
                Gdx.app.error(TAG, "Configured layer is not a tile layer: "
                    + config.getLayerName());
            }
            return;
        }

        renderLayer(tiledMap, tileLayer);
    }

    private void renderLayer(TiledMap tiledMap, TiledMapTileLayer tileLayer) {
        renderTiledSystem.mapRenderer.setView(cameraSystem.camera);
        Batch batch = renderTiledSystem.mapRenderer.getBatch();
        ShaderProgram previousShader = batch.getShader();
        batch.setShader(shaderProgram);
        batch.begin();
        try {
            config.getUniforms().apply(
                tiledMap, shaderProgram, world.getDelta());
            pixelPerfectRenderSystem.resumeWorldTarget();
            restoreBatchState(batch);
            Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
            renderTiledSystem.mapRenderer.renderTileLayer(tileLayer);
        } finally {
            Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
            batch.end();
            batch.setShader(previousShader);
        }
    }

    /** 恢复Uniform回调中临时Batch或FrameBuffer可能改变的GL状态。 */
    private void restoreBatchState(Batch batch) {
        Gdx.gl.glBlendEquation(GL20.GL_FUNC_ADD);
        if (batch.isBlendingEnabled()) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFuncSeparate(
                batch.getBlendSrcFunc(), batch.getBlendDstFunc(),
                batch.getBlendSrcFuncAlpha(), batch.getBlendDstFuncAlpha());
        } else {
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
        shaderProgram.bind();
    }

    @Override
    protected void dispose() {
        config.getUniforms().dispose();
        if (shaderProgram != null) {
            shaderProgram.dispose();
        }
    }
}

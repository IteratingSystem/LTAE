package org.ltae.system;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import org.ltae.environment.CloudShadowConfig;
import org.ltae.manager.ShaderManager;

/**
 * 在世界画面上绘制随风移动的云影，不绘制云层本身。
 */
public final class CloudShadowSystem extends BaseSystem {
    private static final String TAG = CloudShadowSystem.class.getSimpleName();
    private static final String SHADER_PATH = "shader/environment/cloud_shadow";

    private final CloudShadowConfig config;
    private final Vector2 windDisplacement = new Vector2();
    private AssetSystem assetSystem;
    private CameraSystem cameraSystem;
    private TiledMapSystem tiledMapSystem;
    private WindSystem windSystem;
    private Texture cloudNoise;
    private Mesh screenQuad;
    private ShaderProgram shaderProgram;
    private boolean renderLogged;

    public CloudShadowSystem(CloudShadowConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("config cannot be null");
        }
        this.config = config;
    }

    @Override
    protected void initialize() {
        cloudNoise = assetSystem.noiseData.get(config.getNoiseName());
        if (cloudNoise == null) {
            Gdx.app.error(TAG, "Missing cloud noise texture: "
                + config.getNoiseName());
            throw new IllegalStateException("Missing cloud noise texture: "
                + config.getNoiseName());
        }

        screenQuad = createScreenQuad();
        ShaderManager shaderManager = ShaderManager.getInstance();
        shaderProgram = new ShaderProgram(
            shaderManager.getVertexContext(SHADER_PATH),
            shaderManager.getFragmentContext(SHADER_PATH));
        if (!shaderProgram.isCompiled()) {
            String log = shaderProgram.getLog();
            Gdx.app.error(TAG, "Cloud shadow shader compile failed: " + log);
            throw new IllegalStateException("Cloud shadow shader compile failed");
        }
        Gdx.app.log(TAG, "Cloud shadow system initialized");
    }

    @Override
    protected void processSystem() {
        if (!config.isEnabled(tiledMapSystem.getCurrent())
            || config.getOpacity() <= 0f) {
            return;
        }

        if (!renderLogged) {
            renderLogged = true;
            Gdx.app.log(TAG, "Cloud shadows active on map: "
                + tiledMapSystem.getCurrent());
        }

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        cloudNoise.bind(0);
        shaderProgram.bind();
        shaderProgram.setUniformi("u_cloudNoise", 0);
        shaderProgram.setUniformMatrix(
            "u_invProjTrans", cameraSystem.camera.invProjectionView);
        shaderProgram.setUniformf("u_windDisplacement",
            windSystem.getDisplacement(windDisplacement));
        shaderProgram.setUniformf("u_worldSize", config.getWorldSize());
        shaderProgram.setUniformf(
            "u_driftMultiplier", config.getDriftMultiplier());
        shaderProgram.setUniformf(
            "u_coverageThreshold", config.getCoverageThreshold());
        shaderProgram.setUniformf("u_edgeSoftness", config.getEdgeSoftness());
        shaderProgram.setUniformf("u_opacity", config.getOpacity());
        screenQuad.render(shaderProgram, GL20.GL_TRIANGLE_FAN);
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private Mesh createScreenQuad() {
        Mesh mesh = new Mesh(true, 4, 0,
            new VertexAttribute(VertexAttributes.Usage.Position, 2,
                "a_position"),
            new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2,
                "a_texCoord"));
        mesh.setVertices(new float[]{
            -1f, -1f, 0f, 0f,
            1f, -1f, 1f, 0f,
            1f, 1f, 1f, 1f,
            -1f, 1f, 0f, 1f
        });
        return mesh;
    }

    @Override
    protected void dispose() {
        if (screenQuad != null) {
            screenQuad.dispose();
        }
        if (shaderProgram != null) {
            shaderProgram.dispose();
        }
    }
}

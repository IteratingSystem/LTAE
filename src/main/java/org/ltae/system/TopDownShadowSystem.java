package org.ltae.system;

import box2dLight.RayHandler;
import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.EntitySubscription;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.LongMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import org.ltae.component.Inert;
import org.ltae.component.Pos;
import org.ltae.component.Render;
import org.ltae.component.SoarHeight;
import org.ltae.component.TopDownShadow;
import org.ltae.component.ZIndex;
import org.ltae.light.SunLightConfig;
import org.ltae.light.TopDownShadowConfig;
import org.ltae.light.TopDownShadowLight;
import org.ltae.light.TopDownSunLight;
import org.ltae.manager.ShaderManager;

import java.nio.IntBuffer;

/**
 * 为带有TopDownShadow组件的精灵生成并合成俯视角阴影。
 */
public class TopDownShadowSystem extends BaseSystem {
    private static final String TAG = TopDownShadowSystem.class.getSimpleName();
    private static final int SHADOW_SEGMENTS = 32;
    private static final int SUN_RIBBON_MAX_VERTICES = 8;
    private static final int SUN_RIBBON_VERTEX_SIZE = 3;
    private static final float SUN_RIBBON_CHECK_TEXELS = 6f;
    private static final float SUN_RIBBON_SWITCH_TEXELS = 2.5f;
    private static final float OPAQUE_ALPHA_THRESHOLD = 0.01f;
    private static final int GL_MAX_BLEND_EQUATION = 0x8008;
    private static final String SHADER_PATH = "shader/topdown/";

    private final float worldScale;
    private final TopDownShadowConfig config;
    private final SunLightConfig sunConfig;
    private final IntArray sortedShadowEntities = new IntArray();
    private final Vector2 lightDirection = new Vector2();
    private final Vector2 lightPosition = new Vector2();
    private final IntBuffer glStateBuffer = BufferUtils.newIntBuffer(1);
    private final int[] previousTextureBindings = new int[4];
    private final ObjectMap<Texture, LongMap<OpaqueBounds>> opaqueBoundsCache =
        new ObjectMap<>();
    private final ObjectSet<Texture> unreadableOpaqueTextures = new ObjectSet<>();
    private final float[] ribbonLocal = new float[8];
    private final float[] ribbonCandidates =
        new float[SUN_RIBBON_MAX_VERTICES * SUN_RIBBON_VERTEX_SIZE];
    private final float[] ribbonHull =
        new float[SUN_RIBBON_MAX_VERTICES * 2 * SUN_RIBBON_VERTEX_SIZE];

    private B2dSystem b2dSystem;
    private CameraSystem cameraSystem;
    private M<Pos> mPos;
    private M<Render> mRender;
    private M<ZIndex> mZIndex;
    private M<SoarHeight> mSoarHeight;
    private M<TopDownShadow> mTopDownShadow;
    private M<org.ltae.component.TopDownPointLight> mTopDownPointLight;

    private EntitySubscription shadowSubscription;
    private EntitySubscription pointLightSubscription;
    private RayHandler pointRayHandler;
    private TopDownSunLight sunLight;
    private SpriteBatch spriteBatch;
    private Mesh screenQuad;
    private Mesh sunProjectedShadowMesh;
    private Mesh projectedShadowMesh;
    private Mesh sunShadowRibbonMesh;
    private FrameBuffer depthDownsampleBuffer;
    private FrameBuffer entityMask;
    private FrameBuffer groundShadowSource;
    private FrameBuffer groundShadowMask;
    private FrameBuffer receiverShadowMask;
    private ShaderProgram entityMaskShader;
    private ShaderProgram projectedShadowShader;
    private ShaderProgram sunShadowRibbonShader;
    private ShaderProgram receiverShader;
    private ShaderProgram sunCompositeShader;
    private ShaderProgram pointCompositeShader;
    private ShaderProgram depthDownsampleShader;
    private ShaderProgram depthExpandShader;
    private int bufferWidth;
    private int bufferHeight;
    private int depthBufferHeight;
    private float shadowTime;
    public TopDownShadowSystem(float worldScale, TopDownShadowConfig config) {
        this(worldScale, config, new SunLightConfig());
    }

    public TopDownShadowSystem(float worldScale, TopDownShadowConfig config,
                               SunLightConfig sunConfig) {
        if (worldScale <= 0f) {
            throw new IllegalArgumentException("worldScale must be greater than zero");
        }
        if (config == null) {
            throw new IllegalArgumentException("config cannot be null");
        }
        if (sunConfig == null) {
            throw new IllegalArgumentException("sunConfig cannot be null");
        }
        this.worldScale = worldScale;
        this.config = config;
        this.sunConfig = sunConfig;
    }

    @Override
    protected void initialize() {
        shadowSubscription = world.getAspectSubscriptionManager().get(
            Aspect.all(Render.class, Pos.class, ZIndex.class, TopDownShadow.class)
                .exclude(Inert.class));
        pointLightSubscription = world.getAspectSubscriptionManager().get(
            Aspect.all(Pos.class, org.ltae.component.TopDownPointLight.class)
                .exclude(Inert.class));

        RayHandler.useDiffuseLight(true);
        pointRayHandler = new RayHandler(b2dSystem.box2DWorld);
        pointRayHandler.setAmbientLight(0f);
        pointRayHandler.setBlur(true);
        pointRayHandler.setBlurNum(1);
        pointRayHandler.setLightMapRendering(false);
        sunLight = new TopDownSunLight(pointRayHandler, 0f,
            config.getHeightRange());
        sunLight.setSunBearingDegree(sunConfig.getReferenceBearingDegree());
        sunLight.setElevationDegree(sunConfig.getMinimumElevationDegree());

        spriteBatch = new SpriteBatch();
        screenQuad = createScreenQuad();
        sunProjectedShadowMesh = createProjectedShadowMesh(1);
        projectedShadowMesh = createProjectedShadowMesh(SHADOW_SEGMENTS);
        sunShadowRibbonMesh = createSunShadowRibbonMesh();
        ShaderProgram.pedantic = false;
        ShaderManager shaderManager = ShaderManager.getInstance();
        entityMaskShader = compileShader(
            shaderManager.getVertexContext(SHADER_PATH + "sprite"),
            shaderManager.getFragmentContext(SHADER_PATH + "entity_mask"),
            "Entity mask");
        projectedShadowShader = compileShader(
            shaderManager.getVertexContext(SHADER_PATH + "projected_shadow"),
            shaderManager.getFragmentContext(SHADER_PATH + "projected_shadow"),
            "Projected shadow");
        sunShadowRibbonShader = compileShader(
            shaderManager.getVertexContext(SHADER_PATH + "shadow_ribbon"),
            shaderManager.getFragmentContext(SHADER_PATH + "shadow_ribbon"),
            "Sun shadow ribbon");
        receiverShader = compileShader(
            shaderManager.getVertexContext(SHADER_PATH + "receiver"),
            shaderManager.getFragmentContext(SHADER_PATH + "receiver"),
            "Receiver shadow");
        sunCompositeShader = compileShader(
            shaderManager.getVertexContext(SHADER_PATH + "screen"),
            shaderManager.getFragmentContext(SHADER_PATH + "sun_composite"),
            "Sun composite");
        pointCompositeShader = compileShader(
            shaderManager.getVertexContext(SHADER_PATH + "screen"),
            shaderManager.getFragmentContext(SHADER_PATH + "point_composite"),
            "Point composite");
        depthDownsampleShader = compileShader(
            shaderManager.getVertexContext(SHADER_PATH + "screen"),
            shaderManager.getFragmentContext(SHADER_PATH + "depth_downsample"),
            "Shadow depth downsample");
        depthExpandShader = compileShader(
            shaderManager.getVertexContext(SHADER_PATH + "screen"),
            shaderManager.getFragmentContext(SHADER_PATH + "depth_expand"),
            "Shadow depth expansion");
        resizeBuffersIfNeeded();
        validateRenderOrder();
        Gdx.app.log(TAG, "Top-down shadow system initialized");
    }

    /** 检查阴影合成是否位于世界渲染之后。 */
    private void validateRenderOrder() {
        int shadowIndex = -1;
        int renderIndex = -1;
        for (int i = 0; i < world.getSystems().size(); i++) {
            BaseSystem system = world.getSystems().get(i);
            if (system == this) {
                shadowIndex = i;
            } else if (system instanceof RenderBatchingSystem) {
                renderIndex = i;
            }
        }
        if (renderIndex >= 0 && shadowIndex >= 0 && shadowIndex < renderIndex) {
            Gdx.app.error(TAG,
                "TopDownShadowSystem must be registered with LOWEST priority "
                    + "after the world render systems");
        }
    }

    @Override
    protected void processSystem() {
        resizeBuffersIfNeeded();
        shadowTime = (shadowTime + Math.min(world.getDelta(), 1f / 15f)) % 1000f;
        synchronizePointLights();
        if (shadowSubscription.getEntities().isEmpty()
            && pointLightSubscription.getEntities().isEmpty()) {
            return;
        }

        int previousActiveTexture = captureTextureBindings();
        try {
            sortShadowEntities();
            renderEntityMask();

            renderShadowMasks(sunLight);
            compositeSunShadow();
        } finally {
            restoreTextureBindings(previousActiveTexture);
        }
    }

    /** 在环境光合成完成后绘制点光源，避免环境光再次压暗灯光。 */
    void renderPointLightsAfterAmbient() {
        if (pointLightSubscription.getEntities().isEmpty()) {
            return;
        }

        int previousActiveTexture = captureTextureBindings();
        try {
            renderPointLights();
        } finally {
            restoreTextureBindings(previousActiveTexture);
        }
    }

    /**
     * 保存本系统会占用的纹理单元，避免污染海洋等其它多纹理Shader。
     */
    private int captureTextureBindings() {
        int activeTexture = getGlInteger(GL20.GL_ACTIVE_TEXTURE);
        for (int unit = 0; unit < previousTextureBindings.length; unit++) {
            Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0 + unit);
            previousTextureBindings[unit] =
                getGlInteger(GL20.GL_TEXTURE_BINDING_2D);
        }
        Gdx.gl.glActiveTexture(activeTexture);
        return activeTexture;
    }

    private void restoreTextureBindings(int activeTexture) {
        for (int unit = 0; unit < previousTextureBindings.length; unit++) {
            Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0 + unit);
            Gdx.gl.glBindTexture(
                GL20.GL_TEXTURE_2D, previousTextureBindings[unit]);
        }
        Gdx.gl.glActiveTexture(activeTexture);
    }

    private int getGlInteger(int parameter) {
        glStateBuffer.clear();
        Gdx.gl.glGetIntegerv(parameter, glStateBuffer);
        return glStateBuffer.get(0);
    }

    private void synchronizePointLights() {
        IntBag entities = pointLightSubscription.getEntities();
        int[] ids = entities.getData();
        for (int i = 0; i < entities.size(); i++) {
            int entityId = ids[i];
            Pos pos = mPos.get(entityId);
            org.ltae.component.TopDownPointLight component =
                mTopDownPointLight.get(entityId);
            if (component.light == null) {
                component.light = new org.ltae.light.TopDownPointLight(
                    pointRayHandler, Math.max(3, component.rays), component.color,
                    Math.max(0.01f, component.distance * worldScale),
                    worldScale * (pos.x + component.offsetX),
                    worldScale * (pos.y + component.offsetY),
                    Math.max(0.01f, component.height * worldScale));
            }
            component.light.setPosition(
                worldScale * (pos.x + component.offsetX),
                worldScale * (pos.y + component.offsetY));
            component.light.setActive(false);
        }
    }

    private void renderPointLights() {
        IntBag entities = pointLightSubscription.getEntities();
        int[] ids = entities.getData();
        pointRayHandler.setCombinedMatrix(cameraSystem.camera);
        for (int i = 0; i < entities.size(); i++) {
            org.ltae.component.TopDownPointLight component =
                mTopDownPointLight.get(ids[i]);
            if (!component.onOff || component.light == null) {
                continue;
            }
            component.light.setActive(true);
            pointRayHandler.update();
            pointRayHandler.prepareRender();
            renderShadowMasks(component.light);
            compositePointLight();
            component.light.setActive(false);
        }
        for (int i = 0; i < entities.size(); i++) {
            org.ltae.component.TopDownPointLight component =
                mTopDownPointLight.get(ids[i]);
            if (component.light != null) {
                component.light.setActive(component.onOff);
            }
        }
    }

    private void sortShadowEntities() {
        sortedShadowEntities.clear();
        IntBag entities = shadowSubscription.getEntities();
        int[] ids = entities.getData();
        for (int i = 0; i < entities.size(); i++) {
            int entityId = ids[i];
            sortedShadowEntities.add(entityId);
        }
        for (int i = 1; i < sortedShadowEntities.size; i++) {
            int entityId = sortedShadowEntities.get(i);
            float footY = getFootY(entityId);
            int insertAt = i;
            while (insertAt > 0
                && getFootY(sortedShadowEntities.get(insertAt - 1)) < footY) {
                sortedShadowEntities.set(
                    insertAt, sortedShadowEntities.get(insertAt - 1));
                insertAt--;
            }
            sortedShadowEntities.set(insertAt, entityId);
        }
    }

    private void renderEntityMask() {
        entityMask.begin();
        clearBuffer();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendEquation(GL_MAX_BLEND_EQUATION);
        Gdx.gl.glBlendFunc(GL20.GL_ONE, GL20.GL_ONE);
        spriteBatch.setProjectionMatrix(cameraSystem.camera.combined);
        spriteBatch.setShader(entityMaskShader);
        spriteBatch.begin();
        entityMaskShader.setUniformi("u_texture", 0);
        for (int i = 0; i < sortedShadowEntities.size; i++) {
            drawEntity(sortedShadowEntities.get(i));
        }
        spriteBatch.end();
        spriteBatch.setShader(null);
        Gdx.gl.glBlendEquation(GL20.GL_FUNC_ADD);
        Gdx.gl.glDisable(GL20.GL_BLEND);
        entityMask.end();
    }

    private void renderShadowMasks(TopDownShadowLight light) {
        renderGroundShadowMask(light);
        renderReceiverShadowMask(light);
    }

    private void renderGroundShadowMask(TopDownShadowLight light) {
        groundShadowSource.begin();
        clearBuffer();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendEquation(GL_MAX_BLEND_EQUATION);
        Gdx.gl.glBlendFunc(GL20.GL_ONE, GL20.GL_ONE);
        projectedShadowShader.bind();
        projectedShadowShader.setUniformi("u_texture", 0);
        projectedShadowShader.setUniformMatrix(
            "u_projTrans", cameraSystem.camera.combined);
        projectedShadowShader.setUniformf(
            "u_heightRange", config.getHeightRange());
        setLightUniforms(projectedShadowShader, light);
        renderProjectedCasters(light);
        Gdx.gl.glBlendEquation(GL20.GL_FUNC_ADD);
        Gdx.gl.glDisable(GL20.GL_BLEND);
        groundShadowSource.end();
        expandDepth(groundShadowSource, groundShadowMask);
    }

    private void renderProjectedCasters(TopDownShadowLight light) {
        for (int i = 0; i < sortedShadowEntities.size; i++) {
            int entityId = sortedShadowEntities.get(i);
            if (!isCasterInRange(entityId, light)) {
                continue;
            }
            renderProjectedEntity(entityId, light.isDirectional());
        }
    }

    private void renderReceiverShadowMask(TopDownShadowLight light) {
        receiverShadowMask.begin();
        clearBuffer();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendEquation(GL_MAX_BLEND_EQUATION);
        Gdx.gl.glBlendFunc(GL20.GL_ONE, GL20.GL_ONE);
        spriteBatch.setProjectionMatrix(cameraSystem.camera.combined);
        spriteBatch.setShader(receiverShader);
        spriteBatch.begin();
        receiverShader.setUniformi("u_texture", 0);
        receiverShader.setUniformi("u_groundShadow", 1);
        receiverShader.setUniformMatrix(
            "u_projTrans", cameraSystem.camera.combined);
        receiverShader.setUniformf("u_heightRange", config.getHeightRange());
        receiverShader.setUniformf("u_time", shadowTime);
        setLightUniforms(receiverShader, light);
        groundShadowMask.getColorBufferTexture().bind(1);
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        for (int i = 0; i < sortedShadowEntities.size; i++) {
            int entityId = sortedShadowEntities.get(i);
            receiverShader.setUniformf("u_footY", getFootY(entityId));
            drawReceiverEntity(entityId);
        }
        spriteBatch.end();
        spriteBatch.setShader(null);
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        Gdx.gl.glBlendEquation(GL20.GL_FUNC_ADD);
        Gdx.gl.glDisable(GL20.GL_BLEND);
        receiverShadowMask.end();
    }

    private void setLightUniforms(ShaderProgram shader,
                                  TopDownShadowLight light) {
        shader.setUniformf("u_pointMode", light.isDirectional() ? 0f : 1f);
        shader.setUniformf(
            "u_shadowDirection", light.getShadowDirection(lightDirection));
        if (shader.hasUniform("u_sunShadowLengthScale")) {
            shader.setUniformf("u_sunShadowLengthScale",
                light.getShadowLengthScale());
        }
        light.getShadowPosition(lightPosition);
        shader.setUniformf("u_lightPosition", lightPosition);
        shader.setUniformf("u_lightHeight", light.getShadowHeight());
        if (shader.hasUniform("u_lightRange")) {
            shader.setUniformf("u_lightRange", light.getShadowRange());
        }
    }

    private void renderProjectedEntity(int entityId, boolean directional) {
        Render render = mRender.get(entityId);
        Pos pos = mPos.get(entityId);
        if (!isRenderable(render)) {
            return;
        }
        float soarHeight = getSoarHeight(entityId);
        if (render.textureSheets != null && !render.textureSheets.isEmpty()) {
            for (int i = 0; i < render.textureSheets.size; i++) {
                TextureRegion region = render.textureSheets.get(i);
                if (region != null) {
                    renderProjectedRegion(entityId, render, pos, region,
                        soarHeight + i * render.sheetOffset, getFootY(entityId),
                        directional);
                }
            }
            return;
        }
        renderProjectedRegion(
            entityId, render, pos, render.keyframe, soarHeight, getFootY(entityId),
            directional);
    }

    private void renderProjectedRegion(int entityId, Render render, Pos pos,
                                       TextureRegion region, float extraY,
                                       float footY, boolean directional) {
        if (directional && renderSunShadowRibbonIfNeeded(
            entityId, render, pos, region, extraY, footY)) {
            return;
        }
        projectedShadowShader.bind();
        Texture texture = region.getTexture();
        float drawX = worldScale * (pos.x + render.offsetX);
        float drawY = worldScale * (pos.y + render.offsetY + extraY);
        float scaleX = worldScale * render.scaleWidth;
        float scaleY = worldScale * render.scaleHeight;
        projectedShadowShader.setUniformf("u_drawPosition", drawX, drawY);
        projectedShadowShader.setUniformf(
            "u_origin", render.originX, render.originY);
        projectedShadowShader.setUniformf(
            "u_size", region.getRegionWidth(), region.getRegionHeight());
        projectedShadowShader.setUniformf("u_scale", scaleX, scaleY);
        projectedShadowShader.setUniformf("u_rotation", render.rotation);
        projectedShadowShader.setUniformf("u_footY", footY);
        projectedShadowShader.setUniformf(
            "u_shadowDepth", getShadowDepth(entityId, region));
        setTextureCoordinates(projectedShadowShader, render, region);
        texture.bind(0);
        Mesh mesh = directional ? sunProjectedShadowMesh : projectedShadowMesh;
        mesh.render(
            projectedShadowShader, GL20.GL_TRIANGLE_STRIP);
    }

    /**
     * 太阳接近平行于地面时，普通纹理投影会退化为低于一个像素的线。
     * 此时用真实不透明边界生成连续凸阴影带，避免光栅化后的分段与消失。
     */
    private boolean renderSunShadowRibbonIfNeeded(
        int entityId, Render render, Pos pos, TextureRegion region,
        float extraY, float footY) {
        float fullSpan = getProjectedVerticalSpanTexels(
            render, pos, region, extraY, footY,
            0f, region.getRegionWidth(), 0f, region.getRegionHeight());
        if (fullSpan >= SUN_RIBBON_CHECK_TEXELS) {
            return false;
        }

        OpaqueBounds opaqueBounds = getOpaqueBounds(region);
        float opaqueSpan = getProjectedVerticalSpanTexels(
            render, pos, region, extraY, footY,
            opaqueBounds.left, opaqueBounds.right,
            opaqueBounds.bottom, opaqueBounds.top);
        if (opaqueSpan >= SUN_RIBBON_SWITCH_TEXELS) {
            return false;
        }

        int hullSize = buildSunShadowRibbon(
            render, pos, region, extraY, footY,
            opaqueBounds.left, opaqueBounds.right,
            opaqueBounds.bottom, opaqueBounds.top,
            getShadowDepth(entityId, region));
        if (hullSize < 3) {
            return false;
        }
        sunShadowRibbonMesh.setVertices(
            ribbonHull, 0, hullSize * SUN_RIBBON_VERTEX_SIZE);
        sunShadowRibbonShader.bind();
        sunShadowRibbonShader.setUniformMatrix(
            "u_projTrans", cameraSystem.camera.combined);
        sunShadowRibbonShader.setUniformf(
            "u_heightRange", config.getHeightRange());
        sunShadowRibbonMesh.render(
            sunShadowRibbonShader, GL20.GL_TRIANGLE_FAN, 0, hullSize);
        return true;
    }

    private float getProjectedVerticalSpanTexels(
        Render render, Pos pos, TextureRegion region, float extraY,
        float footY, float left, float right, float bottom, float top) {
        projectBounds(render, pos, region, extraY, footY,
            left, right, bottom, top, 0f);
        float minimumY = Float.POSITIVE_INFINITY;
        float maximumY = Float.NEGATIVE_INFINITY;
        for (int i = 0; i < 4; i++) {
            int offset = i * SUN_RIBBON_VERTEX_SIZE;
            minimumY = Math.min(minimumY, ribbonCandidates[offset + 1]);
            maximumY = Math.max(maximumY, ribbonCandidates[offset + 1]);
        }
        float visibleWorldHeight = cameraSystem.camera.viewportHeight
            * cameraSystem.camera.zoom;
        float worldPerTexel = visibleWorldHeight / groundShadowSource.getHeight();
        return (maximumY - minimumY) / Math.max(worldPerTexel, 0.0001f);
    }

    private int buildSunShadowRibbon(
        Render render, Pos pos, TextureRegion region, float extraY,
        float footY, float left, float right, float bottom, float top,
        float depth) {
        projectBounds(render, pos, region, extraY, footY,
            left, right, bottom, top,
            Math.max(depth, 0.0001f) * 0.5f);
        sortRibbonCandidates();
        return buildRibbonConvexHull();
    }

    private void projectBounds(
        Render render, Pos pos, TextureRegion region, float extraY,
        float footY, float left, float right, float bottom, float top,
        float halfDepth) {
        if (render.flipX) {
            float originalLeft = left;
            left = region.getRegionWidth() - right;
            right = region.getRegionWidth() - originalLeft;
        }
        if (render.flipY) {
            float originalBottom = bottom;
            bottom = region.getRegionHeight() - top;
            top = region.getRegionHeight() - originalBottom;
        }

        ribbonLocal[0] = left;
        ribbonLocal[1] = bottom;
        ribbonLocal[2] = right;
        ribbonLocal[3] = bottom;
        ribbonLocal[4] = right;
        ribbonLocal[5] = top;
        ribbonLocal[6] = left;
        ribbonLocal[7] = top;
        float drawX = worldScale * (pos.x + render.offsetX);
        float drawY = worldScale * (pos.y + render.offsetY + extraY);
        float scaleX = worldScale * render.scaleWidth;
        float scaleY = worldScale * render.scaleHeight;
        float cosine = MathUtils.cosDeg(render.rotation);
        float sine = MathUtils.sinDeg(render.rotation);
        sunLight.getShadowDirection(lightDirection);
        float shadowLengthScale = sunLight.getShadowLengthScale();
        for (int i = 0; i < 4; i++) {
            float relativeX = (ribbonLocal[i * 2] - render.originX) * scaleX;
            float relativeY = (ribbonLocal[i * 2 + 1] - render.originY) * scaleY;
            float spriteX = drawX + render.originX
                + relativeX * cosine - relativeY * sine;
            float spriteY = drawY + render.originY
                + relativeX * sine + relativeY * cosine;
            float pixelHeight = Math.max(0f, spriteY - footY);
            float projectedX = spriteX
                + lightDirection.x * pixelHeight * shadowLengthScale;
            float projectedY = footY
                + lightDirection.y * pixelHeight * shadowLengthScale;
            // 凸阴影带与普通投影使用相同锚点，向太阳移动半个纵深。
            projectedX -= lightDirection.x * halfDepth;
            projectedY -= lightDirection.y * halfDepth;
            if (halfDepth <= 0f) {
                int candidate = i * SUN_RIBBON_VERTEX_SIZE;
                ribbonCandidates[candidate] = projectedX;
                ribbonCandidates[candidate + 1] = projectedY;
                ribbonCandidates[candidate + 2] = pixelHeight;
            } else {
                int lower = i * 2 * SUN_RIBBON_VERTEX_SIZE;
                ribbonCandidates[lower] = projectedX;
                ribbonCandidates[lower + 1] = projectedY - halfDepth;
                ribbonCandidates[lower + 2] = pixelHeight;
                ribbonCandidates[lower + 3] = projectedX;
                ribbonCandidates[lower + 4] = projectedY + halfDepth;
                ribbonCandidates[lower + 5] = pixelHeight;
            }
        }
    }

    private void sortRibbonCandidates() {
        for (int i = 1; i < 8; i++) {
            int candidate = i * SUN_RIBBON_VERTEX_SIZE;
            float x = ribbonCandidates[candidate];
            float y = ribbonCandidates[candidate + 1];
            float height = ribbonCandidates[candidate + 2];
            int insertion = i;
            while (insertion > 0 && isPointAfter(
                ribbonCandidates[(insertion - 1) * SUN_RIBBON_VERTEX_SIZE],
                ribbonCandidates[(insertion - 1) * SUN_RIBBON_VERTEX_SIZE + 1],
                x, y)) {
                int destination = insertion * SUN_RIBBON_VERTEX_SIZE;
                int source = (insertion - 1) * SUN_RIBBON_VERTEX_SIZE;
                ribbonCandidates[destination] = ribbonCandidates[source];
                ribbonCandidates[destination + 1] =
                    ribbonCandidates[source + 1];
                ribbonCandidates[destination + 2] =
                    ribbonCandidates[source + 2];
                insertion--;
            }
            int destination = insertion * SUN_RIBBON_VERTEX_SIZE;
            ribbonCandidates[destination] = x;
            ribbonCandidates[destination + 1] = y;
            ribbonCandidates[destination + 2] = height;
        }
    }

    private boolean isPointAfter(float firstX, float firstY,
                                 float secondX, float secondY) {
        return firstX > secondX || (firstX == secondX && firstY > secondY);
    }

    private int buildRibbonConvexHull() {
        int hullSize = 0;
        for (int i = 0; i < 8; i++) {
            while (hullSize >= 2 && crossHullPoint(
                hullSize - 2, hullSize - 1,
                candidateX(i), candidateY(i)) <= 0f) {
                hullSize--;
            }
            setHullPoint(hullSize++, candidateX(i), candidateY(i),
                candidateHeight(i));
        }
        int lowerSize = hullSize;
        for (int i = 6; i >= 0; i--) {
            while (hullSize > lowerSize && crossHullPoint(
                hullSize - 2, hullSize - 1,
                candidateX(i), candidateY(i)) <= 0f) {
                hullSize--;
            }
            setHullPoint(hullSize++, candidateX(i), candidateY(i),
                candidateHeight(i));
        }
        return Math.max(0, hullSize - 1);
    }

    private float crossHullPoint(int first, int second, float x, float y) {
        float firstX = ribbonHull[first * SUN_RIBBON_VERTEX_SIZE];
        float firstY = ribbonHull[first * SUN_RIBBON_VERTEX_SIZE + 1];
        float secondX = ribbonHull[second * SUN_RIBBON_VERTEX_SIZE];
        float secondY = ribbonHull[second * SUN_RIBBON_VERTEX_SIZE + 1];
        return (secondX - firstX) * (y - firstY)
            - (secondY - firstY) * (x - firstX);
    }

    private float candidateX(int index) {
        return ribbonCandidates[index * SUN_RIBBON_VERTEX_SIZE];
    }

    private float candidateY(int index) {
        return ribbonCandidates[index * SUN_RIBBON_VERTEX_SIZE + 1];
    }

    private float candidateHeight(int index) {
        return ribbonCandidates[index * SUN_RIBBON_VERTEX_SIZE + 2];
    }

    private void setHullPoint(int index, float x, float y, float height) {
        int offset = index * SUN_RIBBON_VERTEX_SIZE;
        ribbonHull[offset] = x;
        ribbonHull[offset + 1] = y;
        ribbonHull[offset + 2] = height;
    }

    private OpaqueBounds getOpaqueBounds(TextureRegion region) {
        Texture texture = region.getTexture();
        long key = getRegionKey(region);
        LongMap<OpaqueBounds> textureBounds = opaqueBoundsCache.get(texture);
        if (textureBounds != null) {
            OpaqueBounds cached = textureBounds.get(key);
            if (cached != null) {
                return cached;
            }
        } else {
            textureBounds = new LongMap<>();
            opaqueBoundsCache.put(texture, textureBounds);
        }

        OpaqueBounds bounds = readOpaqueBounds(region);
        textureBounds.put(key, bounds);
        return bounds;
    }

    private OpaqueBounds readOpaqueBounds(TextureRegion region) {
        Texture texture = region.getTexture();
        if (unreadableOpaqueTextures.contains(texture)) {
            return OpaqueBounds.full(region);
        }
        TextureData data = texture.getTextureData();
        if (data == null || data.getType() != TextureData.TextureDataType.Pixmap) {
            logUnreadableTexture(texture, "Texture data is not pixmap-backed");
            return OpaqueBounds.full(region);
        }

        Pixmap pixmap = null;
        try {
            if (!data.isPrepared()) {
                data.prepare();
            }
            pixmap = data.consumePixmap();
            int minimumX = region.getRegionWidth();
            int minimumY = region.getRegionHeight();
            int maximumX = -1;
            int maximumY = -1;
            int startX = region.getRegionX();
            int startY = region.getRegionY();
            for (int y = 0; y < region.getRegionHeight(); y++) {
                for (int x = 0; x < region.getRegionWidth(); x++) {
                    int alpha = pixmap.getPixel(startX + x, startY + y) & 0xff;
                    if (alpha / 255f < OPAQUE_ALPHA_THRESHOLD) {
                        continue;
                    }
                    minimumX = Math.min(minimumX, x);
                    minimumY = Math.min(minimumY, y);
                    maximumX = Math.max(maximumX, x);
                    maximumY = Math.max(maximumY, y);
                }
            }
            if (maximumX < minimumX || maximumY < minimumY) {
                return OpaqueBounds.full(region);
            }
            return new OpaqueBounds(
                minimumX, maximumX + 1f,
                region.getRegionHeight() - maximumY - 1f,
                region.getRegionHeight() - minimumY);
        } catch (RuntimeException exception) {
            logUnreadableTexture(texture,
                "Unable to read texture alpha: " + exception.getMessage());
            return OpaqueBounds.full(region);
        } finally {
            if (pixmap != null && data.disposePixmap()) {
                pixmap.dispose();
            }
        }
    }

    private void logUnreadableTexture(Texture texture, String message) {
        if (unreadableOpaqueTextures.add(texture)) {
            Gdx.app.log(TAG, message + "; using the full texture region");
        }
    }

    private long getRegionKey(TextureRegion region) {
        return ((long) region.getRegionX() & 0xffffL) << 48
            | ((long) region.getRegionY() & 0xffffL) << 32
            | ((long) region.getRegionWidth() & 0xffffL) << 16
            | ((long) region.getRegionHeight() & 0xffffL);
    }

    private void setTextureCoordinates(ShaderProgram shader, Render render,
                                       TextureRegion region) {
        Texture texture = region.getTexture();
        float inverseWidth = 1f / texture.getWidth();
        float inverseHeight = 1f / texture.getHeight();
        float left = region.getRegionX() * inverseWidth;
        float right = (region.getRegionX() + region.getRegionWidth()) * inverseWidth;
        float bottom = (region.getRegionY() + region.getRegionHeight())
            * inverseHeight;
        float top = region.getRegionY() * inverseHeight;
        if (render.flipX) {
            float swap = left;
            left = right;
            right = swap;
        }
        if (render.flipY) {
            float swap = bottom;
            bottom = top;
            top = swap;
        }
        shader.setUniformf("u_uvBottomLeft", left, bottom);
        shader.setUniformf("u_uvTopRight", right, top);
    }

    private boolean isCasterInRange(int entityId, TopDownShadowLight light) {
        Render render = mRender.get(entityId);
        if (!isRenderable(render)) {
            return false;
        }
        if (light.isDirectional()) {
            return true;
        }
        light.getShadowPosition(lightPosition);
        Pos pos = mPos.get(entityId);
        float centerX = worldScale * (pos.x + render.offsetX)
            + render.keyframe.getRegionWidth() * worldScale
            * render.scaleWidth * 0.5f;
        return lightPosition.dst(centerX, getFootY(entityId))
            <= light.getShadowRange() + getMaximumHeight(entityId);
    }

    private float getMaximumHeight(int entityId) {
        Render render = mRender.get(entityId);
        float drawBottom = worldScale
            * (mPos.get(entityId).y + render.offsetY + getSoarHeight(entityId));
        float drawHeight = render.keyframe.getRegionHeight()
            * Math.abs(worldScale * render.scaleHeight);
        return Math.max(0f, drawBottom + drawHeight - getFootY(entityId));
    }

    private float getShadowDepth(int entityId, TextureRegion region) {
        TopDownShadow shadow = mTopDownShadow.get(entityId);
        if (shadow.depth > 0f) {
            return shadow.depth * worldScale;
        }
        return region.getRegionWidth()
            * Math.abs(worldScale * mRender.get(entityId).scaleWidth) / 5f;
    }

    private void expandDepth(FrameBuffer source, FrameBuffer target) {
        downsampleDepth(source);
        target.begin();
        clearBuffer();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        depthDownsampleBuffer.getColorBufferTexture().bind(0);
        depthExpandShader.bind();
        depthExpandShader.setUniformi("u_source", 0);
        depthExpandShader.setUniformf("u_heightRange", config.getHeightRange());
        depthExpandShader.setUniformf("u_texelY", 1f / target.getHeight());
        depthExpandShader.setUniformf("u_worldPerTexelY",
            cameraSystem.camera.viewportHeight * cameraSystem.camera.zoom
                / target.getHeight());
        screenQuad.render(depthExpandShader, GL20.GL_TRIANGLE_FAN);
        target.end();
    }

    private void downsampleDepth(FrameBuffer source) {
        depthDownsampleBuffer.begin();
        clearBuffer();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        source.getColorBufferTexture().bind(0);
        depthDownsampleShader.bind();
        depthDownsampleShader.setUniformi("u_source", 0);
        depthDownsampleShader.setUniformf(
            "u_sourceTexelY", 1f / source.getHeight());
        screenQuad.render(depthDownsampleShader, GL20.GL_TRIANGLE_FAN);
        depthDownsampleBuffer.end();
    }

    private void drawEntity(int entityId) {
        Render render = mRender.get(entityId);
        if (!isRenderable(render)) {
            return;
        }
        Pos pos = mPos.get(entityId);
        float soarHeight = getSoarHeight(entityId);
        if (render.textureSheets != null && !render.textureSheets.isEmpty()) {
            for (int i = 0; i < render.textureSheets.size; i++) {
                TextureRegion region = render.textureSheets.get(i);
                if (region != null) {
                    drawRegion(render, pos, region,
                        soarHeight + i * render.sheetOffset);
                }
            }
            return;
        }
        drawRegion(render, pos, render.keyframe, soarHeight);
    }

    /** 使用与各层投影相同的纵深绘制接收遮罩。 */
    private void drawReceiverEntity(int entityId) {
        Render render = mRender.get(entityId);
        if (!isRenderable(render)) {
            return;
        }
        Pos pos = mPos.get(entityId);
        float soarHeight = getSoarHeight(entityId);
        if (render.textureSheets != null && !render.textureSheets.isEmpty()) {
            for (int i = 0; i < render.textureSheets.size; i++) {
                TextureRegion region = render.textureSheets.get(i);
                if (region != null) {
                    receiverShader.setUniformf(
                        "u_receiverDepth", getShadowDepth(entityId, region));
                    drawRegion(render, pos, region,
                        soarHeight + i * render.sheetOffset);
                    spriteBatch.flush();
                }
            }
            return;
        }
        receiverShader.setUniformf(
            "u_receiverDepth", getShadowDepth(entityId, render.keyframe));
        drawRegion(render, pos, render.keyframe, soarHeight);
        spriteBatch.flush();
    }

    private void drawRegion(Render render, Pos pos, TextureRegion region,
                            float extraY) {
        spriteBatch.draw(region.getTexture(),
            worldScale * (pos.x + render.offsetX),
            worldScale * (pos.y + render.offsetY + extraY),
            render.originX, render.originY,
            region.getRegionWidth(), region.getRegionHeight(),
            worldScale * render.scaleWidth,
            worldScale * render.scaleHeight,
            render.rotation,
            region.getRegionX(), region.getRegionY(),
            region.getRegionWidth(), region.getRegionHeight(),
            render.flipX, render.flipY);
    }

    private boolean isRenderable(Render render) {
        return render != null && render.visible && render.keyframe != null
            && render.keyframe.getTexture() != null;
    }

    private float getFootY(int entityId) {
        Pos pos = mPos.get(entityId);
        Render render = mRender.get(entityId);
        ZIndex zIndex = mZIndex.get(entityId);
        return worldScale * (pos.y + render.offsetY
            + getSoarHeight(entityId) + zIndex.offset);
    }

    private float getSoarHeight(int entityId) {
        return mSoarHeight.has(entityId) ? mSoarHeight.get(entityId).height : 0f;
    }

    private void compositeSunShadow() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        bindCompositeMasks(sunCompositeShader);
        sunCompositeShader.setUniformf(
            "u_shadowOpacity", config.getSunShadowOpacity());
        screenQuad.render(sunCompositeShader, GL20.GL_TRIANGLE_FAN);
        finishComposite();
    }

    private void compositePointLight() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_ONE, GL20.GL_ONE);
        receiverShadowMask.getColorBufferTexture().bind(3);
        entityMask.getColorBufferTexture().bind(2);
        groundShadowMask.getColorBufferTexture().bind(1);
        pointRayHandler.getLightMapTexture().bind(0);
        pointCompositeShader.bind();
        pointCompositeShader.setUniformi("u_lightMap", 0);
        pointCompositeShader.setUniformi("u_groundShadow", 1);
        pointCompositeShader.setUniformi("u_entityMask", 2);
        pointCompositeShader.setUniformi("u_receiverShadow", 3);
        setCompositeCommonUniforms(pointCompositeShader);
        screenQuad.render(pointCompositeShader, GL20.GL_TRIANGLE_FAN);
        finishComposite();
    }

    private void bindCompositeMasks(ShaderProgram shader) {
        receiverShadowMask.getColorBufferTexture().bind(2);
        entityMask.getColorBufferTexture().bind(1);
        groundShadowMask.getColorBufferTexture().bind(0);
        shader.bind();
        shader.setUniformi("u_groundShadow", 0);
        shader.setUniformi("u_entityMask", 1);
        shader.setUniformi("u_receiverShadow", 2);
        setCompositeCommonUniforms(shader);
    }

    private void setCompositeCommonUniforms(ShaderProgram shader) {
        shader.setUniformf("u_shadowTexel",
            1f / bufferWidth, 1f / bufferHeight);
        shader.setUniformf("u_time", shadowTime);
    }

    private void finishComposite() {
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void resizeBuffersIfNeeded() {
        int width = Math.max(1, Math.round(
            Gdx.graphics.getBackBufferWidth() * config.getResolutionScale()));
        int height = Math.max(1, Math.round(
            Gdx.graphics.getBackBufferHeight() * config.getResolutionScale()));
        if (width == bufferWidth && height == bufferHeight) {
            return;
        }
        disposeBuffers();
        bufferWidth = width;
        bufferHeight = height;
        depthBufferHeight = Math.max(1, (height + 1) / 2);
        pointRayHandler.resizeFBO(width, height);
        depthDownsampleBuffer = createDepthBuffer(Texture.TextureFilter.Nearest);
        entityMask = createBuffer(Texture.TextureFilter.Nearest);
        groundShadowSource = createBuffer(Texture.TextureFilter.Nearest);
        groundShadowMask = createDepthBuffer(Texture.TextureFilter.Linear);
        receiverShadowMask = createBuffer(Texture.TextureFilter.Linear);
    }

    private FrameBuffer createBuffer(Texture.TextureFilter filter) {
        return createBuffer(bufferHeight, filter);
    }

    private FrameBuffer createDepthBuffer(Texture.TextureFilter filter) {
        return createBuffer(depthBufferHeight, filter);
    }

    private FrameBuffer createBuffer(int height, Texture.TextureFilter filter) {
        FrameBuffer buffer = new FrameBuffer(
            Pixmap.Format.RGBA8888, bufferWidth, height, false);
        buffer.getColorBufferTexture().setFilter(filter, filter);
        return buffer;
    }

    private void disposeBuffers() {
        if (entityMask != null) {
            depthDownsampleBuffer.dispose();
            entityMask.dispose();
            groundShadowSource.dispose();
            groundShadowMask.dispose();
            receiverShadowMask.dispose();
        }
    }

    private void clearBuffer() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private Mesh createScreenQuad() {
        Mesh mesh = new Mesh(true, 4, 0,
            new VertexAttribute(VertexAttributes.Usage.Position, 2, "a_position"),
            new VertexAttribute(
                VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoord"));
        mesh.setVertices(new float[]{
            -1f, -1f, 0f, 0f,
            1f, -1f, 1f, 0f,
            1f, 1f, 1f, 1f,
            -1f, 1f, 0f, 1f
        });
        return mesh;
    }

    private Mesh createProjectedShadowMesh(int segments) {
        int vertexCount = (segments + 1) * 2;
        Mesh mesh = new Mesh(true, vertexCount, 0,
            new VertexAttribute(VertexAttributes.Usage.Position, 2, "a_position"),
            new VertexAttribute(
                VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoord"));
        float[] vertices = new float[vertexCount * 4];
        int offset = 0;
        for (int i = 0; i <= segments; i++) {
            float vertical = i / (float) segments;
            vertices[offset++] = 0f;
            vertices[offset++] = vertical;
            vertices[offset++] = 0f;
            vertices[offset++] = vertical;
            vertices[offset++] = 1f;
            vertices[offset++] = vertical;
            vertices[offset++] = 1f;
            vertices[offset++] = vertical;
        }
        mesh.setVertices(vertices);
        return mesh;
    }

    private Mesh createSunShadowRibbonMesh() {
        return new Mesh(false, SUN_RIBBON_MAX_VERTICES, 0,
            new VertexAttribute(VertexAttributes.Usage.Position, 2, "a_position"),
            new VertexAttribute(VertexAttributes.Usage.Generic, 1,
                "a_casterHeight"));
    }

    private ShaderProgram compileShader(String vertex, String fragment,
                                        String name) {
        if (vertex == null || fragment == null) {
            throw new IllegalStateException(name + " shader source is missing");
        }
        ShaderProgram shader = new ShaderProgram(vertex, fragment);
        if (!shader.isCompiled()) {
            Gdx.app.error(TAG, name + " shader compile failed: " + shader.getLog());
            shader.dispose();
            throw new IllegalStateException(name + " shader compile failed");
        }
        return shader;
    }

    public TopDownSunLight getSunLight() {
        return sunLight;
    }

    @Override
    protected void dispose() {
        disposeBuffers();
        if (pointRayHandler != null) {
            pointRayHandler.dispose();
        }
        if (spriteBatch != null) {
            spriteBatch.dispose();
            screenQuad.dispose();
            sunProjectedShadowMesh.dispose();
            projectedShadowMesh.dispose();
            sunShadowRibbonMesh.dispose();
            entityMaskShader.dispose();
            projectedShadowShader.dispose();
            sunShadowRibbonShader.dispose();
            receiverShader.dispose();
            sunCompositeShader.dispose();
            pointCompositeShader.dispose();
            depthDownsampleShader.dispose();
            depthExpandShader.dispose();
        }
    }

    private static final class OpaqueBounds {
        private final float left;
        private final float right;
        private final float bottom;
        private final float top;

        private OpaqueBounds(float left, float right, float bottom, float top) {
            this.left = left;
            this.right = right;
            this.bottom = bottom;
            this.top = top;
        }

        private static OpaqueBounds full(TextureRegion region) {
            return new OpaqueBounds(0f, region.getRegionWidth(), 0f,
                region.getRegionHeight());
        }
    }
}

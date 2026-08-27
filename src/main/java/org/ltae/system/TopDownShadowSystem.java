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
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import org.ltae.component.Inert;
import org.ltae.component.Pos;
import org.ltae.component.Render;
import org.ltae.component.SoarHeight;
import org.ltae.component.TopDownShadow;
import org.ltae.component.ZIndex;
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
    private static final int GL_MAX_BLEND_EQUATION = 0x8008;
    private static final String SHADER_PATH = "shader/topdown/";

    private final float worldScale;
    private final TopDownShadowConfig config;
    private final IntArray sortedShadowEntities = new IntArray();
    private final Vector2 lightDirection = new Vector2();
    private final Vector2 lightPosition = new Vector2();
    private final IntBuffer glStateBuffer = BufferUtils.newIntBuffer(1);
    private final int[] previousTextureBindings = new int[4];

    private B2dSystem b2dSystem;
    private CameraSystem cameraSystem;
    private M<Pos> mPos;
    private M<Render> mRender;
    private M<ZIndex> mZIndex;
    private M<SoarHeight> mSoarHeight;
    private M<org.ltae.component.TopDownPointLight> mTopDownPointLight;

    private EntitySubscription renderSubscription;
    private EntitySubscription shadowSubscription;
    private EntitySubscription pointLightSubscription;
    private RayHandler pointRayHandler;
    private TopDownSunLight sunLight;
    private SpriteBatch spriteBatch;
    private Mesh screenQuad;
    private Mesh projectedShadowMesh;
    private FrameBuffer heightMap;
    private FrameBuffer entityMask;
    private FrameBuffer groundShadowMask;
    private FrameBuffer receiverShadowMask;
    private ShaderProgram heightMapShader;
    private ShaderProgram entityMaskShader;
    private ShaderProgram projectedShadowShader;
    private ShaderProgram receiverShader;
    private ShaderProgram sunCompositeShader;
    private ShaderProgram pointCompositeShader;
    private int bufferWidth;
    private int bufferHeight;
    private float shadowTime;
    private float sunVisibility = 1f;

    public TopDownShadowSystem(float worldScale, TopDownShadowConfig config) {
        if (worldScale <= 0f) {
            throw new IllegalArgumentException("worldScale must be greater than zero");
        }
        if (config == null) {
            throw new IllegalArgumentException("config cannot be null");
        }
        this.worldScale = worldScale;
        this.config = config;
    }

    @Override
    protected void initialize() {
        renderSubscription = world.getAspectSubscriptionManager().get(
            Aspect.all(Render.class, Pos.class, ZIndex.class).exclude(Inert.class));
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
        sunLight = new TopDownSunLight(pointRayHandler,
            config.getSunDirectionDegree(), config.getHeightRange());

        spriteBatch = new SpriteBatch();
        screenQuad = createScreenQuad();
        projectedShadowMesh = createProjectedShadowMesh();
        ShaderProgram.pedantic = false;
        ShaderManager shaderManager = ShaderManager.getInstance();
        heightMapShader = compileShader(
            shaderManager.getVertexContext(SHADER_PATH + "sprite"),
            shaderManager.getFragmentContext(SHADER_PATH + "height_map"),
            "Height map");
        entityMaskShader = compileShader(
            shaderManager.getVertexContext(SHADER_PATH + "sprite"),
            shaderManager.getFragmentContext(SHADER_PATH + "entity_mask"),
            "Entity mask");
        projectedShadowShader = compileShader(
            shaderManager.getVertexContext(SHADER_PATH + "projected_shadow"),
            shaderManager.getFragmentContext(SHADER_PATH + "projected_shadow"),
            "Projected shadow");
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
        resizeBuffersIfNeeded();
        Gdx.app.log(TAG, "Top-down shadow system initialized");
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
            renderHeightMap();

            renderShadowMasks(sunLight);
            compositeSunShadow();
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
            sortedShadowEntities.add(ids[i]);
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
        IntBag entities = renderSubscription.getEntities();
        int[] ids = entities.getData();
        for (int i = 0; i < entities.size(); i++) {
            drawEntity(ids[i]);
        }
        spriteBatch.end();
        spriteBatch.setShader(null);
        Gdx.gl.glBlendEquation(GL20.GL_FUNC_ADD);
        Gdx.gl.glDisable(GL20.GL_BLEND);
        entityMask.end();
    }

    private void renderHeightMap() {
        heightMap.begin();
        clearBuffer();
        spriteBatch.setProjectionMatrix(cameraSystem.camera.combined);
        spriteBatch.setShader(heightMapShader);
        spriteBatch.disableBlending();
        spriteBatch.begin();
        heightMapShader.setUniformi("u_texture", 0);
        heightMapShader.setUniformf("u_heightRange", config.getHeightRange());
        for (int i = 0; i < sortedShadowEntities.size; i++) {
            int entityId = sortedShadowEntities.get(i);
            heightMapShader.setUniformf("u_footY", getFootY(entityId));
            setReceiverId(heightMapShader, entityId);
            drawEntity(entityId);
            spriteBatch.flush();
        }
        spriteBatch.end();
        spriteBatch.enableBlending();
        spriteBatch.setShader(null);
        heightMap.end();
    }

    private void renderShadowMasks(TopDownShadowLight light) {
        renderGroundShadowMask(light);
        renderReceiverShadowMask(light);
    }

    private void renderGroundShadowMask(TopDownShadowLight light) {
        groundShadowMask.begin();
        clearBuffer();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendEquation(GL_MAX_BLEND_EQUATION);
        Gdx.gl.glBlendFunc(GL20.GL_ONE, GL20.GL_ONE);
        projectedShadowShader.bind();
        projectedShadowShader.setUniformi("u_texture", 0);
        projectedShadowShader.setUniformMatrix(
            "u_projTrans", cameraSystem.camera.combined);
        setLightUniforms(projectedShadowShader, light);
        for (int i = 0; i < sortedShadowEntities.size; i++) {
            int entityId = sortedShadowEntities.get(i);
            if (!isCasterInRange(entityId, light)) {
                continue;
            }
            renderProjectedEntity(entityId);
        }
        Gdx.gl.glBlendEquation(GL20.GL_FUNC_ADD);
        Gdx.gl.glDisable(GL20.GL_BLEND);
        groundShadowMask.end();
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
        receiverShader.setUniformi("u_heightMap", 1);
        receiverShader.setUniformMatrix(
            "u_projTrans", cameraSystem.camera.combined);
        receiverShader.setUniformf("u_heightRange", config.getHeightRange());
        receiverShader.setUniformf("u_time", shadowTime);
        setLightUniforms(receiverShader, light);
        heightMap.getColorBufferTexture().bind(1);
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        for (int i = 0; i < sortedShadowEntities.size; i++) {
            int entityId = sortedShadowEntities.get(i);
            receiverShader.setUniformf("u_footY", getFootY(entityId));
            setReceiverId(receiverShader, entityId);
            drawEntity(entityId);
            spriteBatch.flush();
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
        light.getShadowPosition(lightPosition);
        shader.setUniformf("u_lightPosition", lightPosition);
        shader.setUniformf("u_lightHeight", light.getShadowHeight());
        if (shader.hasUniform("u_lightRange")) {
            shader.setUniformf("u_lightRange", light.getShadowRange());
        }
    }

    private void renderProjectedEntity(int entityId) {
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
                    renderProjectedRegion(render, pos, region,
                        soarHeight + i * render.sheetOffset, getFootY(entityId));
                }
            }
            return;
        }
        renderProjectedRegion(
            render, pos, render.keyframe, soarHeight, getFootY(entityId));
    }

    private void renderProjectedRegion(Render render, Pos pos,
                                       TextureRegion region, float extraY,
                                       float footY) {
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
        setTextureCoordinates(projectedShadowShader, render, region);
        texture.bind(0);
        projectedShadowMesh.render(
            projectedShadowShader, GL20.GL_TRIANGLE_STRIP);
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

    private void setReceiverId(ShaderProgram shader, int entityId) {
        int encodedId = entityId + 1;
        shader.setUniformf("u_receiverId",
            (encodedId & 0xff) / 255f,
            ((encodedId >>> 8) & 0xff) / 255f);
    }

    private void compositeSunShadow() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        bindCompositeMasks(sunCompositeShader);
        sunCompositeShader.setUniformf(
            "u_shadowOpacity", config.getSunShadowOpacity() * sunVisibility);
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
            1f / groundShadowMask.getWidth(),
            1f / groundShadowMask.getHeight());
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
        pointRayHandler.resizeFBO(width, height);
        heightMap = createBuffer(Texture.TextureFilter.Nearest);
        entityMask = createBuffer(Texture.TextureFilter.Nearest);
        groundShadowMask = createBuffer(Texture.TextureFilter.Linear);
        receiverShadowMask = createBuffer(Texture.TextureFilter.Linear);
    }

    private FrameBuffer createBuffer(Texture.TextureFilter filter) {
        FrameBuffer buffer = new FrameBuffer(
            Pixmap.Format.RGBA8888, bufferWidth, bufferHeight, false);
        buffer.getColorBufferTexture().setFilter(filter, filter);
        return buffer;
    }

    private void disposeBuffers() {
        if (heightMap != null) {
            heightMap.dispose();
            entityMask.dispose();
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

    private Mesh createProjectedShadowMesh() {
        int vertexCount = (SHADOW_SEGMENTS + 1) * 2;
        Mesh mesh = new Mesh(true, vertexCount, 0,
            new VertexAttribute(VertexAttributes.Usage.Position, 2, "a_position"),
            new VertexAttribute(
                VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoord"));
        float[] vertices = new float[vertexCount * 4];
        int offset = 0;
        for (int i = 0; i <= SHADOW_SEGMENTS; i++) {
            float vertical = i / (float) SHADOW_SEGMENTS;
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

    public void setSunVisibility(float sunVisibility) {
        this.sunVisibility = MathUtils.clamp(sunVisibility, 0f, 1f);
    }

    public float getSunVisibility() {
        return sunVisibility;
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
            projectedShadowMesh.dispose();
            heightMapShader.dispose();
            entityMaskShader.dispose();
            projectedShadowShader.dispose();
            receiverShader.dispose();
            sunCompositeShader.dispose();
            pointCompositeShader.dispose();
        }
    }
}

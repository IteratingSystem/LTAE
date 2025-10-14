package org.ltae.system;

import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import org.ltae.component.LayerSampling;
import org.ltae.component.Pos;
import org.ltae.component.Render;
import org.ltae.utils.SamplingUtil;

@All({LayerSampling.class, Render.class, Pos.class})
public class LayerSamplingSystem extends IteratingSystem {
    private RenderTiledSystem renderTiledSystem;
    private TiledMapSystem tiledMapSystem;
    private CameraSystem cameraSystem;

    private M<Render> mRender;
    private M<LayerSampling> mSampling;
    private M<Pos> mPos;


    @Override
    protected void process(int entityId) {
        LayerSampling sampling = mSampling.get(entityId);
        if (sampling.isSampled && !sampling.update){
            return;
        }
        TextureRegion textureRegion = SamplingUtil.getInstance().samplingLayer(tiledMapSystem.getTiledMap(),sampling.layerName);

        Render render = mRender.get(entityId);
        render.keyframe = textureRegion;
        sampling.isSampled = true;
    }
}

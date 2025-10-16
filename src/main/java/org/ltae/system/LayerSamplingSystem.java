package org.ltae.system;

import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.Array;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import org.ltae.component.LayerSampling;
import org.ltae.component.Pos;
import org.ltae.component.Render;
import org.ltae.component.TileAnimation;
import org.ltae.utils.SamplingUtil;

@All({LayerSampling.class, Render.class, Pos.class})
public class LayerSamplingSystem extends IteratingSystem {
    private RenderTiledSystem renderTiledSystem;
    private TiledMapSystem tiledMapSystem;
    private CameraSystem cameraSystem;

    private M<Render> mRender;
    private M<LayerSampling> mSampling;
    private M<Pos> mPos;
    private M<TileAnimation> mTileAnimation;

    private float totalTime;

    @Override
    protected void initialize() {
        super.initialize();
        totalTime = 0f;
    }

    @Override
    protected void process(int entityId) {
        totalTime += world.getDelta();
        LayerSampling sampling = mSampling.get(entityId);

        //采样完成
        if (sampling.crtNum == sampling.needNum){
            //如果帧数大于1并且没有动画组件,则创建动画组件,反向判断
            if (sampling.needNum == 1 || mTileAnimation.has(entityId)){
                return;
            }

            Array<StaticTiledMapTile> staticTiledMapTiles = new Array<>();
            for (TextureRegion region : sampling.regions) {
                staticTiledMapTiles.add(new StaticTiledMapTile(region));
            }
            AnimatedTiledMapTile animatedTiledMapTile = new AnimatedTiledMapTile(sampling.interval,staticTiledMapTiles);

            TileAnimation tileAnimation = mTileAnimation.create(entityId);
            tileAnimation.initialize(animatedTiledMapTile, Animation.PlayMode.LOOP,0,0);
            return;
        }

        //采样时间
        float samplingTime = sampling.crtNum * sampling.interval/60f;
        //没到时间则不采样

        if (totalTime < samplingTime){
            return;
        }
        //采样
        TextureRegion textureRegion = SamplingUtil.getInstance().samplingLayer(tiledMapSystem.getTiledMap(),sampling.layerName);
        sampling.regions.add(textureRegion);
        Render render = mRender.get(entityId);
        render.keyframe = textureRegion;
        //一次采样完成
        sampling.crtNum++;
    }
}

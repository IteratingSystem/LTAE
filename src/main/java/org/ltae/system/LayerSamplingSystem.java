package org.ltae.system;

import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
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
    private TotalTimeSystem totalTimeSystem;

    private M<Render> mRender;
    private M<LayerSampling> mSampling;
    private M<Pos> mPos;
    private M<TileAnimation> mTileAnimation;


    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    protected void process(int entityId) {
        LayerSampling sampling = mSampling.get(entityId);

        //采样完成
        if (sampling.isSampled()){
            //如果帧数大于1并且没有动画组件,则创建动画组件,反向判断
            if (sampling.needNum == 1 || mTileAnimation.has(entityId)){
                return;
            }

            Array<StaticTiledMapTile> staticTiledMapTiles = new Array<>();
            for (TextureRegion region : sampling.regions) {
                staticTiledMapTiles.add(new StaticTiledMapTile(region));
            }
            AnimatedTiledMapTile animatedTiledMapTile = new AnimatedTiledMapTile(sampling.interval/1000f,staticTiledMapTiles);

            TileAnimation tileAnimation = mTileAnimation.create(entityId);
            tileAnimation.initialize(animatedTiledMapTile, Animation.PlayMode.LOOP,0,0);
            return;
        }

        //当前时间会采样到第n帧
        float intervalSeconds = sampling.interval / 1000f;
        int n = (int)(totalTimeSystem.totalTime / intervalSeconds) % sampling.needNum;

        //判断当前帧是否采样,已采样则跳过
        if(sampling.regions[n] != null){
            return;
        }
        //采样
        TextureRegion textureRegion = SamplingUtil.getInstance().samplingLayer(tiledMapSystem.getTiledMap(),sampling.layerName);
        sampling.regions[n] = textureRegion;
        Render render = mRender.get(entityId);
        render.keyframe = textureRegion;
        //一次采样完成
        sampling.crtNum++;
    }
}

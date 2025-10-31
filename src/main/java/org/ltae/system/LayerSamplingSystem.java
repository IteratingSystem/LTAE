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
    private TiledMapSystem tiledMapSystem;

    private M<Render> mRender;
    private M<LayerSampling> mSampling;
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
            //如果如果没有动画瓦片或者已经有动画组件则直接返回,否则生成动画组件
            if (sampling.flagAnimTile == null || sampling.isCreateAnim){
                return;
            }
            Array<StaticTiledMapTile> staticTiledMapTiles = new Array<>();
            for (TextureRegion region : sampling.regions) {
                staticTiledMapTiles.add(new StaticTiledMapTile(region));
            }
            AnimatedTiledMapTile animatedTiledMapTile = new AnimatedTiledMapTile(sampling.flagAnimTile.getAnimationIntervals()[0]/1000f,staticTiledMapTiles);

            mTileAnimation.remove(entityId);
            TileAnimation tileAnimation = mTileAnimation.create(entityId);
            tileAnimation.initialize(animatedTiledMapTile, Animation.PlayMode.LOOP,0,0);
            sampling.isCreateAnim = true;
            return;
        }

        //静态图层采样
        if (sampling.flagAnimTile == null){
            TextureRegion textureRegion = SamplingUtil.getInstance().samplingLayer(tiledMapSystem.getTiledMap(),sampling.layerName);
            sampling.regions[0] = textureRegion;
            Render render = mRender.get(entityId);
            render.keyframe = textureRegion;
            return;
        }

        //动画图层采样
        int currentFrameIndex = sampling.flagAnimTile.getCurrentFrameIndex();
        if (sampling.regions[currentFrameIndex] != null) {
            return;
        }
        TextureRegion textureRegion = SamplingUtil.getInstance().samplingLayer(tiledMapSystem.getTiledMap(),sampling.layerName);
        sampling.regions[currentFrameIndex] = textureRegion;
        Render render = mRender.get(entityId);
        render.keyframe = textureRegion;
    }
}

package org.ltae.component;

import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import org.ltae.manager.map.MapManager;
import org.ltae.manager.map.serialize.SerializeParam;
import org.ltae.manager.map.serialize.json.EntityData;


/**
 * 图层采样组件
 */
public class LayerSampling extends SerializeComponent {
    private static final String TAG = LayerSampling.class.getSimpleName();
    @SerializeParam
    public String layerName;

    //已采样纹理
    public TextureRegion[] regions;
    public AnimatedTiledMapTile flagAnimTile;
    public boolean isCreateAnim = false;

    @Override
    public void reload(World world, EntityData entityData) {
        super.reload(world, entityData);

        //获取地图
        TiledMap tiledMap = MapManager.getInstance().getTiledMap(entityData.fromMap);
        //获取图层
        TiledMapTileLayer mapLayer = null;
        try{
            mapLayer = (TiledMapTileLayer)tiledMap.getLayers().get(layerName);
        } catch (Exception e) {
            Gdx.app.error(TAG,"Failed to samplingLayer,layerName is '"+layerName+"'");
            throw new RuntimeException(e);
        }
        //便利所有的块,找出帧数最高的AnimaTile
        int width = mapLayer.getWidth();
        int height = mapLayer.getHeight();
        for (int i = 0; i < width; i++) {
            for (int i1 = 0; i1 < height; i1++) {
                TiledMapTileLayer.Cell cell = mapLayer.getCell(i, i1);
                if (cell == null){
                    continue;
                }
                TiledMapTile tile = cell.getTile();
                if (tile instanceof AnimatedTiledMapTile animatedTiledMapTile) {
                    if (flagAnimTile == null || flagAnimTile.getAnimationIntervals().length < animatedTiledMapTile.getAnimationIntervals().length){
                        flagAnimTile = animatedTiledMapTile;
                        continue;
                    }
                }
            }
        }
        if (flagAnimTile != null){
            regions = new TextureRegion[flagAnimTile.getAnimationIntervals().length];
        }else {
            regions = new TextureRegion[1];
        }
    }

    //是否已经采样完成
    public boolean isSampled(){
        if (flagAnimTile == null){
            return regions[0] != null;
        }
        for (int i = 0; i < flagAnimTile.getAnimationIntervals().length; i++) {
            if (regions[i] == null) {
                return false;
            }
        }
        return true;
    }
}

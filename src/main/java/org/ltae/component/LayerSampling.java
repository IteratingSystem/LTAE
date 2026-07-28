package org.ltae.component;

import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import org.ltae.component.parent.SerializeComponent;
import org.ltae.manager.map.MapManager;
import org.ltae.serialize.PostLoad;
import org.ltae.serialize.SerializeParam;
import org.ltae.serialize.data.EntityDatum;


/**
 * 图层采样组件
 */
public class LayerSampling extends SerializeComponent {
    private static final String TAG = LayerSampling.class.getSimpleName();
    @SerializeParam
    public String layerName;

    //已采样纹理
    public TextureRegion[] regions;
    public transient AnimatedTiledMapTile flagAnimTile;
    public boolean isCreateAnim;

    @Override
    public void write(Json json) {
        super.write(json);
        json.writeValue("layerName", layerName);
        json.writeValue("isCreateAnim", isCreateAnim);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        layerName = jsonData.has("layerName") ? jsonData.getString("layerName") : null;
        isCreateAnim = jsonData.getBoolean("isCreateAnim", false);
    }

    @PostLoad
    public void postLoadLayerSampling(World world) {
        isCreateAnim = false;
        if (fromMap == null) return;

        TiledMap tiledMap = MapManager.getInstance().getTiledMap(fromMap);
        if (tiledMap == null) return;

        TiledMapTileLayer mapLayer = null;
        try{
            mapLayer = (TiledMapTileLayer)tiledMap.getLayers().get(layerName);
        } catch (Exception e) {
            Gdx.app.error(TAG,"Failed to samplingLayer,layerName is '"+layerName+"'");
            throw new RuntimeException(e);
        }
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

package org.ltae.component;

import com.artemis.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.utils.ObjectMap;
import org.ltae.tiled.TileCompLoader;
import org.ltae.tiled.TileDetails;
import org.ltae.tiled.TileParam;

import java.util.Iterator;

/**
 * @Auther WenLong
 * @Date 2025/3/11 15:44
 * @Description 多动画组件(瓦片动画)
 **/
public class TileAnimations extends Component implements TileCompLoader {
    private final static String TAG = TileAnimations.class.getSimpleName();
    public ObjectMap<String,TileAnimation> table;
    public float stateTime;

    @TileParam
    public String current;
    @Override
    public void loader(TileDetails tileDetails) {
        table = new ObjectMap<>();
        stateTime = 0;
        TiledMapTile tiledMapTile = tileDetails.tiledMapTile;
        int tileId = tiledMapTile.getId();

        for (TiledMapTileSet tileSet : tileDetails.tiledMap.getTileSets()) {
            if (tileSet.getTile(tileId) != tiledMapTile) {
                continue;
            }
            Iterator<TiledMapTile> tileSetItr = tileSet.iterator();
            while (tileSetItr.hasNext()){
                TiledMapTile tile = tileSetItr.next();
                if (!(tile instanceof AnimatedTiledMapTile animatedTile)){
                    continue;
                }
                MapProperties props = animatedTile.getProperties();
                MapProperties animProp = props.get("TileAnimation",MapProperties.class);
                String name = animProp.get("name", String.class);
                String playModeName = animProp.get("playModeName", String.class);
                TileAnimation tileAnimation = new TileAnimation();
                tileAnimation.initialize(animatedTile,playModeName);
                if (table.containsKey(name)){
                    Gdx.app.log(TAG,"Repetitive animation naming:"+name);
                    continue;
                }
                table.put(name,tileAnimation);
            }
        }
    }

    public TextureRegion getKeyFrame() {
        TileAnimation tileAnimation = table.get(current);
        int frameNumber = tileAnimation.getKeyFrameIndex(stateTime);
        return tileAnimation.keyFrames[frameNumber];
    }
    public void changeAnimation(String animationName){
        current = animationName;
        stateTime = 0;
    }
}

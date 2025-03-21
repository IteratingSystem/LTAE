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

    private boolean flipX;
    private boolean flipY;

    @TileParam
    public String current;
    @Override
    public void loader(TileDetails tileDetails) {
        table = new ObjectMap<>();
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
                if (!props.containsKey("TileAnimation")) {
                    continue;
                }
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

    public TileAnimation getTileAnimation(){
        return table.get(current);
    }
    public TextureRegion getKeyFrame() {
        TileAnimation tileAnimation = getTileAnimation();
        return tileAnimation.getKeyFrame();
    }
    public void changeAnimation(String animationName){
        current = animationName;
        TileAnimation tileAnimation = getTileAnimation();
        tileAnimation.stateTime = 0;
    }
    public boolean isAnimationFinished(){
        TileAnimation tileAnimation = getTileAnimation();
        return tileAnimation.isAnimationFinished();
    }
    public boolean isLast(int lastCount){
        TileAnimation tileAnimation = getTileAnimation();
        return tileAnimation.isLast(lastCount);
    }
    public void flip(boolean flipX,boolean flipY){
        this.flipX = flipX;
        this.flipY = flipY;
        getKeyFrame().flip(flipX,flipY);
    }
    public boolean isFlipX(){
        return flipX;
    }
    public boolean isFlipY(){
        return flipY;
    }
}

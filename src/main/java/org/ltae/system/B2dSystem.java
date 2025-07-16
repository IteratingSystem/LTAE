package org.ltae.system;

import com.artemis.BaseSystem;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import dev.lyze.projectTrianglePlatforming.TiledTileCollisionToBox2d;
import dev.lyze.projectTrianglePlatforming.TiledTileCollisionToBox2dOptions;
import net.mostlyoriginal.api.event.common.Subscribe;
import org.ltae.box2d.listener.DefContactListener;
import org.ltae.event.B2dEvent;


/**
 * @Auther WenLong
 * @Date 2025/2/12 15:44
 * @Description Box2D物理系统
 **/
public class B2dSystem extends BaseSystem {
    private TiledMapSystem tiledMapSystem;
    private float worldScale;
    private float gx;   //横向重力
    private float gy;   //纵向重力
    private boolean doSleep;
    private boolean combineTileCollisions;
    public World box2DWorld;
    public TiledTileCollisionToBox2d tiledTileCollisionToBox2d;

    public B2dSystem(float gx,float gy,boolean doSleep,float worldScale,boolean combineTileCollisions){
        this.worldScale = worldScale;
        this.gx = gx;
        this.gy = gy;
        this.doSleep = doSleep;
        this.combineTileCollisions = combineTileCollisions;
    }
    @Override
    protected void initialize() {
        box2DWorld = new World(new Vector2(gx,gy),doSleep);
        box2DWorld.setContinuousPhysics(true);
        box2DWorld.setContactListener(new DefContactListener());
        updateMapShape();
    }

    public void updateMapShape(){
        if (tiledTileCollisionToBox2d == null){
            //绑定tiled中的物理形状
            tiledTileCollisionToBox2d = new TiledTileCollisionToBox2d(TiledTileCollisionToBox2dOptions.builder()
                    .scale(worldScale)
                    .combineTileCollisions(combineTileCollisions)
                    .triangulateInsteadOfThrow(true)
                    .build());
        }
        MapLayer phyLayer = tiledMapSystem.getPhyLayer();
        if (phyLayer instanceof TiledMapTileLayer tileLayer) {
            tiledTileCollisionToBox2d.parseLayer(tileLayer,box2DWorld);
        }
    }
    private void delAllBodies(){
        box2DWorld.step(world.delta, 6, 2);
        Array<Body> bodies = new Array<>();
        box2DWorld.getBodies(bodies);
        for (Body body : bodies) {
            box2DWorld.destroyBody(body);
        }
        world.process();
    }
    @Override
    protected void processSystem() {
        box2DWorld.step(world.delta,6,2);
    }
    @Subscribe
    public void onEvent(B2dEvent event){
        if (event.type == B2dEvent.DELETE_ALL_BODIES){
            delAllBodies();
            return;
        }
    }
}

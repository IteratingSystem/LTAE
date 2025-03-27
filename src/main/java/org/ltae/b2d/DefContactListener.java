package org.ltae.b2d;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.physics.box2d.*;
import org.ltae.component.TileAnimation;

/**
 * @Auther WenLong
 * @Date 2025/3/27 16:47
 * @Description 自定义碰撞监听
 **/
public class DefContactListener implements ContactListener {
    /**
     * 碰撞之前
     * @param contact
     * @param manifold
     */
    @Override
    public void preSolve(Contact contact, Manifold manifold) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        Object userDataA = fixtureA.getUserData();
        Object userDataB = fixtureB.getUserData();

        //判断碰撞时是否是指定的动画帧--start--
        if (userDataA instanceof KeyframeFixData keyframeFixData){
            Entity entity = keyframeFixData.entity;
            int entityId = keyframeFixData.entityId;
            World world = entity.getWorld();
            String aniName = keyframeFixData.aniName;
            int keyframeIndex = keyframeFixData.keyframeIndex;

            ComponentMapper<TileAnimation> mTileAnimation = world.getMapper(TileAnimation.class);
            if (mTileAnimation.has(entityId)) {
                TileAnimation tileAnimation = mTileAnimation.get(entityId);
                if (!tileAnimation.name.equals(aniName)) {
                    contact.setEnabled(false);
                    return;
                }
                if (tileAnimation.getKeyframeIndex() != keyframeIndex) {
                    contact.setEnabled(false);
                    return;
                }
            }
        }
        if (userDataB instanceof KeyframeFixData keyframeFixData){
            Entity entity = keyframeFixData.entity;
            int entityId = keyframeFixData.entityId;
            World world = entity.getWorld();
            String aniName = keyframeFixData.aniName;
            int keyframeIndex = keyframeFixData.keyframeIndex;

            ComponentMapper<TileAnimation> mTileAnimation = world.getMapper(TileAnimation.class);
            if (mTileAnimation.has(entityId)) {
                TileAnimation tileAnimation = mTileAnimation.get(entityId);
                if (!tileAnimation.name.equals(aniName)) {
                    contact.setEnabled(false);
                    return;
                }
                if (tileAnimation.getKeyframeIndex() != keyframeIndex) {
                    contact.setEnabled(false);
                    return;
                }
            }
        }
        //判断碰撞时是否是指定的动画帧--end--
    }

    @Override
    public void beginContact(Contact contact) {

    }

    @Override
    public void endContact(Contact contact) {
    }


    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {

    }
}

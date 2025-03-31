package org.ltae.b2d;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * @Auther WenLong
 * @Date 2025/3/28 10:49
 * @Description 包含实体数据的监听器,需要注意的是,传感器只会触发beginContact和endContact回调
 **/
public class EcsContactListener implements ContactListener {
    public Entity entity;
    public World world;
    public EcsContactListener(Entity entity){
        this.entity = entity;
        world = entity.getWorld();
    }

    /**
     * 开始碰撞
     * @param contact
     */
    @Override
    public void beginContact(Contact contact) {

    }

    /**
     * 结束碰撞
     * @param contact
     */
    @Override
    public void endContact(Contact contact) {

    }

    /**
     * 碰撞前处理
     * @param contact
     * @param manifold
     */
    @Override
    public void preSolve(Contact contact, Manifold manifold) {

    }

    /**
     * 碰撞后处理
     * @param contact
     * @param contactImpulse
     */
    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {

    }
}

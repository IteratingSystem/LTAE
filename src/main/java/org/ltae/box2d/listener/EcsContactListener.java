package org.ltae.box2d.listener;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.physics.box2d.*;
import org.ltae.box2d.DefFixData;

/**
 * @Auther WenLong
 * @Date 2025/3/28 10:49
 * @Description 包含实体数据的监听器,需要注意的是,传感器只会触发beginContact和endContact回调
 **/
public class EcsContactListener implements ContactListener {
    public int entityId;
    public Entity entity;
    public World world;

    public Fixture thisFix;
    public Fixture otherFix;
    public Object thisFixData;
    public Object otherFixData;
    public EcsContactListener(Entity entity){
        this.entity = entity;
        world = entity.getWorld();
        entityId = entity.getId();
    }

    /**
     * 开始碰撞
     * @param contact
     */
    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        Object userDataA = fixtureA.getUserData();
        Object userDataB = fixtureB.getUserData();
        if (userDataA instanceof DefFixData defFixData) {
            if (defFixData.entityId == entityId) {
                thisFix = fixtureA;
                thisFixData = defFixData;
                otherFix = fixtureB;
                otherFixData = fixtureB.getUserData();
            }else {
                otherFix = fixtureA;
                otherFixData = defFixData;
                thisFix = fixtureB;
                thisFixData = fixtureB.getUserData();
            }
        }else if (userDataB instanceof DefFixData defFixData){
            if (defFixData.entityId == entityId) {
                otherFix = fixtureA;
                otherFixData = defFixData;
                thisFix = fixtureB;
                thisFixData = fixtureB.getUserData();
            }else {
                thisFix = fixtureA;
                thisFixData = defFixData;
                otherFix = fixtureB;
                otherFixData = fixtureB.getUserData();
            }
        }
    }

    /**
     * 结束碰撞
     * @param contact
     */
    @Override
    public void endContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        Object userDataA = fixtureA.getUserData();
        Object userDataB = fixtureB.getUserData();
        if (userDataA instanceof DefFixData defFixData) {
            if (defFixData.entityId == entityId) {
                thisFix = fixtureA;
                thisFixData = defFixData;
                otherFix = fixtureB;
                otherFixData = fixtureB.getUserData();
            }else {
                otherFix = fixtureA;
                otherFixData = defFixData;
                thisFix = fixtureB;
                thisFixData = fixtureB.getUserData();
            }
        }else if (userDataB instanceof DefFixData defFixData){
            if (defFixData.entityId == entityId) {
                otherFix = fixtureA;
                otherFixData = defFixData;
                thisFix = fixtureB;
                thisFixData = fixtureB.getUserData();
            }else {
                thisFix = fixtureA;
                thisFixData = defFixData;
                otherFix = fixtureB;
                otherFixData = fixtureB.getUserData();
            }
        }
    }

    /**
     * 碰撞前处理
     * @param contact
     * @param manifold
     */
    @Override
    public void preSolve(Contact contact, Manifold manifold) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        Object userDataA = fixtureA.getUserData();
        Object userDataB = fixtureB.getUserData();
        if (userDataA instanceof DefFixData defFixData) {
            if (defFixData.entityId == entityId) {
                thisFix = fixtureA;
                thisFixData = defFixData;
                otherFix = fixtureB;
                otherFixData = fixtureB.getUserData();
            }else {
                otherFix = fixtureA;
                otherFixData = defFixData;
                thisFix = fixtureB;
                thisFixData = fixtureB.getUserData();
            }
        }else if (userDataB instanceof DefFixData defFixData){
            if (defFixData.entityId == entityId) {
                otherFix = fixtureA;
                otherFixData = defFixData;
                thisFix = fixtureB;
                thisFixData = fixtureB.getUserData();
            }else {
                thisFix = fixtureA;
                thisFixData = defFixData;
                otherFix = fixtureB;
                otherFixData = fixtureB.getUserData();
            }
        }
    }

    /**
     * 碰撞后处理
     * @param contact
     * @param contactImpulse
     */
    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        Object userDataA = fixtureA.getUserData();
        Object userDataB = fixtureB.getUserData();
        if (userDataA instanceof DefFixData defFixData) {
            if (defFixData.entityId == entityId) {
                thisFix = fixtureA;
                thisFixData = defFixData;
                otherFix = fixtureB;
                otherFixData = fixtureB.getUserData();
            }else {
                otherFix = fixtureA;
                otherFixData = defFixData;
                thisFix = fixtureB;
                thisFixData = fixtureB.getUserData();
            }
        }else if (userDataB instanceof DefFixData defFixData){
            if (defFixData.entityId == entityId) {
                otherFix = fixtureA;
                otherFixData = defFixData;
                thisFix = fixtureB;
                thisFixData = fixtureB.getUserData();
            }else {
                thisFix = fixtureA;
                thisFixData = defFixData;
                otherFix = fixtureB;
                otherFixData = fixtureB.getUserData();
            }
        }
    }
}

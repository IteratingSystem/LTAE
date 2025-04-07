package org.ltae.box2d.listener;

import com.badlogic.gdx.physics.box2d.*;
import org.ltae.box2d.DefFixData;

/**
 * @Auther WenLong
 * @Date 2025/3/27 16:47
 * @Description 自定义碰撞监听,用于初始化给Box2D的World,实现的功能是触发每个形状给定的监听器逻辑
 **/
public class DefContactListener implements ContactListener {

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
            if (defFixData.listener != null) {
                defFixData.listener.preSolve(contact,manifold);
            }
        }
        if (userDataB instanceof DefFixData defFixData) {
            if (defFixData.listener != null) {
                defFixData.listener.preSolve(contact,manifold);
            }
        }
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        Object userDataA = fixtureA.getUserData();
        Object userDataB = fixtureB.getUserData();
        if (userDataA instanceof DefFixData defFixData) {
            if (defFixData.listener != null) {
                defFixData.listener.beginContact(contact);
            }
        }
        if (userDataB instanceof DefFixData defFixData) {
            if (defFixData.listener != null) {
                defFixData.listener.beginContact(contact);
            }
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        Object userDataA = fixtureA.getUserData();
        Object userDataB = fixtureB.getUserData();
        if (userDataA instanceof DefFixData defFixData) {
            if (defFixData.listener != null) {
                defFixData.listener.endContact(contact);
            }
        }
        if (userDataB instanceof DefFixData defFixData) {
            if (defFixData.listener != null) {
                defFixData.listener.endContact(contact);
            }
        }
    }


    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        Object userDataA = fixtureA.getUserData();
        Object userDataB = fixtureB.getUserData();
        if (userDataA instanceof DefFixData defFixData) {
            if (defFixData.listener != null) {
                defFixData.listener.postSolve(contact,contactImpulse);
            }
        }
        if (userDataB instanceof DefFixData defFixData) {
            if (defFixData.listener != null) {
                defFixData.listener.postSolve(contact,contactImpulse);
            }
        }
    }
}

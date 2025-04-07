package org.ltae.box2d.listener;

import com.artemis.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * @Auther WenLong
 * @Date 2025/3/28 10:49
 * @Description 挂载在夹具(形状)中的监听器
 **/
public class ShapeContactListener extends EcsContactListener {

    public ShapeContactListener(Entity entity) {
        super(entity);
    }

    @Override
    public void beginContact(Contact contact) {
        super.beginContact(contact);
    }

    @Override
    public void endContact(Contact contact) {
        super.endContact(contact);
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {
        super.preSolve(contact, manifold);
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {
        super.postSolve(contact, contactImpulse);
    }
}

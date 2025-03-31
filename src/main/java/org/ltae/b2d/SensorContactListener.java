package org.ltae.b2d;

import com.artemis.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * @Auther WenLong
 * @Date 2025/3/28 10:49
 * @Description 挂载在夹具(形状)中的监听器,需要注意的是,传感器只会触发beginContact和endContact回调
 **/
public class SensorContactListener extends EcsContactListener {

    public SensorContactListener(Entity entity) {
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
}

package org.ltae.box2d.listener;

import com.artemis.Entity;
import com.badlogic.gdx.physics.box2d.Contact;

/**
 * @Auther WenLong
 * @Date 2025/3/28 10:49
 * @Description 传感器形状的监听器重写此结果口库,需要注意的是:传感器只会触发beginContact和endContact回调
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

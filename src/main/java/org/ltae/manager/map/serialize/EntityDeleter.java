package org.ltae.manager.map.serialize;

import com.artemis.*;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.utils.Disposable;
import org.ltae.event.EntityEvent;

/**
 * @Auther WenLong
 * @Date 2025/7/11 10:35
 * @Description
 **/
public class EntityDeleter {
    public static void deleteEntity(World world,int entityId){
        Entity entity = world.getEntity(entityId);
        Bag<Component> components = entity.getComponents(new Bag<>(Component.class));
        for (Component component : components) {
            if (component instanceof Disposable disposable) {
                disposable.dispose();
            }
        }
        world.delete(entityId);
    }
    public static void deleteAll(World world){
        //删除所有已存在的实体
        EntitySubscription subscription = world.getAspectSubscriptionManager().get(Aspect.all());
        IntBag entityIds = subscription.getEntities();
        EntityEvent entityEvent = new EntityEvent(EntityEvent.DELETE_ENTITY);
        for(int i = 0; i < entityIds.size(); i++) {
            deleteEntity(world,entityIds.get(i));
        }
    }
}

package org.ltae.manager;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.Bag;
import com.badlogic.gdx.utils.Disposable;

/**
 * @Auther WenLong
 * @Date 2025/7/11 10:35
 * @Description
 **/
public class EntityManager {
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
}

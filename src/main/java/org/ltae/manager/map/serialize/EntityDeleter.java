package org.ltae.manager.map.serialize;

import com.artemis.*;
import com.artemis.managers.TagManager;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;
import org.ltae.event.EntityEvent;
import org.ltae.manager.map.MapManager;

import javax.swing.text.html.HTML;

/**
 * @Auther WenLong
 * @Date 2025/7/11 10:35
 * @Description
 **/
public class EntityDeleter {
    public static final String TAG = EntityDeleter.class.getSimpleName();
    public static void deleteEntity(World world,int entityId){
        Entity entity = world.getEntity(entityId);
        Bag<Component> components = entity.getComponents(new Bag<>(Component.class));
        for (Component component : components) {
            if (component instanceof Disposable disposable) {
                disposable.dispose();
            }
        }
        //删除实体同时取消注册的标签
        TagManager tagManager = world.getSystem(TagManager.class);
        String tag = tagManager.getTag(entityId);
        if (tag != null && !"".equals(tag)) {
            tagManager.unregister(tag);
        }
        world.delete(entityId);
    }
    public static void deleteAll(World world){
        //删除所有已存在的实体
        EntitySubscription subscription = world.getAspectSubscriptionManager().get(Aspect.all());
        IntBag entityIds = subscription.getEntities();
        for(int i = 0; i < entityIds.size(); i++) {
            deleteEntity(world,entityIds.get(i));
        }
    }

    /**
     * 删除所有实体同时过滤掉不删除的某些实体
     * @param world
     * @param filterTags
     */
    public static void deleteAll(World world,String[] filterTags){
        TagManager tagManager = world.getSystem(TagManager.class);
        IntBag filterIds = new IntBag();
        for (String filterTag : filterTags) {
            Entity entity = tagManager.getEntity(filterTag);
            if (entity == null) {
                Gdx.app.log(TAG,"In 'deleteAll(World world,String[] filterTags)',not find entity from filterTag,filterTag:"+filterTag);
                continue;
            }
            int id = entity.getId();
            filterIds.add(id);
        }
        EntitySubscription subscription = world.getAspectSubscriptionManager().get(Aspect.all());
        IntBag entityIds = subscription.getEntities();
        for(int i = 0; i < entityIds.size(); i++) {
            int entityId = entityIds.get(i);
            if (filterIds.contains(entityId)){
                continue;
            }
            deleteEntity(world,entityId);
        }
    }
}

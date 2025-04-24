package org.ltae.ai;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

/**
 * @Auther WenLong
 * @Date 2025/4/9 16:59
 * @Description ecs叶任务
 **/
public class EcsLeafTask extends LeafTask<Entity> {

    public World world;
    public Entity entity;
    public String entityTag;
    public int entityId;
    public TagManager tagManager;
    private void initialize(){
        entity = getObject();
        world = entity.getWorld();
        entityId = entity.getId();
        tagManager = world.getSystem(TagManager.class);
        entityTag = tagManager.getTag(entityId);
    }

    @Override
    public void start() {
        initialize();
    }

    @Override
    protected Task<Entity> copyTo(Task<Entity> task) {
        return task;
    }

    @Override
    public Status execute() {
        return Status.RUNNING;
    }
    public final String getTAG(){
        return getClass().getSimpleName();
    }
}


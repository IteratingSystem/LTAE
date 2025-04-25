package org.ltae.ai;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;
import com.badlogic.gdx.math.Vector2;
import org.ltae.component.Pos;

/**
 * @Auther WenLong
 * @Date 2025/4/10 15:50
 * @Description 距离
 **/
public class Dst extends EcsLeafTask {

    //对方的TAG
    @TaskAttribute
    public String targetEntityTag = "PLAYER";
    //判断距离
    @TaskAttribute
    public float distance = 0;

    @Override
    public Status execute() {
        if (!tagManager.isRegistered(targetEntityTag)) {
            Gdx.app.error(getTAG(),"entityTag is not registered:tagName:"+targetEntityTag);
            return Status.FAILED;
        }

        ComponentMapper<Pos> mPos = world.getMapper(Pos.class);
        if (!mPos.has(entity)) {
            Gdx.app.error(getTAG(),"This entity is not has 'Pos' component!Entity tag:"+entityTag);
            return Status.FAILED;
        }

        Entity target = tagManager.getEntity(targetEntityTag);
        if (!mPos.has(target)) {
            Gdx.app.error(getTAG(),"Target entity entity is not has 'Pos' component!Entity tag:"+targetEntityTag);
            return Status.FAILED;
        }

        Pos targetPos = mPos.get(target);
        Pos pos = mPos.get(entityId);
        float dst = new Vector2(pos.x, pos.y).dst(targetPos.x, targetPos.y);
        if (distance >= dst){
            return Status.SUCCEEDED;
        }
        return Status.FAILED;
    }
}

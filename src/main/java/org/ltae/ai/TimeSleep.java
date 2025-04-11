package org.ltae.ai;

import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

/**
 * @Auther WenLong
 * @Date 2025/4/10 15:50
 * @Description 时间休眠任务
 **/
public class TimeSleep extends EcsLeafTask {

    //传入休眠时间(秒)
    @TaskAttribute
    public float time = 1;

    private float timing;

    @Override
    public void start() {
        super.start();
        timing = 0;
    }

    @Override
    public Status execute() {
        timing += world.getDelta();
        if (timing >= time){
            return Status.SUCCEEDED;
        }
        return Status.RUNNING;
    }

    @Override
    protected Task copyTo(Task task) {
        return task;
    }
}

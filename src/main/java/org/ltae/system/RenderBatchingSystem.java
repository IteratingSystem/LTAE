package org.ltae.system;

import com.artemis.BaseSystem;
import com.artemis.World;
import com.artemis.annotations.Wire;
import com.artemis.utils.Bag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.api.system.delegate.EntityProcessAgent;
import net.mostlyoriginal.api.system.delegate.EntityProcessPrincipal;
import net.mostlyoriginal.api.utils.BagUtils;
import org.ltae.component.Render;
import org.ltae.component.ZIndex;

/**
 * @Auther WenLong
 * @Date 2024/6/6 9:34
 * @Description 渲染管线
 **/
@Wire
public class RenderBatchingSystem extends BaseSystem implements EntityProcessPrincipal {
    private final static String TAG = RenderBatchingSystem.class.getSimpleName();
    private M<Render> mRender;
    private M<ZIndex> mZIndex;
    private RenderTiledSystem renderTiledSystem;

    private Batch batch;
    protected final Bag<Job> sortedJobs = new Bag<>();

    @Override
    protected void setWorld(World world) {
        super.setWorld(world);
    }

    @Override
    protected void initialize() {
        super.initialize();
        batch = renderTiledSystem.mapRenderer.getBatch();
    }

    @Override
    protected void processSystem() {
        BagUtils.sort(sortedJobs);
//        EntityProcessAgent activeAgent = null;
        final Object[] data = sortedJobs.getData();
        batch.begin();
        for (int i = 0,s = sortedJobs.size();i < s;i++){
            final Job job = (Job)data[i];
            final EntityProcessAgent agent = job.agent;

//            if (agent != activeAgent){
//                if (activeAgent != null){
////                    activeAgent.end();
//                }
//                activeAgent = agent;
////                activeAgent.begin();
//            }
            agent.process(job.entityId);
        }
        batch.end();
    }

    @Override
    public void registerAgent(int entityId, EntityProcessAgent agent) {
        if (!mRender.has(entityId) || !mZIndex.has(entityId)){
            Gdx.app.log(TAG,"Lack of necessary components:[Render] or [ZIndex]");
            return;
        }

        Job job = new Job(entityId, agent);
        sortedJobs.add(job);
    }

    @Override
    public void unregisterAgent(int entityId, EntityProcessAgent agent) {
        final Object[] data = sortedJobs.getData();
        for (int i = 0,s = sortedJobs.size(); i < s; i++){
            final Job e2 = (Job) data[i];
            if (e2.entityId == entityId && e2.agent == agent){
                sortedJobs.remove(i);
                break;
            }
        }
    }


    public class Job implements Comparable<Job>{
        public final int entityId;
        public final EntityProcessAgent agent;

        public Job(final int entityId,final EntityProcessAgent agent){
            this.entityId = entityId;
            this.agent = agent;
        }
        @Override
        public int compareTo(Job o) {
            int entityIdO = o.entityId;
            float startNum = 0;
            float endNum = 0;
            if (mZIndex.has(entityIdO)) {
                ZIndex zIndex0 = mZIndex.get(entityIdO);
                startNum = zIndex0.index + zIndex0.offset;
            }
            if (mZIndex.has(entityId)){
                ZIndex zIndex = mZIndex.get(entityId);
                endNum = zIndex.index + zIndex.offset;
            }
            return (int)(startNum-endNum);
        }
    }
}

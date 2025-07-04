package org.ltae.component;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.utils.ObjectMap;
import org.ltae.system.AssetSystem;
import org.ltae.tiled.TiledSerializeLoader;
import org.ltae.tiled.SerializeParam;
import org.ltae.tiled.details.EntityDetails;
import org.ltae.tiled.details.SystemDetails;

/**
 * @Auther WenLong
 * @Date 2025/4/9 16:14
 * @Description 行为树组件
 **/
public class BTree extends SerializeComponent implements TiledSerializeLoader {
    private final static String TAG = BTree.class.getSimpleName();
    public BehaviorTree<Entity> tree;

    @SerializeParam
    public String treeName;
    @Override
    public void loader(SystemDetails systemDetails, EntityDetails entityDetails) {
        World world = systemDetails.world;
        AssetSystem assetSystem = world.getSystem(AssetSystem.class);
        if (assetSystem == null){
            Gdx.app.error(TAG,"assetSystem is null!");
            return;
        }
        ObjectMap<String, BehaviorTree> bTreeData = assetSystem.bTreeData;
        if (bTreeData.isEmpty()) {
            Gdx.app.error(TAG,"bTreeData is empty!Unable to load behavior tree: "+treeName);
            return;
        }
        if (!bTreeData.containsKey(treeName)) {
            Gdx.app.error(TAG,"This behavior tree is not present in bTreeData: "+treeName);
            return;
        }
        Entity entity = world.getEntity(entityDetails.entityId);
        tree = bTreeData.get(treeName);
        tree.setObject(entity);
        tree.start();
    }
}

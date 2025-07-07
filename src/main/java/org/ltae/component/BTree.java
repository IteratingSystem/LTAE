package org.ltae.component;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.utils.ObjectMap;
import org.ltae.system.AssetSystem;
import org.ltae.serialize.SerializeParam;
import org.ltae.serialize.json.EntityJson;

/**
 * @Auther WenLong
 * @Date 2025/4/9 16:14
 * @Description 行为树组件
 **/
public class BTree extends SerializeComponent{
    private final static String TAG = BTree.class.getSimpleName();
    public BehaviorTree<Entity> tree;

    @SerializeParam
    public String treeName;
    @Override
    public void reload(World world, EntityJson entityJson) {
        super.reload(world,entityJson);
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
        Entity entity = world.getEntity(entityId);
        tree = bTreeData.get(treeName);
        tree.setObject(entity);
        tree.start();
    }
}

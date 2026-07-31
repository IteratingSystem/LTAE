package org.ltae.component;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import org.ltae.component.parent.SerializeComponent;
import org.ltae.serialize.PostLoad;
import org.ltae.system.AssetSystem;
import org.ltae.serialize.SerializeParam;
import org.ltae.serialize.data.EntityDatum;

/**
 * @Auther WenLong
 * @Date 2025/4/9 16:14
 * @Description 行为树组件
 **/
public class BTree extends SerializeComponent {
    private final static String TAG = BTree.class.getSimpleName();
    public transient BehaviorTree<Entity> tree;

    @SerializeParam
    public String treeName;

    @Override
    public void write(Json json) {
        super.write(json);
        json.writeValue("treeName", treeName);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        treeName = jsonData.has("treeName") ? jsonData.getString("treeName") : null;
    }

    @PostLoad
    public void postLoadBTree(World world) {
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

        BehaviorTree<?> rawTree = bTreeData.get(treeName);
        if (rawTree instanceof BehaviorTree<?>) {
            @SuppressWarnings("unchecked")
            BehaviorTree<Entity> typedTree = (BehaviorTree<Entity>) rawTree;
            tree = typedTree;
            tree.setObject(entity);
            tree.start();
        } else {
            Gdx.app.error(TAG, "BehaviorTree type mismatch for: " + treeName);
        }
    }
}

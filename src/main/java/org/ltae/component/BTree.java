package org.ltae.component;

import com.artemis.Component;
import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.utils.ObjectMap;
import org.ltae.system.AssetSystem;
import org.ltae.tiled.TileCompLoader;
import org.ltae.tiled.TileDetails;
import org.ltae.tiled.TileParam;

/**
 * @Auther WenLong
 * @Date 2025/4/9 16:14
 * @Description 行为树组件
 **/
public class BTree extends Component implements TileCompLoader {
    private final static String TAG = BTree.class.getSimpleName();
    public BehaviorTree<Entity> tree;

    @TileParam
    public String treeName;
    @Override
    public void loader(TileDetails tileDetails) {
        AssetSystem assetSystem = tileDetails.assetSystem;
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
        tree = bTreeData.get(treeName);
        tree.setObject(tileDetails.entity);
        tree.start();
    }
}

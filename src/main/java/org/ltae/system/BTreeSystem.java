package org.ltae.system;

import com.artemis.annotations.All;
import com.artemis.annotations.One;
import com.artemis.systems.IteratingSystem;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import org.ltae.component.BTree;

/**
 * @Auther WenLong
 * @Date 2025/4/9 16:29
 * @Description 行为树系统
 **/

@One(BTree.class)
public class BTreeSystem extends IteratingSystem {
    private M<BTree> mBTree;

    @Override
    protected void process(int entityId) {
        BTree bTree = mBTree.get(entityId);
        bTree.tree.step();
    }
}

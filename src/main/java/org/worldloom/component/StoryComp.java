package org.worldloom.component;

import com.artemis.World;
import org.worldloom.component.parent.SerializeComponent;
import org.worldloom.serialize.SerializeParam;
import org.worldloom.serialize.data.EntityDatum;

/**
 * Ink 对话剧本组件。
 *
 * <p>保存剧本资源名称、运行状态和初始节点。语言选择、标签处理及具体剧情逻辑
 * 由游戏项目负责。</p>
 */
public class StoryComp extends SerializeComponent {
    public static final String DEFAULT_START_NODE = "start";

    @SerializeParam
    public String storyName;
    @SerializeParam
    public String saveJson;
    @SerializeParam
    public String startNode;

    @Override
    public void reload(World world, EntityDatum entityDatum) {
        super.reload(world, entityDatum);

        if (startNode == null || startNode.isEmpty()) {
            startNode = DEFAULT_START_NODE;
        }
    }
}

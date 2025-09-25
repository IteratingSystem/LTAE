package org.ltae.component;

import com.artemis.World;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import org.ltae.manager.ShaderManage;
import org.ltae.manager.map.serialize.SerializeParam;
import org.ltae.manager.map.serialize.json.EntityData;

public class ShaderComp extends SerializeComponent {
    @SerializeParam
    public String vertexName;
    @SerializeParam
    public String fragmentName;

    public ShaderProgram shaderProgram;

    @Override
    public void reload(World world, EntityData entityData) {
        super.reload(world, entityData);
        ShaderManage shaderManage = ShaderManage.getInstance();
        String vertexContext = shaderManage.getVertexContext(vertexName);
        String fragmentContext = shaderManage.getFragmentContext(fragmentName);
        shaderProgram = new ShaderProgram(vertexContext,fragmentContext);
//        shaderProgram.bind();
    }
}

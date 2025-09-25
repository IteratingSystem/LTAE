package org.ltae.component;

import com.artemis.World;
import com.badlogic.gdx.Gdx;
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
        if (vertexContext == null || fragmentContext == null){
            shaderProgram = null;
            return;
        }
        try {
            shaderProgram = new ShaderProgram(vertexContext,fragmentContext);
            if (!shaderProgram.isCompiled()) {
                Gdx.app.error(getTag(),"Could not compile shader: "+shaderProgram.getLog());
            }
        }catch (Exception e){
            Gdx.app.error(getTag(),"Failed to init shaderProgram: "+e.getMessage());
        }

    }
}

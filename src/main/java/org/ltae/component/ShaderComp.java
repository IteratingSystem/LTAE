package org.ltae.component;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import org.ltae.LtaePluginRule;
import org.ltae.manager.ShaderManage;
import org.ltae.serialize.SerializeParam;
import org.ltae.serialize.data.EntityDatum;
import org.ltae.shader.ShaderUniforms;
import org.ltae.utils.ReflectionUtils;

public class ShaderComp extends SerializeComponent {
    @SerializeParam
    public String vertexName;
    @SerializeParam
    public String fragmentName;
    /**
     * 指定ShaderUniforms子类用于传入参数
     * 此处参数uniformSimpleName为单独类名
     * 包名为LtaePluginRule.SHADER_UNIFORMS_PKG,可修改报名
     */
    @SerializeParam
    public String uniformSimpleName;

    public ShaderProgram shaderProgram;
    public ShaderUniforms shaderUniforms;

    @Override
    public void reload(World world, EntityDatum entityDatum) {
        super.reload(world, entityDatum);
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
            return;
        }
        if (uniformSimpleName == null || uniformSimpleName.isEmpty()){
            return;
        }
        String className = LtaePluginRule.SHADER_UNIFORMS_PKG + "." + uniformSimpleName;
        shaderUniforms = ReflectionUtils.createObject(className,new Class[]{Entity.class},new Entity[]{world.getEntity(entityId)},ShaderUniforms.class);
    }
}

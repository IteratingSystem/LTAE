package org.ltae.component;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import org.ltae.component.parent.SerializeComponent;
import org.ltae.manager.ReflectionManager;
import org.ltae.manager.ShaderManager;
import org.ltae.serialize.SerializeParam;
import org.ltae.serialize.data.EntityDatum;
import org.ltae.shader.ShaderUniforms;

import java.util.Set;

public class ShaderComp extends SerializeComponent {
    @SerializeParam
    public String vertexName;
    @SerializeParam
    public String fragmentName;
    @SerializeParam
    public String uniformSimpleName;

    public ShaderProgram shaderProgram;
    public ShaderUniforms shaderUniforms;

    @Override
    public void reload(World world, EntityDatum entityDatum) {
        super.reload(world, entityDatum);
        ShaderManager shaderManager = ShaderManager.getInstance();
        String vertexContext = shaderManager.getVertexContext(vertexName);
        String fragmentContext = shaderManager.getFragmentContext(fragmentName);

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

        ReflectionManager reflectionManager = ReflectionManager.getInstance();
        Set<Class<? extends ShaderUniforms>> subTypesOfWithGame = reflectionManager.getSubTypesOfWithGame(ShaderUniforms.class);
        for (Class<? extends ShaderUniforms> aClass : subTypesOfWithGame) {
            if (!aClass.getSimpleName().equals(shaderUniforms)) {
                return;
            }
            shaderUniforms = reflectionManager.createObject(
                    aClass,
                    new Class[]{Entity.class},
                    new Entity[]{world.getEntity(entityId)}
            );
            break;
        }

        if (shaderUniforms == null) {
            Gdx.app.error(getTag(),"Could not find shaderUniforms for "+shaderUniforms);
            return;
        }
        Gdx.app.debug(getTag(),"Loaded shaderUniforms : " + shaderUniforms);
    }
}

package org.worldloom.component;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import org.worldloom.component.parent.SerializeComponent;
import org.worldloom.manager.ReflectionManager;
import org.worldloom.manager.ShaderManager;
import org.worldloom.serialize.SerializeParam;
import org.worldloom.serialize.data.EntityDatum;
import org.worldloom.shader.ShaderUniforms;

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
        Class<? extends ShaderUniforms> aClass = reflectionManager.getSubTypesOfWithGame(ShaderUniforms.class)
                .stream()
                .filter(c -> c.getSimpleName().equals(uniformSimpleName))
                .findFirst()
                .orElse(null);

        if (aClass == null){
            Gdx.app.error(getTag(),"Could not find ShaderUniforms for "+uniformSimpleName);
            return;
        }

        shaderUniforms = reflectionManager.createObject(
                aClass,
                new Class[]{Entity.class},
                new Entity[]{world.getEntity(entityId)}
        );

        if (shaderUniforms == null) {
            Gdx.app.error(getTag(),"Could not find shaderUniforms for "+shaderUniforms);
            return;
        }
        Gdx.app.debug(getTag(),"Loaded shaderUniforms : " + shaderUniforms);
    }
}

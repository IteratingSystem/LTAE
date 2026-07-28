package org.ltae.component;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import org.ltae.component.parent.SerializeComponent;
import org.ltae.manager.ReflectionManager;
import org.ltae.manager.ShaderManager;
import org.ltae.serialize.PostLoad;
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

    public transient ShaderProgram shaderProgram;
    public transient ShaderUniforms shaderUniforms;

    @Override
    public void write(Json json) {
        super.write(json);
        json.writeValue("vertexName", vertexName);
        json.writeValue("fragmentName", fragmentName);
        json.writeValue("uniformSimpleName", uniformSimpleName);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        vertexName = jsonData.has("vertexName") ? jsonData.getString("vertexName") : null;
        fragmentName = jsonData.has("fragmentName") ? jsonData.getString("fragmentName") : null;
        uniformSimpleName = jsonData.has("uniformSimpleName") ? jsonData.getString("uniformSimpleName") : null;
    }

    @PostLoad
    public void postLoadShader(World world) {
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

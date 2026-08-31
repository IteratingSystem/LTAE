package org.ltae.shader;

import com.artemis.World;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Disposable;

/**
 * 为Shader瓦片层提供游戏侧的纹理和Uniform数据。
 */
public interface TileLayerShaderUniforms extends Disposable {
    default void initialize(World world, ShaderProgram shaderProgram) {
    }

    void apply(TiledMap tiledMap, ShaderProgram shaderProgram, float delta);

    @Override
    default void dispose() {
    }
}

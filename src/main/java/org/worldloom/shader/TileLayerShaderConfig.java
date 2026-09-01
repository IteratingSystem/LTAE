package org.worldloom.shader;

/**
 * Shader瓦片层的不可变配置。
 */
public final class TileLayerShaderConfig {
    private final String layerName;
    private final String vertexName;
    private final String fragmentName;
    private final TileLayerShaderUniforms uniforms;

    public TileLayerShaderConfig(String layerName, String vertexName,
                                 String fragmentName,
                                 TileLayerShaderUniforms uniforms) {
        this.layerName = requireName(layerName, "layerName");
        this.vertexName = requireName(vertexName, "vertexName");
        this.fragmentName = requireName(fragmentName, "fragmentName");
        if (uniforms == null) {
            throw new IllegalArgumentException("uniforms cannot be null");
        }
        this.uniforms = uniforms;
    }

    public String getLayerName() {
        return layerName;
    }

    public String getVertexName() {
        return vertexName;
    }

    public String getFragmentName() {
        return fragmentName;
    }

    public TileLayerShaderUniforms getUniforms() {
        return uniforms;
    }

    private static String requireName(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        return value;
    }
}

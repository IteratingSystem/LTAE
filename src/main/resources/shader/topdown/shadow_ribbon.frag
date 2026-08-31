#ifdef GL_ES
precision mediump float;
#endif

void main() {
    // 阴影带已经包含纵深，G通道保持为零以避免后续重复膨胀。
    gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
}

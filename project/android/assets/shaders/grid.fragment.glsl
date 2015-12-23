#ifdef GL_ES
precision highp float;
#endif

varying vec3 n;
varying vec2 uv;


uniform mat4 _mv; // model-view matrix
uniform mat4 _mvProj; // model-view-projection matrix


vec4 sand1 = vec4(0.93, 0.95, 0.56, 1.0);



void main(void) {
	gl_FragColor = sand1;
}

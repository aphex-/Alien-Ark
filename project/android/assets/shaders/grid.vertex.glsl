attribute vec3 vertex;
attribute vec3 normal;
attribute vec2 uv1;
attribute vec4 tangent;

uniform mat4 _mv; // model-view matrix
uniform mat4 _mvProj; // model-view-projection matrix

varying vec2 uv;
varying vec3 n;

void main(void) {
    vec3 v = vertex;
	gl_Position = _mvProj * _mv * vec4(v, 1.0);;
	uv = uv1;
}
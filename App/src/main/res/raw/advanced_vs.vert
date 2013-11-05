attribute vec4 a_position;
attribute vec2 a_texcoord;
attribute vec3 a_normal;

uniform mat4 mvp_matrix;
uniform mat4 normal_matrix;

// ec = eye coordinate
varying vec3 ec_normal;
varying vec3 ec_light_dir;
varying vec3 ec_view_dir;
varying vec2 uv;

vec3 light_pos = vec3(5.0, 5.0, 15.0);

void main()
{
	vec3 v = a_position.xyz;
	
	//No normalization here, is done in the fragment shader instead.
	ec_normal    = mat3(normal_matrix) * a_normal;
    ec_view_dir  = -(mat3(normal_matrix) * v);
    ec_light_dir = light_pos - v;

    uv = a_texcoord;
    
    gl_Position = mvp_matrix * a_position;
}

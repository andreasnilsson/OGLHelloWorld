attribute vec3 aPosition;
attribute vec2 aUV;
attribute vec3 aNormal;


uniform mat4 uMVPMatrix;
uniform vec3 uCamera;

varying vec3 n;
varying vec3 eye;
varying vec2 uv;
varying vec3 light_dir;
varying vec3 view_dir;


void main()
{
    vec3 pos = aPosition;

    // calculate the light direction
    light_dir = vec3();

    // calculate the view direction
    view_dir = vec3();

    // interpolating uv's
    uv = aUV;

    //No need to normalize here since we need to do it in the fs
    n = mat3(uMVPMatrix) * aNormal;

    gl_Position = uMVPMatrix * vec4(pos, 1.0);
}

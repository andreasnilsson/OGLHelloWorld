precision highp float;

varying vec3 n;
varying vec3 eye;
varying vec2 uv;
varying vec3 light_dir;
varying vec3 view_dir;

uniform sampler2D texture01;

void main()
{
    // set the ambient color
    vec3 ambient_color = vec3(0.1);

    // Normalize vectors
    vec3 L = normalize(light_dir);
    vec3 N = normalize(n);
    // normalize and create half-way vector
    vec3 H = normalize(light_dir + view_dir);

    // TODO do the actual light computations

    gl_FragColor.rgb = texture2D(texture01, uv).rgb;

    gl_FragColor.a = 1.0;
}

precision highp float;

varying vec3 n;
varying vec3 eye;
varying vec2 uv;
varying vec3 light_dir;
varying vec3 view_dir;

uniform sampler2D texture01;

void main()
{
    vec3 ambient_color = vec3(0.1);
    vec3 L = normalize(light_dir);
    vec3 N = normalize(n);
    vec3 H = normalize(light_dir + view_dir);

    float NdotL = max(dot(N, L), 0.0);
    float NdotH = max(dot(N, H), 0.0);

    float intensity = pow(NdotH, 15.0);

    gl_FragColor.rgb =
    ambient_color
    + 0.5 * NdotL * texture2D(texture01, uv).rgb
    + 0.5 * intensity * vec3(1.0);
    //+ 0.5*vec3(uv.x, uv.y, 1.0)
    //+ 0.1 * light_dir;

    gl_FragColor.a = 1.0;
}

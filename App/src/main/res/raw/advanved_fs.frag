precision highp float;

varying vec3 ec_normal;
varying vec3 ec_light_dir;
varying vec3 ec_view_dir;
varying vec2 uv;

uniform sampler2D texture01;

// Constants
const vec3  WHITE = vec3(1.0);
const float ALPHA = 1.0;

//Material properties
float att_ambient  = 0.15;
float att_diffuse  = 0.4;
float att_specular = 0.55;

vec3 ambient_color  = WHITE;
vec3 diffuse_color  = WHITE;
vec3 specular_color = WHITE;

float shininess = 8.0;

void main()
{
    vec3 L = normalize(ec_light_dir);
    vec3 N = normalize(ec_normal);
    vec3 H = normalize(ec_light_dir + ec_view_dir);

    float NdotL = max(dot(N, L), 0.0);
    float NdotH = max(dot(N, H), 0.0);    

    vec3 diffuse_texture = texture2D(texture01, uv).rgb;

    vec3 i_ambient  = att_ambient  * ambient_color;
    vec3 i_diffuse  = att_diffuse  * diffuse_color  * diffuse_texture * NdotL;
    vec3 i_specular = att_specular * specular_color * pow(NdotH, shininess);

    gl_FragColor = vec4(i_ambient + i_diffuse + i_specular, ALPHA);
}

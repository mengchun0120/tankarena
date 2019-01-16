precision mediump float;

uniform bool useTime;
uniform float curTime;
uniform float duration;
uniform vec3 timeColor;
uniform vec4 color;

void main() {
    if(!useTime) {
        gl_FragColor = color;
    } else {
        gl_FragColor = vec4(timeColor, 1.0 - curTime / duration);
    }
}
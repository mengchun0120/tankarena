uniform bool relativeToViewportOrigin;
uniform vec2 viewportOrigin;
uniform bool useObjRef;
uniform vec2 objRef;
uniform bool rotate;
uniform vec2 rotateRef;
uniform vec2 rotateDirection;
uniform vec2 viewportSize;

attribute vec2 position;

void main() {
    vec2 pos = position;

    if(rotate) {
        vec2 delta = pos - rotateRef;
        pos[0] = rotateRef[0] + delta[0] * rotateDirection[0] -
            delta[1] * rotateDirection[1];
        pos[1] = rotateRef[1] + delta[0] * rotateDirection[1] +
            delta[1] * rotateDirection[0];
    }

    if(useObjRef) {
        pos += objRef;
    }

    if(relativeToViewportOrigin) {
        pos -= viewportOrigin;
    }

    gl_Position = vec4(pos * 2.0 / viewportSize, 0.0, 1.0);
}
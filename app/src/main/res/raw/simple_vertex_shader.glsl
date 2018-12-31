uniform bool u_RelativeToViewport;
uniform vec2 u_ViewportOrigin;
uniform bool u_UseObjRef;
uniform vec2 u_ObjRef;
uniform mat4 u_ProjectionMatrix;

attribute vec2 a_Position;

void main() {
    vec2 pos = a_Position;

    if(u_UseObjRef) {
        pos += u_ObjRef;
    }

    if(u_RelativeToViewport) {
        pos -= u_ViewportOrigin;
    }

    gl_Position = u_ProjectionMatrix * vec4(pos, 0.0, 1.0);
}
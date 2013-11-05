package ogl;

import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;

public class GLES20Renderer implements GLSurfaceView.Renderer {

    public static final int BYTES_PER_FLOAT = Float.SIZE / 8;

    private final float[] UP_VECTOR = {0f, 1f, 0f};

    private int mWidth;
    private int mHeight;

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Setup opengl
        glClearColor(1, 0, 0, 1);

        // Initiate objects
        GLObject triangle = createSimpleTexturedQuad();

        // Compile and link shader


        // setup parameters


    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        mHeight = height;
        mWidth = width;
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Bind stuff

        // Draw

    }

    protected GLObject createSimpleTexturedQuad() {
        // For simplifying the reading of the defined quadVertices array
        final int X = 0;
        final int Y = 1;
        final int Z = 2;
        final int U = 3;
        final int V = 4;

        final int vertexDataStride = 5;   // I.e. there are 5 floats between each defined point.
        final int positionOffset = 0;     // The offset for each vertex where the position attributes start.
        final int texCoordOffset = 3; // The offset for texture coordinate.

        // The four vectors of the quad
        final float v0[] = {-0.5f, -0.5f, 0f, 0, 0};
        final float v1[] = {0.5f, -0.5f, 0f, 1, 0};
        final float v2[] = {0.5f,  0.5f, 0f, 1, 1};
        final float v3[] = {-0.5f,  0.5f, 0f, 0, 1};

        final float[] quadVertices = {
                // First triangle
                v0[X], v0[Y], v0[Z], v0[U], v0[V],
                v1[X], v1[Y], v1[Z], v1[U], v1[V],
                v2[X], v2[Y], v2[Z], v2[U], v2[V],

                // Second triangle
                v0[X], v0[Y], v0[Z], v0[U], v0[V],
                v2[X], v2[Y], v2[Z], v2[U], v2[V],
                v3[X], v3[Y], v3[Z], v3[U], v3[V],
        };



        return new GLObject(allocateNativeFloatBuffer(quadVertices), vertexDataStride, positionOffset, texCoordOffset);

    }

    /**
     * TODO
     *
     * @param vertices The vertices of whom we will allocate data for.
     * @return The allocated data.
     */
    private FloatBuffer allocateNativeFloatBuffer(float[] vertices) {
        final FloatBuffer buffer = ByteBuffer
                .allocateDirect(vertices.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertices);

        // Reset buffer position
        buffer.position(0);

        return buffer;
    }


    public static class GLObject {

        public final FloatBuffer vertexBuffer;
        private final int vertexDataStride;
        private final int positionOffset;
        private final int textureCoordOffset;

        public GLObject(FloatBuffer vertexBuffer, int vertexDataStride, int positionOffset, int textureCoordOffset) {
            this.vertexBuffer = vertexBuffer;
            this.vertexDataStride = vertexDataStride;
            this.positionOffset = positionOffset;
            this.textureCoordOffset = textureCoordOffset;
        }
    }
}

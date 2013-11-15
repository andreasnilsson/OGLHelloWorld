package com.jayway.oglhelloworld.ogl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.jayway.oglhelloworld.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_TRUE;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

public class GLES20Renderer implements GLSurfaceView.Renderer {
    private static final String TAG = GLES20Renderer.class.getSimpleName();

    // Constants
    public static final int COORDS_PER_VERTEX = 3;
    public static final float[] CLEAR_COLOR = {0f, 0f, 0f, 1f};
    public static final int BYTES_PER_FLOAT = Float.SIZE / 8;


    // Camera/View related
    private final float[] up = {0f, 1f, 0f};
    private final float[] eye = {0f, 0f, 5f};
    private final float[] center = {0f, 0f, 0f};
    private int mHeight;
    private int mWidth;
    private static final float FOV = 45; // Field of view

    private final Context mContext;

    // Matrices
    private float[] mMatrixMVP = new float[16];
    private float[] mMatrixProjection = new float[16];
    private float[] mMatrixView = new float[16];
    private float[] mMatrixModel = new float[16];

    // Shader Constants
    private static final int SHADER_COMPILED_WITH_ERROR = 0;
    private static final int PROGRAM_COMPILED_WITH_ERROR = 0;
    private int mShaderProgram = PROGRAM_COMPILED_WITH_ERROR;

    // Shader source uniform and attribute variable names
    private static final String U_MVP_MATRIX = "uMVPMatrix";
    private static final String A_POSITION = "aPosition";

    // Shader Handles
    private int mLocationMVPMatrix;
    private int mPositionHandle;

    // Vertex objects
    private GLObject mTriangle;

    private final String mVertexShader =
                      "attribute vec4 aPosition;"
                    + "void main() {"
                    + "  gl_Position = aPosition;"
                    + "}";

    private final String mFragmentShader =
                     "precision mediump float;"
                    +"void main() {"
                    +"  gl_FragColor = vec4(1.0);"
                    +"}";


    public GLES20Renderer(Context context) {
        mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Setup opengl
        glClearColor(CLEAR_COLOR[0], CLEAR_COLOR[1], CLEAR_COLOR[2], CLEAR_COLOR[3]);

        // Initiate objects
        mTriangle = createSimpleTexturedQuad();

        // Compile and link shader program
        String fragmentShaderSrc = null;
        String vertexShaderSrc = null;
        try {
            Log.i(TAG, "Fragment source: Start loading...");
            fragmentShaderSrc = loadShaderSourceFromRaw(R.raw.simple_fs);
            Log.i(TAG, "Fragment source: Done!");

            Log.i(TAG, "Vertex source: Start loading...");
            vertexShaderSrc = loadShaderSourceFromRaw(R.raw.simple_vs);
            Log.i(TAG, "Vertex source: Done");
        } catch (IOException e) {
            Log.e(TAG, "Failed reading reading shader resource", e);
        }

//        if (vertexShaderSrc != null && fragmentShaderSrc != null) {
        mShaderProgram = createProgram(vertexShaderSrc, fragmentShaderSrc);
//
//        }

        mShaderProgram = createProgram(mVertexShader, mFragmentShader);

        // Setup uniform and attributes
        if (mShaderProgram != PROGRAM_COMPILED_WITH_ERROR) {
            glUseProgram(mShaderProgram);
            // bind Uniforms and Attributes
//            mLocationMVPMatrix = glGetUniformLocation(mShaderProgram, U_MVP_MATRIX);

//            if (mLocationMVPMatrix == -1) {
//                Log.w(TAG, "Failed binding: " + U_MVP_MATRIX);
//            }

//            mPositionHandle = glGetAttribLocation(mShaderProgram, A_POSITION);
//            if (mPositionHandle == -1) {
//                Log.w(TAG, "Failed binding: " + A_POSITION);
//            }
        }

        String test = "tomte";

        switch (test) {
            case "tomte:":
                break;

        }
    }
//
    private String loadShaderSourceFromRaw(int id) throws IOException {
        final InputStream is = mContext.getResources().openRawResource(id);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String readLine;
        while ((readLine = br.readLine()) != null) {
            sb.append(readLine);
        }

        return sb.toString();
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        Matrix.setIdentityM(mMatrixModel, 0);
        Matrix.setIdentityM(mMatrixProjection, 0);
        Matrix.setIdentityM(mMatrixView, 0);

        glViewport(0, 0, width, height);

//        mHeight = height;
//        mWidth = width;
//
//        float aspect = (float) width / height;


//        // Setup projection
//        Matrix.setIdentityM(mMatrixProjection, 0);
//        Matrix.perspectiveM(mMatrixProjection, 0, FOV, aspect, 0.1f, 10f);
//
//        // Setup view matrix
//        Matrix.setLookAtM(mMatrixView, 0,
//                eye[0], eye[1], eye[2],
//                center[0], center[1], center[2],
//                up[0], up[1], up[2]);

//
//        glViewport(0, 0, width, height);
        //Setup OpenGL viewport


        // Projection using projection matrix, requires later API level.
//        Matrix.perspectiveM(projMatrix, 0, 45f, aspect, 0.1f, 100f);

        // Projection using frustum matrix
//        Matrix.frustumM(mMatrixProjection, 0, -aspect, aspect, -1, 1, 3, 7);
//        Matrix.setLookAtM(mMatrixView, 0, 0, 0, -5, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

//        Matrix.perspectiveM(mMatrixProjection, 0, 45f, aspect, 0.1f, 100f);
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        glClear(GL_COLOR_BUFFER_BIT);

//        // MVP matrix computation
//        // P*(V*M) ===============================================================================================================
//
//        Matrix.setIdentityM(mMatrixMVP, 0);
//
//        // Compute ModelView Matrix
//        Matrix.multiplyMM(mMatrixMVP, 0, mMatrixView, 0, mMatrixModel, 0);
//
//        // Compute ModelViewProjection Matrix
//        Matrix.multiplyMM(mMatrixMVP, 0, mMatrixProjection, 0, mMatrixMVP, 0);

        // ========================================================================================================================

        // Bind shader program
        glUseProgram(mShaderProgram);


        // Set Uniform data
//        glUniformMatrix4fv(mLocationMVPMatrix, 1, false, mMatrixMVP, 0);
//        GLESUtil.checkGlError("glUniformMatrix4fv");

        mPositionHandle = glGetAttribLocation(mShaderProgram, "aPosition");

        glEnableVertexAttribArray(mPositionHandle);

        glVertexAttribPointer(mPositionHandle,
                COORDS_PER_VERTEX,
                GL_FLOAT,
                false,
                mTriangle.vertexDataStride,
                mTriangle.vertexBuffer);

        // Draw vertices
        glDrawArrays(GL_TRIANGLES, 0, mTriangle.noVertices);

        glDisableVertexAttribArray(mPositionHandle);

    }

    // Shader related

    public static int createProgram(String vertexSource, String fragmentSource) {
        int vertexShader = loadShader(GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == SHADER_COMPILED_WITH_ERROR) {
            return PROGRAM_COMPILED_WITH_ERROR;
        }

        int pixelShader = loadShader(GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == SHADER_COMPILED_WITH_ERROR) {
            return PROGRAM_COMPILED_WITH_ERROR;
        }

        int program = glCreateProgram();
        if (program != PROGRAM_COMPILED_WITH_ERROR) {
            glAttachShader(program, vertexShader);
            glAttachShader(program, pixelShader);
            glLinkProgram(program);
            int[] linkStatus = new int[1];
            glGetProgramiv(program, GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GL_TRUE) {
                Log.e(TAG, "Could not link program: ");
                Log.e(TAG, glGetProgramInfoLog(program));
                glDeleteProgram(program);
                program = PROGRAM_COMPILED_WITH_ERROR;
            }
        }
        return program;
    }

    private static int loadShader(int shaderType, String source) {
        int shader = glCreateShader(shaderType);
        if (shader != SHADER_COMPILED_WITH_ERROR) {
            glShaderSource(shader, source);
            glCompileShader(shader);
            int[] compiled = new int[1];
            glGetShaderiv(shader, GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == SHADER_COMPILED_WITH_ERROR) {
                Log.e(TAG, getShaderTypeName(shaderType) + " compile failed: " + shaderType + ":");
                Log.e(TAG, glGetShaderInfoLog(shader));
                glDeleteShader(shader);
                shader = SHADER_COMPILED_WITH_ERROR;
            }
        }
        return shader;
    }


    private static String getShaderTypeName(int shaderType) {
        switch (shaderType) {
            case GL_FRAGMENT_SHADER:
                return "Fragment Shader";
            case GL_VERTEX_SHADER:
                return "Vertex Shader";
        }
        return "Unknown Shader";
    }


    // Model related

    float triangleCoords[] = { // in counterclockwise order:
            0.0f, 0.622008459f, 0.0f,   // top
            -0.5f, -0.311004243f, 0.0f,   // bottom left
            0.5f, -0.311004243f, 0.0f    // bottom right
    };

    protected GLObject createSimpleTexturedQuad() {
        // For simplifying the reading of the defined quadVertices array
        final int X = 0;
        final int Y = 1;
        final int Z = 2;
        final int U = 3;
        final int V = 4;

        final int vertexDataStride = COORDS_PER_VERTEX * 4;   // I.e. there are 5 floats between each defined point.
        final int positionOffset = 0;     // The offset for each vertex where the position attributes start.
        final int texCoordOffset = 0; // The offset for texture coordinate.

        // The four vectors of the quad
//        final float v0[] = {-.5f, -.5f, 0, 0, 0};
//        final float v1[] = { .5f, -.5f, 0, 1, 0};
//        final float v2[] = { .5f,  .5f, 0, 1, 1};
//        final float v3[] = {-.5f,  .5f, 0, 0, 1};

//        final float[] quadVertices = {
//                First triangle
//                v0[X], v0[Y], v0[Z], //v0[U], v0[V],
//                v1[X], v1[Y], v1[Z], //v1[U], v1[V],
//                v2[X], v2[Y], v2[Z], //v2[U], v2[V],
//
//                Second triangle
//                v0[X], v0[Y], v0[Z], //v0[U], v0[V],
//                v2[X], v2[Y], v2[Z], //v2[U], v2[V],
//                v3[X], v3[Y], v3[Z], //v3[U], v3[V],
//        };

        // not quad..

        return new GLObject(triangleCoords, vertexDataStride, positionOffset, texCoordOffset);
    }


    // Utils

    public static class GLObject {

        public final FloatBuffer vertexBuffer;
        public final int vertexDataStride;
        public final int positionOffset;
        public final int textureCoordOffset;
        public final int noVertices;


        public GLObject(final float[] triangleCoords, final int vertexDataStride, final int positionOffset, final int texCoordOffset) {
            this.positionOffset = positionOffset;
            this.textureCoordOffset = texCoordOffset;
            this.vertexBuffer = allocateNativeFloatBuffer(triangleCoords);
            this.noVertices = triangleCoords.length / COORDS_PER_VERTEX;
            this.vertexDataStride = vertexDataStride;
        }

    }

    /**
     * TODO
     *
     * @param vertices The vertices of whom we will allocate data for.
     * @return The allocated data.
     */
    private static FloatBuffer allocateNativeFloatBuffer(float[] vertices) {
        int bytes_per_float = 4;
        final FloatBuffer buffer = ByteBuffer.allocateDirect(vertices.length * bytes_per_float)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        buffer.put(vertices).position(0);

        return buffer;
    }
}

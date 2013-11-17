package com.jayway.oglhelloworld.ogl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.jayway.oglhelloworld.R;
import com.jayway.oglhelloworld.util.Log;

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
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_NEAREST;
import static android.opengl.GLES20.GL_REPEAT;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_TRUE;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glTexParameterf;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

/**
 * Only renders a quad with a color
 */

public class SimpleRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = SimpleRenderer.class.getSimpleName();
    /**
     * In bytes
     */
    private static final int SIZE_OF_FLOAT = 4;
    private static final Log LOG = new Log(SimpleRenderer.class);

    private final Context mContext;

    // Constants
    public static final float[] CLEAR_COLOR = {0.2f, 0.2f, 0.2f, 1f};


    // Camera/View related
    private static final float NEAR_PLANE = .1f;
    private static final float FAR_PLANE = 10f;
    private static final float FOV = 45; // Field of view

    private final float[] up = {0f, 1f, 0f};
    private final float[] eye = {0f, 0f, 5f};
    private final float[] center = {0f, 0f, 0f};

    // Matrices
    private float[] mMVPMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];
    private float[] mViewMatrix = new float[16];

    // Shader Constants
    private static final int SHADER_COMPILED_WITH_ERROR = 0;
    private static final int PROGRAM_COMPILED_WITH_ERROR = 0;

    private int mShaderProgram = PROGRAM_COMPILED_WITH_ERROR;

    // Shader Handles
    private int mMVPMatrixHandle;
    private int mPositionHandle;
    private int mTextureCoordinateHandle;
    private int mTextureHandle;

    // 3D Objects
    private GLObject mGLObject;

    // Shader source uniform and attribute variable names
    private static final String U_MVP_MATRIX = "uMVPMatrix";
    private static final String U_TEXURE01 = "uTexture01";

    private static final String A_POSITION = "aPosition";
    private static final String A_TEXTURE_COORDINATE = "aTexCoordinate"; // also known as UV-coordinate

    private final String mVertexShader =
                      "attribute vec4 " + A_POSITION + ";"
                    + "attribute vec2 " + A_TEXTURE_COORDINATE + ";"
                    + "uniform mat4 " + U_MVP_MATRIX + ";"
                    + "varying vec2 uv;"
                    + "void main() {"
                    + "  uv = " + A_TEXTURE_COORDINATE + ";"
                    + "  gl_Position = " + U_MVP_MATRIX + "*" + A_POSITION + ";"
                    + "}";

    private final String mFragmentShader =
                      "precision mediump float;"
                    + "varying vec2 uv;"
                    + "uniform sampler2D " + U_TEXURE01 + ";"
                    + "void main() {"
                    + "  gl_FragColor = texture2D(" + U_TEXURE01 + ", vec2(uv.x, 1.0-uv.y));"
                    + "}";

    public SimpleRenderer(Context context) {
        mContext = context;

        Matrix.setIdentityM(mProjectionMatrix, 0);
        Matrix.setIdentityM(mViewMatrix, 0);
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        final boolean useTextureCoordinates = true;

        // Setup opengl
        glClearColor(CLEAR_COLOR[0], CLEAR_COLOR[1], CLEAR_COLOR[2], CLEAR_COLOR[3]);

        // Initiate objects
//        mGLObject = createSimpleTriangle();
        mGLObject = createSimpleQuad(useTextureCoordinates);

        mShaderProgram = createProgram(mVertexShader, mFragmentShader);

        // Setup uniform and attributes
        if (mShaderProgram != PROGRAM_COMPILED_WITH_ERROR) {
            glUseProgram(mShaderProgram);
            // bind Uniforms and Attributes
            mMVPMatrixHandle = glGetUniformLocation(mShaderProgram, U_MVP_MATRIX);

            if (mMVPMatrixHandle == -1) {
                LOG.w("Failed binding: " + U_MVP_MATRIX);
            }

            mPositionHandle = glGetAttribLocation(mShaderProgram, A_POSITION);
            if (mPositionHandle == -1) {
                LOG.w("Failed binding: " + A_POSITION);
            }

            if (useTextureCoordinates) {
                mTextureCoordinateHandle = glGetAttribLocation(mShaderProgram, A_TEXTURE_COORDINATE);
                if (mTextureCoordinateHandle == -1) {
                    LOG.w("Failed binding: " + A_TEXTURE_COORDINATE);
                }

            }
        }

        mTextureHandle = loadTexture(R.drawable.jayway);

        // Setup view matrix
        Matrix.setLookAtM(mViewMatrix, 0,
                eye[0], eye[1], eye[2],
                center[0], center[1], center[2],
                up[0], up[1], up[2]);
    }

    private int loadTexture(int resId) {
        // Normally you want to load resources in the background and show something else mean while
        // But since we only have one small texture it does not create
        final Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), resId);

        // Generate texture id
        int[] textures = {-1};
        glGenTextures(textures.length, textures, 0);

        // verify generated texture id is not -1

        if (textures[0] == -1) {
            LOG.e("Failed generating texture id");
        }
        
        glBindTexture(GL_TEXTURE_2D, textures[0]);

        // Setup texture parameters
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        // Upload texture to OpenGL
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);

        // Recycle bitmap from memory
        bitmap.recycle();

        return textures[0];
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        glViewport(0, 0, width, height);

        // Setup projection
        final float aspect = (float) width / height;
        Matrix.perspectiveM(mProjectionMatrix, 0, FOV, aspect, NEAR_PLANE, FAR_PLANE);
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        glClear(GL_COLOR_BUFFER_BIT);

        // MVP matrix computation
        // P*(V*M) ===============================================================================================================

        Matrix.setIdentityM(mMVPMatrix, 0);

        // Compute ModelView Matrix
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mGLObject.modelMatrix, 0);

        // Compute ModelViewProjection Matrix
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        // ========================================================================================================================

        // Bind shader program
        glUseProgram(mShaderProgram);

        // activete texture unit
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, mTextureHandle);

        // Set Uniform data
        glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        // Bind position coordinates, e.g. x,y and z.
        mGLObject.vertexBuffer.position(mGLObject.positionOffset);
        glEnableVertexAttribArray(mPositionHandle);
        glVertexAttribPointer(mPositionHandle,
                mGLObject.vertexPositionDimension,
                GL_FLOAT,
                false,
                mGLObject.vertexDataStride,
                mGLObject.vertexBuffer);

        // Bind texture coordinates, e.g. u and v.
        mGLObject.vertexBuffer.position(mGLObject.uvOffset);
        glEnableVertexAttribArray(mTextureCoordinateHandle);
        glVertexAttribPointer(mTextureCoordinateHandle,
                mGLObject.vertexTexturedCoordinateDimension,
                GL_FLOAT,
                false,
                mGLObject.vertexDataStride,
                mGLObject.vertexBuffer);


        // Draw vertices
        glDrawArrays(GL_TRIANGLES, 0, mGLObject.noVertices);
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
                LOG.e("Could not link program: ");
                LOG.e(glGetProgramInfoLog(program));
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
                LOG.e(getShaderTypeName(shaderType) + " compile failed: " + shaderType + ":");
                LOG.e(glGetShaderInfoLog(shader));
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

    protected GLObject createSimpleTriangle() {
        final float triangleCoords[] = {      // in counterclockwise order:
                0.0f, 0.622008459f, 0.0f,   // top
                -0.5f, -0.311004243f, 0.0f,   // bottom left
                0.5f, -0.311004243f, 0.0f    // bottom right
        };

        // For simplifying the reading of the defined quadVertices array
        final int X = 0;
        final int Y = 1;
        final int Z = 2;
        final int U = 3;
        final int V = 4;

        final int coordinatePerVertex = 3;

        final int vertexDataStride = coordinatePerVertex * 4;   // I.e. there are 5 floats between each defined point.
        final int positionOffset = 0;     // The offset for each vertex where the position attributes start.
        final int texCoordOffset = 0; // The offset for texture coordinate.

        return new GLObject(new AbstractVertexType.VertexType(), triangleCoords);
    }

    protected GLObject createSimpleQuad(boolean useTexCoords) {
        // For simplifying the reading of the defined quadVertices array
        final int x = 0;
        final int y = 1;
        final int z = 2;
        final int u = 3;
        final int v = 4;

        // The four vectors of the quad
        final float v0[] = {-.5f, -.5f, 0, 0, 0};
        final float v1[] = { .5f, -.5f, 0, 1, 0};
        final float v2[] = { .5f,  .5f, 0, 1, 1};
        final float v3[] = {-.5f,  .5f, 0, 0, 1};

        float[] verticesNoUVs = {
                //First triangle
                v0[x], v0[y], v0[z],
                v1[x], v1[y], v1[z],
                v2[x], v2[y], v2[z],

                //Second triangle
                v0[x], v0[y], v0[z],
                v2[x], v2[y], v2[z],
                v3[x], v3[y], v3[z],
        };

        float[] verticesWithUVs = {
                //First triangle
                v0[x], v0[y], v0[z], v0[u], v0[v],
                v1[x], v1[y], v1[z], v1[u], v1[v],
                v2[x], v2[y], v2[z], v2[u], v2[v],
                //Second triangle
                v0[x], v0[y], v0[z], v0[u], v0[v],
                v2[x], v2[y], v2[z], v2[u], v2[v],
                v3[x], v3[y], v3[z], v3[u], v3[v],
        };

        final float[] quadVertices = useTexCoords ? verticesWithUVs : verticesNoUVs;

        return new GLObject(new AbstractVertexType.TexturedVertexType(), quadVertices);
    }

    public GLObject getGlObject() {
        return mGLObject;
    }


    // Utils

    public static class GLObject {
        public float[] modelMatrix = new float[16];

        public final FloatBuffer vertexBuffer;
        public final int vertexDataStride;
        public final int positionOffset;
        public final int uvOffset;
        public final int noVertices;
        public final int vertexPositionDimension;
        public final int vertexTexturedCoordinateDimension;

        public GLObject(AbstractVertexType abstractVertexType, final float[] vertexData) {
            this.positionOffset = abstractVertexType.getPositionOffset();
            this.uvOffset = abstractVertexType.getUVOffset();

            this.vertexBuffer = allocateNativeFloatBuffer(vertexData);

            this.noVertices = vertexData.length / abstractVertexType.getDimensionPerVertex();

            this.vertexDataStride = abstractVertexType.getDataStrideInBytes();

            this.vertexPositionDimension = uvOffset;
            this.vertexTexturedCoordinateDimension = (vertexDataStride / 4) - uvOffset;

            Matrix.setIdentityM(modelMatrix, 0);
        }

        // todo here we can animate..
        public void update(final float dt) {
            final float degreesPerSecond = 15;
            Matrix.rotateM(modelMatrix, 0, degreesPerSecond * dt, 0, 0, 1);
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
}

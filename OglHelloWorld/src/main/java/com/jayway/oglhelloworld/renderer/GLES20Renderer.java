package com.jayway.oglhelloworld.renderer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.jayway.oglhelloworld.R;
import com.jayway.oglhelloworld.ogl.GLObjectFactory;
import com.jayway.oglhelloworld.ogl.VertexType;
import com.jayway.oglhelloworld.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_BACK;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
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
import static android.opengl.GLES20.glCullFace;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnable;
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
import static android.opengl.GLES20.glUniform3fv;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

public class GLES20Renderer implements GLSurfaceView.Renderer {
    private static final Log LOG = new Log(GLES20Renderer.class);

    private final Context mContext;

    // Constants
    public static final float[] CLEAR_COLOR = {0.5f, 0.5f, 0.5f, 1f};
    public final boolean USE_TEXTURE_COORDINATES = true;
    public final boolean USE_NORMALS = true;

    // Camera/View related
    private static final float NEAR_PLANE = .1f;
    private static final float FAR_PLANE = 10f;
    private static final float FIELD_OF_VIEW = 45;

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
    private int mUVHandle;
    private int mNormalHandle;
    private int mCameraHandle;

    // Texture id's
    private int mTextureId;

    // 3D Objects
    private List<GLObject> mGlObjectList = new ArrayList<>();
    private int mSelectedGLObject = 0;

    // Shader source uniform and attribute variable names
    private static final String U_MVP_MATRIX = "uMVPMatrix";
    private static final String U_TEXTURE_01 = "uTexture01";
    private static final String U_CAMERA = "uCamera";

    private static final String A_POSITION = "aPosition";
    private static final String A_TEXTURE_COORDINATE = "aUV"; // also known as UV-coordinate
    private static final String A_NORMAL = "aNormal";

    private final String mVertexShader =
                      "attribute vec3 " + A_POSITION + ";"
                    + "attribute vec2 " + A_TEXTURE_COORDINATE + ";"
                    + "uniform mat4 " + U_MVP_MATRIX + ";"
                    + "varying vec2 uv;"
                    + "void main() {"
                    + "  uv = " + A_TEXTURE_COORDINATE + ";"
                    + "  gl_Position = " + U_MVP_MATRIX + "* vec4(" + A_POSITION + ",1.0);"
                    + "}";

    private final String mFragmentShader =
                      "precision mediump float;"
                    + "uniform sampler2D " + U_TEXTURE_01 + ";"
                    + "varying vec2 uv;"
                    + "void main() {"
                    + "  gl_FragColor = texture2D(" + U_TEXTURE_01 + ", vec2(uv.x, 1.0-uv.y));"
                    + "}";
    public GLES20Renderer(Context context) {
        mContext = context;

        Matrix.setIdentityM(mProjectionMatrix, 0);
        Matrix.setIdentityM(mViewMatrix, 0);
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Setup OpenGL
        glClearColor(CLEAR_COLOR[0], CLEAR_COLOR[1], CLEAR_COLOR[2], CLEAR_COLOR[3]);
        glEnable(GL_DEPTH_TEST);
        glCullFace(GL_BACK);

        // Initiate objects
        createGLObjects(USE_TEXTURE_COORDINATES, USE_NORMALS);

        if (USE_NORMALS) {
            try {
                final String vs = loadShaderSourceFromRaw(R.raw.advanced_vs);
                final String fs = loadShaderSourceFromRaw(R.raw.advanved_fs);

                mShaderProgram = createProgram(vs, fs);
            } catch (IOException e) {
                LOG.e("Failed loading shaders form raw", e);

            }
        } else {
            mShaderProgram = createProgram(mVertexShader, mFragmentShader);
        }

        if (mShaderProgram != PROGRAM_COMPILED_WITH_ERROR) {
            bindUniformsAndAttributes(mShaderProgram, USE_TEXTURE_COORDINATES, USE_NORMALS);

            mTextureId = loadTexture(R.drawable.jayway);

            // Setup view matrix
            Matrix.setLookAtM(mViewMatrix, 0,
                    eye[0], eye[1], eye[2],
                    center[0], center[1], center[2],
                    up[0], up[1], up[2]);

        } else {
            LOG.w("Shader failed compiling");
        }
    }

    private void bindUniformsAndAttributes(int program, final boolean useTextures, final boolean useNormals) {
        // Setup uniform and attributes
        if (program != PROGRAM_COMPILED_WITH_ERROR) {
            glUseProgram(program);

            // Bind uniform handles
            mMVPMatrixHandle = glGetUniformLocation(program, U_MVP_MATRIX);
            if (mMVPMatrixHandle == -1) {
                LOG.w("Failed binding: " + U_MVP_MATRIX);
            }

            mCameraHandle = glGetUniformLocation(program, U_CAMERA);
            if (mPositionHandle == -1) {
                LOG.w("Failed binding: " + A_POSITION);
            }


            // Bind attribute handles
            mPositionHandle = glGetAttribLocation(program, A_POSITION);
            if (mPositionHandle == -1) {
                LOG.w("Failed binding: " + A_POSITION);
            }

            if (useTextures) {
                mUVHandle = glGetAttribLocation(program, A_TEXTURE_COORDINATE);
                if (mUVHandle == -1) {
                    LOG.w("Failed binding: " + A_TEXTURE_COORDINATE);
                }
            }

            if (useNormals) {
                mNormalHandle = glGetAttribLocation(program, A_NORMAL);
                if (mNormalHandle == -1) {
                    LOG.w("Failed binding: " + A_NORMAL);
                }
            }
        }
    }

    private void createGLObjects(boolean useUVs, boolean useNormals) {
        mGlObjectList.add(GLObjectFactory.createSimpleTriangle(useUVs, useNormals));
        mGlObjectList.add(GLObjectFactory.createSimpleQuad(useUVs, useNormals));
        mGlObjectList.add(GLObjectFactory.createCube(1, 1, 1, useUVs, useNormals));
        mGlObjectList.add(GLObjectFactory.createCubeWithFlatNormals(1, 1, 1, useUVs, useNormals));
        mGlObjectList.add(GLObjectFactory.createTorus(0.7f, 0.4f, 40, 40, useUVs, useNormals));
        // More objects can be added here
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
        Matrix.perspectiveM(mProjectionMatrix, 0, FIELD_OF_VIEW, aspect, NEAR_PLANE, FAR_PLANE);
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        GLObject glObject = mGlObjectList.get(mSelectedGLObject);

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // MVP matrix computation
        // P*(V*M) ===============================================================================================================

        Matrix.setIdentityM(mMVPMatrix, 0);

        // Compute ModelView Matrix
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, glObject.modelMatrix, 0);

        // Compute ModelViewProjection Matrix
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        // ========================================================================================================================

        // Bind shader program
        glUseProgram(mShaderProgram);

        // Bind camera to shader
        glUniform3fv(mCameraHandle, 1, eye, 0);

        // activate texture unit
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, mTextureId);

        // Set Uniform data
        glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        // Bind position coordinates, e.g. x,y and z.
        glObject.vBuffer.position(glObject.vPosOffset);
        glEnableVertexAttribArray(mPositionHandle);
        glVertexAttribPointer(mPositionHandle,
                glObject.vPosDimension,
                GL_FLOAT,
                false,
                glObject.vDataStride,
                glObject.vBuffer);

        if(glObject.hasTextureCoordinates()) {
            // Bind texture coordinates, e.g. u and v.
            glObject.vBuffer.position(glObject.vUVOffset);
            glEnableVertexAttribArray(mUVHandle);
            glVertexAttribPointer(mUVHandle,
                    glObject.vUVDimension,
                    GL_FLOAT,
                    false,
                    glObject.vDataStride,
                    glObject.vBuffer);
        }

        if (glObject.hasNormals()) {
            // Bind texture coordinates, e.g. u and v.
            glObject.vBuffer.position(glObject.vNormOffset);
            glEnableVertexAttribArray(mNormalHandle);
            glVertexAttribPointer(mNormalHandle,
                    glObject.vNormaDimension,
                    GL_FLOAT,
                    false,
                    glObject.vDataStride,
                    glObject.vBuffer);
        }

        // Draw vertices
        glDrawArrays(glObject.glRenderMode, 0, glObject.vCount);
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

    public GLObject getSelectedGlObject() {
        return mGlObjectList.get(mSelectedGLObject);
    }

    public GLObject nextGLObject() {
        mSelectedGLObject = mSelectedGLObject == mGlObjectList.size() - 1
                ? 0
                : mSelectedGLObject + 1;

        return mGlObjectList.get(mSelectedGLObject);
    }


    // Utils

    /**
     * Data value object holding references to what is needed to draw an array with
     * OpenGL.
     */
    public static class GLObject {

        // Exposed directly for performance reasons
        public float[] modelMatrix = new float[16];

        public final FloatBuffer vBuffer;
        public final int vDataStride;
        public final int vCount;

        public final int vPosOffset;
        public final int vPosDimension;

        public final int vUVOffset;
        public final int vUVDimension;

        public final int vNormaDimension;
        public final int vNormOffset;
        public final String title;

        public final int glRenderMode;

        public GLObject(String title, VertexType vertexType, float[] vertexData) {
            this(title, vertexType, vertexData, GL_TRIANGLES);

        }

        public GLObject(String title, VertexType vertexType, float[] vertexData, int glRenderMode) {
            this.title = title;
            /**
             * prefix v means vertex.
             */
            this.vBuffer = allocateNativeFloatBuffer(vertexData);
            this.vCount = vertexData.length / vertexType.getDimension();
            this.vDataStride = vertexType.getDataStrideInBytes();

            this.vPosOffset = vertexType.getPositionOffset();
            this.vPosDimension = vertexType.getPositionCount();

            this.vUVOffset = vertexType.getUVOffset();
            this.vUVDimension = vertexType.getUVCount();

            this.vNormOffset = vertexType.getNormalOffset();
            this.vNormaDimension = vertexType.getNormalCount();

            this.glRenderMode = glRenderMode;

            Matrix.setIdentityM(modelMatrix, 0);
        }

        // Animation callback
        public void update(final float dt) {
            final float degreesPerSecond = 60;
            Matrix.rotateM(modelMatrix, 0, degreesPerSecond * dt, 0, 0, 1);
            Matrix.rotateM(modelMatrix, 0, degreesPerSecond * 1.5f * dt, 1, 0, 0);
            Matrix.rotateM(modelMatrix, 0, degreesPerSecond * dt, 0, 1, 0);
        }

        public boolean hasTextureCoordinates() {
            return vUVDimension > 0;
        }

        public boolean hasNormals() {
            return vNormaDimension > 0;
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
            // Fix for enabling lazy style comments..
            final int i = readLine.indexOf("//");
            if(i != -1) {
                sb.append(readLine.substring(0, i));
            } else {
                sb.append(readLine);
            }
        }

        return sb.toString();
    }
}

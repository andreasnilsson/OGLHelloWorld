package com.jayway.oglhelloworld.renderer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.jayway.oglhelloworld.R;
import com.jayway.oglhelloworld.glutil.ShaderUtil;
import com.jayway.oglhelloworld.ogl.GLObject;
import com.jayway.oglhelloworld.ogl.GLObjectDB;
import com.jayway.oglhelloworld.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_BACK;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_REPEAT;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glCullFace;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glTexParameterf;
import static android.opengl.GLES20.glUniform3fv;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

/**
 * A simple {@link android.opengl.GLES20} Renderer.
 *
 * @author Andreas Nilsson
 */
public class GLES20Renderer implements GLSurfaceView.Renderer {
    private static final Log LOG = new Log(GLES20Renderer.class);

    private final Context mContext;

    private GLObjectDB objectDB = GLObjectDB.getInstance();
    private GLObject mCurrentObject = objectDB.getSelectedObject();

    // Constants
    public static final float[] CLEAR_COLOR = {0.5f, 0.5f, 0.5f, 1f};
    public static final boolean USE_TEXTURE_COORDINATES = true;
    public static final boolean USE_NORMALS             = true;

    // Camera/View related
    private static final float NEAR_PLANE    = .1f;
    private static final float FAR_PLANE     = 10f;
    private static final float FIELD_OF_VIEW = 45;

    private final float[] up     = {0f, 1f, 0f};
    private final float[] eye    = {0f, 0f, 5f};
    private final float[] center = {0f, 0f, 0f};

    // Matrices
    private float[] mMVPMatrix        = new float[16];
    private float[] mProjectionMatrix = new float[16];
    private float[] mViewMatrix       = new float[16];

    // Shader program
    private int mShaderProgram = ShaderUtil.CREATE_PROGRAM_FAILED;

    // Shader Handles
    private int mMVPMatrixHandle;
    private int mPositionHandle;
    private int mUVHandle;
    private int mNormalHandle;
    private int mCameraHandle;

    // Texture id's
    private int mTextureId;

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
                      "precision highp float;"
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
        // Setup DemoOpenGL
        glClearColor(CLEAR_COLOR[0], CLEAR_COLOR[1], CLEAR_COLOR[2], CLEAR_COLOR[3]);
        glEnable(GL_DEPTH_TEST);
        glCullFace(GL_BACK); // Only draw triangles that are facing us.

        // Compile shaders
        String vs = ShaderUtil.loadShaderSourceFromRaw(mContext, R.raw.advanced_vs);
        String fs = ShaderUtil.loadShaderSourceFromRaw(mContext, R.raw.advanved_fs);

        mShaderProgram = ShaderUtil.createAndLinkShaderProgram(vs, fs);

        if (mShaderProgram != ShaderUtil.CREATE_PROGRAM_FAILED) {
            // Bind Qualifiers to shader program
            bindQualifiers(mShaderProgram, USE_TEXTURE_COORDINATES, USE_NORMALS);

            // Load textures
            mTextureId = loadTexture(R.drawable.jayway);

            // Setup view matrix
            Matrix.setLookAtM(mViewMatrix, 0,
                    eye[0], eye[1], eye[2],
                    center[0], center[1], center[2],
                    up[0], up[1], up[2]);

        } else {
            LOG.w("Shader compilation failed");
        }
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
        if (mCurrentObject == null) return;
        final GLObject glObject = mCurrentObject;

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

        if (glObject.hasTextureCoordinates()) {
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

    /**
     * Binds handles with the shader {@code program}.
     *
     * @param program     The DemoOpenGL shader program.
     * @param useTextures {@code true} if we want to use textures.
     * @param useNormals  {@code true} if we want to use normals.
     */
    private void bindQualifiers(int program, final boolean useTextures, final boolean useNormals) {
        // Setup uniform and attributes
        if (program != ShaderUtil.CREATE_PROGRAM_FAILED) {
            glUseProgram(program);

            // Bind uniforms
            mMVPMatrixHandle = glGetUniformLocation(program, U_MVP_MATRIX);
            if (mMVPMatrixHandle == -1) {
                LOG.w("Failed binding: " + U_MVP_MATRIX);
            }

            mCameraHandle = glGetUniformLocation(program, U_CAMERA);
            if (mPositionHandle == -1) {
                LOG.w("Failed binding: " + A_POSITION);
            }

            // Bind attributes
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

    /**
     * Loads a drawable resource as texture into the graphics memory.
     *
     * @param resId The drawable resource id.
     * @return The texture handle
     */
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
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        // Upload texture to DemoOpenGL
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);

        // Bitmap is uploaded to the graphics memory so we can recycle it from memory.
        bitmap.recycle();

        return textures[0];
    }

    /**
     * Set the {@link com.jayway.oglhelloworld.ogl.GLObject} target to render.
     *
     * @param target The target.
     */
    public void setTarget(final GLObject target) {
        mCurrentObject = target;
    }
}

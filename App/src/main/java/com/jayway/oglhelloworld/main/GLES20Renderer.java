package com.jayway.oglhelloworld.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.jayway.oglhelloworld.R;
import com.jayway.oglhelloworld.gl.GlObject;
import com.jayway.oglhelloworld.gl.GlObjectManager;
import com.jayway.oglhelloworld.util.Log;
import com.jayway.oglhelloworld.gl.util.ShaderUtil;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;

/**
 * A simple {@link android.opengl.GLES20} Renderer.
 *
 * @author Andreas Nilsson
 */
public class GLES20Renderer implements GLSurfaceView.Renderer {
    private static final Log LOG = new Log(GLES20Renderer.class);

    private final Context mContext;

    private GlObjectManager mObjectManager = GlObjectManager.getInstance();
    private GlObject mSelectedObject = mObjectManager.getSelectedObject();

    // Constants
    public static final float[] CLEAR_COLOR = {0.5f, 0.5f, 0.5f, 1f}; // 50% Grey
    public static final boolean USE_TEXTURE_COORDINATES = true;
    public static final boolean USE_NORMALS = true;

    // Camera/View related
    private static final float NEAR_PLANE    = .1f;
    private static final float FAR_PLANE     = 100f;
    private static final float FIELD_OF_VIEW = 45;

    private static final float[] UP     = {0f, 1f, 0f};
    private static final float[] EYE    = {0f, 0f, 5f};
    private static final float[] CENTER = {0f, 0f, 0f};

    // Matrices
    private static float[] sTempMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];
    private float[] mModelViewMatrix = new float[16];

    private float[] mProjectionMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mNormalMatrix = new float[16];

    // Shader program
    private int mShaderProgram = ShaderUtil.CREATE_PROGRAM_FAILED;

    // Shader Handles
    private int mMVPMatrixHandle;
    private int mNormalMatrixHandle;
    private int mPositionHandle;
    private int mUVHandle;
    private int mNormalHandle;

    // Texture ids
    private int mTextureId = -1;

    // Shader source uniform/attribute variable names
    private static final String U_MVP_MATRIX         = "mvp_matrix";
    private static final String U_NORMAL_MATRIX      = "normal_matrix";
    private static final String U_TEXTURE_01         = "texture01";

    private static final String A_POSITION           = "a_position";
    private static final String A_TEXTURE_COORDINATE = "a_texcoord";
    private static final String A_NORMAL             = "a_normal";

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

        setIdentity(mProjectionMatrix);
        setIdentity(mViewMatrix);
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Setup OpenGL
        glClearColor(CLEAR_COLOR[0], CLEAR_COLOR[1], CLEAR_COLOR[2], CLEAR_COLOR[3]);
        glEnable(GL_DEPTH_TEST);

        float[][] allMatrices = {
                sTempMatrix,
                mMVPMatrix,
                mModelViewMatrix,
                mProjectionMatrix,
                mViewMatrix,
                mNormalMatrix,
        };

        setIdentity(allMatrices);

        // Compile shaders
        String vs;
        String fs;
        if (USE_NORMALS) {
            vs = ShaderUtil.loadShaderSourceFromRaw(mContext, R.raw.advanced_vs);
            fs = ShaderUtil.loadShaderSourceFromRaw(mContext, R.raw.advanved_fs);
        } else {
            vs = mVertexShader;
            fs = mFragmentShader;
        }

        mShaderProgram = ShaderUtil.createAndLinkShaderProgram(vs, fs);

        if (mShaderProgram != ShaderUtil.CREATE_PROGRAM_FAILED) {
            // Bind Qualifiers to shader program
            getQualifierHandles(mShaderProgram, USE_TEXTURE_COORDINATES, USE_NORMALS);

            // Load textures
            mTextureId = loadTexture(R.drawable.jayway);

            // Setup view matrix
            Matrix.setLookAtM(mViewMatrix, 0,
                              EYE[0],    EYE[1],    EYE[2],
                              CENTER[0], CENTER[1], CENTER[2],
                              UP[0],     UP[1],     UP[2]);

        } else {
            LOG.w("Shader compilation failed");
        }
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        glViewport(0, 0, width, height);

        // Setup projection
        setIdentity(mProjectionMatrix);
        final float aspectRatio = (float) width / height;
        Matrix.perspectiveM(mProjectionMatrix, 0, FIELD_OF_VIEW, aspectRatio, NEAR_PLANE, FAR_PLANE);
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        // If we have no object there is no point in drawing anything
        if (mSelectedObject == null) {
            LOG.w("There is no object selected to draw!");
            return;
        }

        final GlObject glObject = mSelectedObject;

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // MATRIX: Computations ===================================================================================================
        // Due to how matrix multiplications work(Not commutative) we have to multiply them in this order: P*(V*M)
        setIdentity(mMVPMatrix);

        // Compute Model-View Matrix
        Matrix.multiplyMM(mModelViewMatrix, 0, mViewMatrix, 0, glObject.modelMatrix, 0);

        // Compute Model-View-Projection Matrix
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mModelViewMatrix, 0);

        // Compute Normal matrix
        computeNormalMatrix(mNormalMatrix, mModelViewMatrix);
        // MATRIX: END ============================================================================================================


        // SHADER PROGRAM: BIND ===================================================================================================
        glUseProgram(mShaderProgram);


        // UNIFORMS: Bind =========================================================================================================
        // activate texture unit (Not needed if you are only using 1 texture)
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, mTextureId);

        // Set Uniform data
        glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        glUniformMatrix4fv(mNormalMatrixHandle, 1, false, mNormalMatrix, 0);
        // UNIFORMS: End ==========================================================================================================


        // ATTRIBUTES: Bind =======================================================================================================
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
        // ATTRIBUTES: End ========================================================================================================


        // DRAW VERTICES ==========================================================================================================
        glDrawArrays(glObject.glRenderMode, 0, glObject.vCount);
    }

    private void getQualifierHandles(int program, final boolean useTextures, final boolean useNormals) {
        // Setup uniform and attributes
        if (program != ShaderUtil.CREATE_PROGRAM_FAILED) {
            glUseProgram(program);

            // Bind uniforms
            mMVPMatrixHandle = glGetUniformLocation(program, U_MVP_MATRIX);
            if (mMVPMatrixHandle == -1) {
                LOG.e("Failed binding: " + U_MVP_MATRIX);
            }

            mNormalMatrixHandle = glGetUniformLocation(program, U_NORMAL_MATRIX);
            if (mNormalHandle == -1) {
                LOG.e("Failed getting handle for: " + U_NORMAL_MATRIX);
            }

            // Bind attributes
            mPositionHandle = glGetAttribLocation(program, A_POSITION);
            if (mPositionHandle == -1) {
                LOG.e("Failed getting handle for: " + A_POSITION);
            }

            if (useTextures) {
                mUVHandle = glGetAttribLocation(program, A_TEXTURE_COORDINATE);
                if (mUVHandle == -1) {
                    LOG.e("Failed getting handle for: " + A_TEXTURE_COORDINATE);
                }
            }

            if (useNormals) {
                mNormalHandle = glGetAttribLocation(program, A_NORMAL);
                if (mNormalHandle == -1) {
                    LOG.e("Failed getting handle for: " + A_NORMAL);
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
     * Set the {@link com.jayway.oglhelloworld.gl.GlObject} target to render.
     *
     * @param target The target.
     */
    public void setTarget(final GlObject target) {
        mSelectedObject = target;
    }

    // Matrix helper methods

    /**
     * @param outNormalMatrix The computed normal matrix.
     * @param modelViewMatrix The model view matrix.
     */
    public static void computeNormalMatrix(float[] outNormalMatrix, float[] modelViewMatrix) {
        setIdentity(outNormalMatrix, sTempMatrix);

        final boolean wasInverted = Matrix.invertM(sTempMatrix, 0, modelViewMatrix, 0);
        Matrix.transposeM(outNormalMatrix, 0, sTempMatrix, 0);

        if (wasInverted) {
            Matrix.transposeM(outNormalMatrix, 0, sTempMatrix, 0);
        } else {
            LOG.e("Could not invert ModelView matrix, returning identity");
        }
    }

    /**
     * Assumes that each matrix is an array of 16 floats.
     *
     * @param matrices The matrices, which will be set to the identity matrix.
     */
    private static void setIdentity(float[]... matrices) {
        for (float[] m : matrices) {
            Matrix.setIdentityM(m, 0);
        }
    }
}
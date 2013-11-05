package com.jayway.oglhelloworld.gl;

import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_TRIANGLES;

/**
 * Object holding all necessary data to render it as a triangle mesh with DemoOpenGL.
 * <p/>
 * It also provides functionality modify the model matrix e.g. rotation.
 *
 * @author Andreas Nilsson
 */
public class GlObject {

    // Exposed directly for performance reasons
    public float[] modelMatrix = new float[16];

    public final String title;
    public final FloatBuffer vBuffer;
    public final int vDataStride;
    public final int vCount;

    public final int vPosOffset;
    public final int vPosDimension;

    public final int vUVOffset;
    public final int vUVDimension;

    public final int vNormaDimension;
    public final int vNormOffset;

    public final int glRenderMode;

    // Animation state
    private float mRotationY;
    private float mRotationX;

    /**
     * Creates a {@link GlObject} with {@link #glRenderMode} set
     * to {@link android.opengl.GLES20#GL_TRIANGLES}.
     *
     * @param title      The title.
     * @param vertexType The vertex type.
     * @param vertexData The vertex data.
     */
    public GlObject(String title, VertexType vertexType, float[] vertexData) {
        this(title, vertexType, vertexData, GL_TRIANGLES);
    }

    /**
     * @param glRenderMode The render mode.
     * @see GlObject#GlObject(String, VertexType, float[])
     */
    public GlObject(String title, VertexType vertexType, float[] vertexData, int glRenderMode) {
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

    // Setters

    /**
     * Sets the y rotation for this object.
     * <p/>
     * Notice: rotation will be clipped within [-90 90] degrees.
     *
     * @param angle The angle in degrees.
     */
    public void setRotationY(float angle) {
        // Constraint the angle
        mRotationY = Math.max(Math.min(angle, 90), -90);
        updateModelMatrix();
    }

    /**
     * Sets the x rotation for this object.
     * <p/>
     * Notice: rotation will be clipped within [-90 90] degrees.
     *
     * @param angle The angle in degrees.
     */
    public void setRotationX(float angle) {
        // Constraint the angle
        mRotationX = Math.max(Math.min(angle, 90), -90);
        updateModelMatrix();
    }

    // Getters

    public float getRotationY() {
        return mRotationY;
    }

    public float getRotationX() {
        return mRotationX;
    }

    /**
     * Applies transformations to the model matrix.
     */
    private void updateModelMatrix() {
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.rotateM(modelMatrix, 0, getRotationX(), 0, 1, 0);
        Matrix.rotateM(modelMatrix, 0, getRotationY(), 1, 0, 0);
    }

    public boolean hasTextureCoordinates() {
        return vUVDimension > 0;
    }

    public boolean hasNormals() {
        return vNormaDimension > 0;
    }

    /**
     * Called when the animation loop updates.
     *
     * @param dt The time since last update.
     */
    public void update(final float dt) {
        final float degreesPerSecond = 60;
        Matrix.rotateM(modelMatrix, 0, degreesPerSecond * dt, 1, 0, 0);
        Matrix.rotateM(modelMatrix, 0, degreesPerSecond * dt * 2f, 0, 1, 0);
        Matrix.rotateM(modelMatrix, 0, degreesPerSecond * dt * 2f, 0, 0, 1);
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
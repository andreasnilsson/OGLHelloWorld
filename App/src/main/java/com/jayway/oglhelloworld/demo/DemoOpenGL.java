package com.jayway.oglhelloworld.demo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.*;
import static android.opengl.GLES20.glVertexAttribPointer;

/**
 * Simple example of a rendering setup for GLES20.
 * I have tried to remove anything unnecessary to only show the core GLES calls.
 * <p/>
 * @author Andreas Nilsson
 */
public class DemoOpenGL {
    private static final int NO_OF_TRIANGLES = 1;
    private static final int NO_OF_ELEMENTS_PER_POSITION = 3;
    private static final int STRIDE_BETWEEN_VERTICES_IN_DATA = 3;
    private static final boolean DO_NORMALIZE_VALUES = false;

    int mShaderProgram = 0;
    final float[] mMatrix = new float[16];
    final FloatBuffer mTriangle = ByteBuffer
            .allocateDirect(3)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer();

    public void compileAndLinkShader() {
        // Loaded from resources or statically defined
        String vertexShaderSrc   = "...";
        String fragmentShaderSrc = "...";

        // Create shader shaderProgram
        mShaderProgram = glCreateProgram();

        // Compile vertex shader
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertexShaderSrc);
        glCompileShader(vertexShader);

        // Compile fragment shader
        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, fragmentShaderSrc);
        glCompileShader(fragmentShader);

        // Attach and link shaders
        glAttachShader(mShaderProgram, vertexShader);
        glAttachShader(mShaderProgram, fragmentShader);
        glLinkProgram(mShaderProgram);

        // Now we can use this shaderProgram before drawing
        glUseProgram(mShaderProgram);
    }

    void draw() {
        // Clear the frame with black color
        glClearColor(0, 0, 0, 1);
        glUseProgram(mShaderProgram);

        // Bind the uniform state
        int uniformHandle = glGetUniformLocation(mShaderProgram, "matrix");
        glUniformMatrix4fv(uniformHandle, 1, false, mMatrix, 0);

        // Bind the mesh data
        int attributeHandle = glGetAttribLocation(mShaderProgram, "position");
        glEnableVertexAttribArray(attributeHandle);
        glVertexAttribPointer(attributeHandle,
                NO_OF_ELEMENTS_PER_POSITION,
                GL_FLOAT,
                DO_NORMALIZE_VALUES,
                STRIDE_BETWEEN_VERTICES_IN_DATA,
                mTriangle);

        // Draw mTriangle
        glDrawArrays(GL_TRIANGLES, 0, NO_OF_TRIANGLES);
    }
}

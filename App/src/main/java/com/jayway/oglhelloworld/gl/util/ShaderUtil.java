package com.jayway.oglhelloworld.gl.util;

import android.content.Context;

import com.jayway.oglhelloworld.util.Log;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FALSE;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;

/**
 * Utility class for loading shader source as well as compiling and linking them.
 *
 * @author Andreas Nilsson
 */
public class ShaderUtil {
    private static final Log LOG = new Log(ShaderUtil.class);
    public static final int CREATE_SHADER_FAILED = 0;
    public static final int CREATE_PROGRAM_FAILED = 0;

    /**
     * Create and links an OpenGL shader program from the attached shader sources.
     *
     * @param vertexSource   The vertex shader source.
     * @param fragmentSource The fragment shader source.
     * @return The OpenGL handle for the shader program.
     */
    public static int createAndLinkShaderProgram(String vertexSource, String fragmentSource) {
        int program = CREATE_PROGRAM_FAILED;

        int vertexShader = createShader(GL_VERTEX_SHADER, vertexSource);
        int fragmentShader = createShader(GL_FRAGMENT_SHADER, fragmentSource);

        if (verifyShaderProgram(vertexShader, fragmentShader)) {
            program = glCreateProgram();
            if (program != CREATE_PROGRAM_FAILED) {
                glAttachShader(program, vertexShader);
                glAttachShader(program, fragmentShader);

                glLinkProgram(program);
                if (getLinkStatus(program) == GL_FALSE) {
                    LOG.e("Could not link program : " + program);
                    LOG.e(glGetProgramInfoLog(program));
                    program = CREATE_PROGRAM_FAILED;
                }
            }
        }

        return program;
    }

    /**
     * Loads a string from the raw resources with provided resource id.
     *
     * @param context The context.
     * @param resId   The raw string resource id.
     * @return The shader source string.
     */
    public static String loadShaderSourceFromRaw(Context context, int resId) {
        final InputStream is = context.getResources().openRawResource(resId);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String readLine;
        try {
            while ((readLine = br.readLine()) != null) {
                // Fix for enabling lazy style comments..
                final int i = readLine.indexOf("//");
                if (i != -1) {
                    sb.append(readLine.substring(0, i));
                } else {
                    sb.append(readLine);
                }
            }
        } catch (IOException e) {
            LOG.e("Failed loading shader with raw resId: " + resId, e);
        } finally {
            closeSilently(br);
        }

        return sb.toString();
    }

    /**
     * Compiles the shader of {@code shaderType} with the {@code source}.
     *
     * @param shaderType The shader type,
     *                   e.g. {@link android.opengl.GLES20#GL_VERTEX_SHADER} or
     *                   {@link android.opengl.GLES20#GL_FRAGMENT_SHADER}.
     * @param source     The shader source.
     * @return The shader handle or {@link #CREATE_SHADER_FAILED} if it failed to create the shader.
     */
    private static int createShader(int shaderType, String source) {
        int shader = glCreateShader(shaderType);
        if (shader != CREATE_SHADER_FAILED) {
            glShaderSource(shader, source);
            glCompileShader(shader);
            if (getCompileStatus(shader) == CREATE_SHADER_FAILED) {
                LOG.e(getShaderTypeName(shaderType) + "(" + shaderType + ") : ");
                LOG.e(glGetShaderInfoLog(shader));
                shader = CREATE_SHADER_FAILED;
            }
        }

        return shader;
    }

    /**
     * Returns the compilation status for shader of type {@code shaderType}.
     *
     * @param shaderProgram The shader program handle.
     * @return {@link android.opengl.GLES20#GL_TRUE} if compilation was successful otherwise
     * {@link android.opengl.GLES20#GL_FALSE}
     */
    protected static int getCompileStatus(int shaderProgram) {
        int[] compileStatus = new int[1];
        glGetShaderiv(shaderProgram, GL_COMPILE_STATUS, compileStatus, 0);

        if (compileStatus[0] == CREATE_SHADER_FAILED) {
            glDeleteShader(shaderProgram);
        }

        return compileStatus[0];
    }

    /**
     * Checks if the a shader has linked successfully.
     *
     * @param shaderProgram The shader shaderProgram handle.
     * @return {@link android.opengl.GLES20#GL_TRUE} if the linking was successful otherwise
     * {@link android.opengl.GLES20#GL_FALSE}
     */
    protected static int getLinkStatus(int shaderProgram) {
        int[] linkStatus = new int[1];
        glGetProgramiv(shaderProgram, GL_LINK_STATUS, linkStatus, 0);

        if (linkStatus[0] == GL_FALSE) {
            glDeleteProgram(shaderProgram);
        }

        return linkStatus[0];
    }

    /**
     * Checks if the shader handle is a valid id.
     *
     * @param shaderProgram The shader program handle.
     * @return {@code true} if <b>ALL</b> provided shaders are ok, otherwise {@code false}.
     */
    protected static boolean verifyShaderProgram(int... shaderProgram) {
        for (int s : shaderProgram) {
            if (s == CREATE_SHADER_FAILED) return false;
        }
        return true;
    }

    /**
     * Convenience function to return the shader type as a readable string.
     *
     * @param shaderType The type of shader. E.g. {@link android.opengl.GLES20#GL_VERTEX_SHADER}
     *                   or {@link android.opengl.GLES20#GL_FRAGMENT_SHADER}
     * @return The name of the shader type.
     */
    protected static String getShaderTypeName(int shaderType) {
        switch (shaderType) {
            case GL_FRAGMENT_SHADER:
                return "Fragment Shader";
            case GL_VERTEX_SHADER:
                return "Vertex Shader";
        }
        return "Unknown Shader";
    }

    /**
     * Closes a closeable silently and ignores any exception that might be raised.
     *
     * @param closeable The closeable.
     */
    private static void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) {
            }
        }
    }
}

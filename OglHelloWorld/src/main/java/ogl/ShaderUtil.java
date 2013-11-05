package ogl;

import android.util.Log;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_TRUE;
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

public class ShaderUtil {
	private static final String TAG = "ShaderUtil";

	private final static String mVertexShader =
              "uniform mat4 uMVPMatrix;\n"
			+ "attribute vec4 aPosition;\n"
            + "attribute vec2 aTextureCoord;\n"
			+ "varying vec2 vTextureCoord;\n"
            + "void main() {\n"
			+ "  gl_Position = aPosition * uMVPMatrix;\n"
			+ "  vTextureCoord = aTextureCoord;\n"
            + "}\n";

	private final static String mFragmentShader =
              "precision mediump float;\n"
			+ "varying vec2 vTextureCoord;\n"
            + "uniform sampler2D uTexture0;\n"
			+ "void main() {\n"
			+ "  gl_FragColor = texture2D(sTexture, vTextureCoord);\n"
            + "}\n";


    private static final int SHADER_COMPILED_WITH_ERROR = 0;
    private static final int PROGRAM_COMPILED_WITH_ERROR = 0;

	public static final int createDefaultShader() {
		return createProgram(mVertexShader, mFragmentShader);
	}

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
//			GLESUtil.checkGlError("glAttachShader");
			glAttachShader(program, pixelShader);
//			GLESUtil.checkGlError("glAttachShader");
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
				Log.e(TAG, getShaderType(shaderType) + " compile failed: " + shaderType + ":");
				Log.e(TAG, glGetShaderInfoLog(shader));
				glDeleteShader(shader);
				shader = SHADER_COMPILED_WITH_ERROR;
			}
		}
		return shader;
	}

    /**
     * Returns a human readable string of which type of shader is defined.
     * @param shaderType
     * @return
     */
    private static String getShaderType(int shaderType) {
        switch (shaderType){
            case GL_FRAGMENT_SHADER:
                return "Fragment Shader";
            case GL_VERTEX_SHADER:
                return "Vertex Shader";
        }
        return "Shader type not recognized";
    }
}

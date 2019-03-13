package com.gulei.gldemo.util;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by gl152 on 2019/3/13.
 */

public class SharderUtils {

    public static void checkGLError(String op) {
        Log.e("gl", "checkGLError: " + op);
    }

    /**
     * @param shaderType 着色器类型如：GLES20.GL_VERTEX_SHADER
     * @param source     着色器的代码：字符串或者assest文件中读取着色器的sh文件转成字符串
     * @return 返回着色器句柄
     */
    public static int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            //检查是否创建成功
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                checkGLError("Could not compile shader:" + shaderType);
                checkGLError("GLES20 Error:" + GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    public static int loadShader(Resources res, int shaderType, String resName) {
        return loadShader(shaderType, loadFromAssetsFile(resName, res));
    }

    public static int createProgram(String vertexShader, String fragmentShader) {
        int vertex = loadShader(GLES20.GL_VERTEX_SHADER, vertexShader);
        if (vertex == 0) return 0;
        int fragment = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);
        if (fragment == 0) return 0;
        int program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, vertex);
            checkGLError("Attach Vertex Shader");
            GLES20.glAttachShader(program, fragment);
            checkGLError("Attach Fragment Shader");
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                checkGLError("Could not link program:" + GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }

    public static int createProgram(Resources res, String vertexRes, String fragmentRes) {
        return createProgram(loadFromAssetsFile(vertexRes, res), loadFromAssetsFile(fragmentRes, res));
    }

    public static String loadFromAssetsFile(String fname, Resources res) {
        StringBuilder result = new StringBuilder();
        InputStream is = null;
        try {
            is = res.getAssets().open(fname);
            int ch = 0;
            byte[] buffer = new byte[1024];
            while ((ch = is.read(buffer)) != -1) {
                result.append(new String(buffer, 0, ch));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result.toString().replaceAll("\\r\\n", "\n");
    }
}

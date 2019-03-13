package com.gulei.gldemo;

import android.content.Context;
import android.opengl.GLES20;

import com.gulei.gldemo.util.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by gl152 on 2019/1/17.
 */

public class Square {
    private static final String TAG = "Square";

    int mProgram;
    private static float[] squareVertex = {
            -0.5f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            0.5f, 0.5f, 0.0f
    };

    private static float[] color = {
            1.0f, 1.0f, 1.0f, 1.0f
    };
    private FloatBuffer vertexBuffer;
    private FloatBuffer fragmentColorBuffer;
    private int vPositionHandle;
    private int vColorHandle;
    private int vMvpMatrixHandle;

    public Square(Context context) {

        ByteBuffer vb = ByteBuffer.allocateDirect(squareVertex.length * 4);
        vb.order(ByteOrder.nativeOrder());
        vertexBuffer = vb.asFloatBuffer();
        vertexBuffer.put(squareVertex);
        vertexBuffer.position(0);

        ByteBuffer fcb = ByteBuffer.allocateDirect(color.length * 4);
        fcb.order(ByteOrder.nativeOrder());
        fragmentColorBuffer = fcb.asFloatBuffer();
        fragmentColorBuffer.put(color);
        fragmentColorBuffer.position(0);

        int vertexSharder = loadSharder(GLES20.GL_VERTEX_SHADER, Utils.getFromAssets(context, "squareVertexSharder.glsl"));
        int fragmentSharder = loadSharder(GLES20.GL_FRAGMENT_SHADER, Utils.getFromAssets(context, "squareFragmentSharder.glsl"));
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexSharder);
        GLES20.glAttachShader(mProgram, fragmentSharder);
        GLES20.glLinkProgram(mProgram);
    }

    private int loadSharder(int type, String sharderString) {
        int sharder = GLES20.glCreateShader(type);
        GLES20.glShaderSource(sharder, sharderString);
        GLES20.glCompileShader(sharder);
        return sharder;
    }


    public void draw(float[] mvpMatrix) {
        GLES20.glUseProgram(mProgram);
        vPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(vPositionHandle);
        GLES20.glVertexAttribPointer(vPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);

        vColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
//        GLES20.glEnableVertexAttribArray(vColorHandle);
        GLES20.glUniform4fv(vColorHandle, 1, fragmentColorBuffer);

        vMvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(vMvpMatrixHandle, 1, false, mvpMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, squareVertex.length / 3);
        GLES20.glDisableVertexAttribArray(vPositionHandle);
    }

}

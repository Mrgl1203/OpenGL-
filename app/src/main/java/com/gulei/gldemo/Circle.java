package com.gulei.gldemo;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gl152 on 2019/2/26.
 */

public class Circle {
    float[] color = {1.0f, 1.0f, 1.0f, 1.0f};
    float radius = 0.5f;
    float[] circleVertex;
    FloatBuffer vertexBuffer;
    FloatBuffer fragmentColorBuffer;

    int mProgram;
    int vPositionHandle;
    int vColorHandle;
    int vMvpMatrix;

    public Circle(Context context) {
        List<Float> data = new ArrayList<>();
        data.add(0.0f);
        data.add(0.0f);
        data.add(0.0f);
        float degree = 360f / 36;
        for (float i = 0; i <= 360f; i += degree) {
            data.add((float) (radius * Math.sin(i * Math.PI / 180f)));
            data.add((float) (radius * Math.cos(i * Math.PI / 180f)));
            data.add(0.0f);
        }
        circleVertex = new float[data.size()];
        for (int i = 0; i < circleVertex.length; i++) {
            circleVertex[i] = data.get(i);
        }

        ByteBuffer vb = ByteBuffer.allocateDirect(circleVertex.length * 4);
        vb.order(ByteOrder.nativeOrder());
        vertexBuffer = vb.asFloatBuffer();
        vertexBuffer.put(circleVertex);
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

    private int loadSharder(int type, String sharderCode) {
        int sharder = GLES20.glCreateShader(type);
        GLES20.glShaderSource(sharder, sharderCode);
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

        vMvpMatrix = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(vMvpMatrix, 1, false, mvpMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, circleVertex.length / 3);
        GLES20.glDisableVertexAttribArray(vPositionHandle);
    }


}

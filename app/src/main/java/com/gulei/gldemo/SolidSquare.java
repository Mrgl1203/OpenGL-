package com.gulei.gldemo;

import android.content.Context;
import android.opengl.GLES20;

import com.gulei.gldemo.util.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by gl152 on 2019/2/26.
 * https://blog.csdn.net/junzia/article/details/52820177
 */

public class SolidSquare {
    private int mProgram;
    final int COORDS_PER_VERTEX = 3;
    final float cubePositions[] = {
            -1.0f, 1.0f, 1.0f,    //正面左上0
            -1.0f, -1.0f, 1.0f,   //正面左下1
            1.0f, -1.0f, 1.0f,    //正面右下2
            1.0f, 1.0f, 1.0f,     //正面右上3
            -1.0f, 1.0f, -1.0f,    //反面左上4
            -1.0f, -1.0f, -1.0f,   //反面左下5
            1.0f, -1.0f, -1.0f,    //反面右下6
            1.0f, 1.0f, -1.0f,     //反面右上7
    };

    //    final short index[] = {
//            6, 7, 4, 6, 4, 5,    //后面
//            6, 3, 7, 6, 2, 3,    //右面
//            6, 5, 1, 6, 1, 2,    //下面
//            0, 3, 2, 0, 2, 1,    //正面
//            0, 1, 5, 0, 5, 4,    //左面
//            0, 7, 3, 0, 4, 7,    //上面
//    };
    final short index[] = {
            0, 3, 2, 0, 2, 1,    //正面
            0, 1, 5, 0, 5, 4,    //左面
            0, 7, 3, 0, 4, 7,    //上面
            6, 7, 4, 6, 4, 5,    //后面
            6, 3, 7, 6, 2, 3,    //右面
            6, 5, 1, 6, 1, 2     //下面
    };


    float color[] = {
            0f, 1f, 0f, 1f,
            0f, 1f, 0f, 1f,
            0f, 1f, 0f, 1f,
            0f, 1f, 0f, 1f,
            1f, 0f, 0f, 1f,
            1f, 0f, 0f, 1f,
            1f, 0f, 0f, 1f,
            1f, 0f, 0f, 1f,
    };

    private int mPositionHandle;
    private int mColorHandle;
    private int mMatrixHandler;

    //顶点个数
    private final int vertexCount = cubePositions.length / COORDS_PER_VERTEX;

    //顶点之间的偏移量
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 每个顶点四个字节

    private FloatBuffer vertexBuffer, colorBuffer;
    private ShortBuffer indexBuffer;

    public SolidSquare(Context context) {
        ByteBuffer bb = ByteBuffer.allocateDirect(
                cubePositions.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(cubePositions);
        vertexBuffer.position(0);

        ByteBuffer dd = ByteBuffer.allocateDirect(color.length * 4);
        dd.order(ByteOrder.nativeOrder());
        colorBuffer = dd.asFloatBuffer();
        colorBuffer.put(color);
        colorBuffer.position(0);

        ByteBuffer cc = ByteBuffer.allocateDirect(index.length * 2);
        cc.order(ByteOrder.nativeOrder());
        indexBuffer = cc.asShortBuffer();
        indexBuffer.put(index);
        indexBuffer.position(0);

        int vertexSharder = loadSharder(GLES20.GL_VERTEX_SHADER, Utils.getFromAssets(context, "SolidVertexSharder.glsl"));
        int fragmentSharder = loadSharder(GLES20.GL_FRAGMENT_SHADER, Utils.getFromAssets(context, "SolidFragmentSharder.glsl"));
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexSharder);
        GLES20.glAttachShader(mProgram, fragmentSharder);
        GLES20.glLinkProgram(mProgram);
        //开启深度测试
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    }

    private int loadSharder(int type, String sharderCode) {
        int sharder = GLES20.glCreateShader(type);
        GLES20.glShaderSource(sharder, sharderCode);
        GLES20.glCompileShader(sharder);
        return sharder;
    }

    public void draw(float[] mvpMatrix) {

        GLES20.glUseProgram(mProgram);

        mMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        GLES20.glUniformMatrix4fv(mMatrixHandler, 1, false, mvpMatrix, 0);

        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

        mColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        GLES20.glEnableVertexAttribArray(mColorHandle);
        GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false, 0, colorBuffer);

//        // 获取片段着色器的颜色的句柄
//        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
//        // 设置绘制三角形的颜色
//        GLES20.glUniform4fv(mColorHandle, 1, colorBuffer);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, index.length, GLES20.GL_UNSIGNED_SHORT, indexBuffer);
        GLES20.glDisableVertexAttribArray(mPositionHandle);

    }
}

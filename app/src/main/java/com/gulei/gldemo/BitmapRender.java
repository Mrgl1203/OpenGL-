package com.gulei.gldemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.view.View;

import com.gulei.gldemo.util.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by gl152 on 2019/2/27.
 */

public class BitmapRender extends Shape {

    Bitmap mBitmap;
    float[] mProjectMatrix = new float[16];
    private float[] mViewMatrix = new float[16];  //[4*4]的相机视图变换矩阵
    private float[] mMVPMatrix = new float[16];//用于存储变换矩阵结果的总变换矩阵[4*4]

    int mProgram;
    int textureId;
    int mTexture;
    int mPositionHandle;
    int mMatrixHandle;
    int mCoordinateHandle;
    FloatBuffer vertexBuffer;
    FloatBuffer coordinateBuffer;

    //二维顶点坐标
    private float[] sPos = {
            -1.0f, 1.0f,//左上角
            -1.0f, -1.0f,//左下角
            1.0f, 1.0f,//右上角
            1.0f, -1.0f//右上角
    };

    //二维文理坐标，根据安卓坐标系180度翻转，要对应顶点坐标位置，否则纹理错位
    private float[] sCoordinate = {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f
    };

    public BitmapRender(View mView) {
        super(mView);
        mBitmap = BitmapFactory.decodeResource(mView.getContext().getResources(), R.mipmap.fengj);
        ByteBuffer vb = ByteBuffer.allocateDirect(sPos.length * 4);
        vertexBuffer = vb.order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(sPos);
        vertexBuffer.position(0);

        ByteBuffer cb = ByteBuffer.allocateDirect(sCoordinate.length * 4);
        coordinateBuffer = cb.order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(sCoordinate);
        coordinateBuffer.position(0);


    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        int vertexSharder = loadSharder(GLES20.GL_VERTEX_SHADER, Utils.getFromAssets(mView.getContext(), "BitmapVertex.glsl"));
        int fragmentSharder = loadSharder(GLES20.GL_FRAGMENT_SHADER, Utils.getFromAssets(mView.getContext(), "BitmapFragment.glsl"));
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexSharder);
        GLES20.glAttachShader(mProgram, fragmentSharder);
        GLES20.glLinkProgram(mProgram);

        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mMatrixHandle = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        mCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "vCoordinate");
        mTexture = GLES20.glGetUniformLocation(mProgram, "vTexture");
    }


    private int loadSharder(int type, String sharderCode) {
        int sharder = GLES20.glCreateShader(type);
        GLES20.glShaderSource(sharder, sharderCode);
        GLES20.glCompileShader(sharder);
        return sharder;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        int w = mBitmap.getWidth();
        int h = mBitmap.getHeight();
        float sWH = w / (float) h;
        float sWidthHeight = width / (float) height;
        if (width > height) {
            if (sWH > sWidthHeight) {
                Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight * sWH, sWidthHeight * sWH, -1, 1, 3, 7);
            } else {
                Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight / sWH, sWidthHeight / sWH, -1, 1, 3, 7);
            }
        } else {
            if (sWH > sWidthHeight) {
                Matrix.orthoM(mProjectMatrix, 0, -1, 1, -1 / sWidthHeight * sWH, 1 / sWidthHeight * sWH, 3, 7);
            } else {
                Matrix.orthoM(mProjectMatrix, 0, -1, 1, -sWH / sWidthHeight, sWH / sWidthHeight, 3, 7);
            }
        }
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUseProgram(mProgram);

        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glEnableVertexAttribArray(mCoordinateHandle);
        GLES20.glUniform1i(mTexture, 0);
        textureId = createTexture();
        GLES20.glVertexAttribPointer(mPositionHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, vertexBuffer);
        GLES20.glVertexAttribPointer(mCoordinateHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, coordinateBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }

    private int createTexture() {
        int[] texture = new int[1];
        if (mBitmap != null && !mBitmap.isRecycled()) {
            //生成纹理
            GLES20.glGenTextures(1, texture, 0);
            //生成纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
            return texture[0];
        }
        return 0;
    }

}

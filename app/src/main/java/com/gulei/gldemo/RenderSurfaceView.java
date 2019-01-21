package com.gulei.gldemo;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by gl152 on 2019/1/17.
 */

public class RenderSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {
    Triangle mTriangle;
    float[] mProjectionMatrix = new float[16]; //[4*4]的透视投影变换矩阵
    private float[] mViewMatrix = new float[16];  //[4*4]的相机视图变换矩阵
    private float[] mMVPMatrix = new float[16];//用于存储变换矩阵结果的总变换矩阵[4*4]

    public RenderSurfaceView(Context context) {
        this(context, null);
    }

    public RenderSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //选择OpenGL ES 2.0 context
        setEGLContextClientVersion(2);
        setRenderer(this);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //清除颜色缓冲区时指定RGBA值（也就是所有的颜色都会被替换成指定的RGBA值）。每个值的取值范围都是0.0~1.0，超出范围的将被截断。
        GLES20.glClearColor(0f, 0f, 0f, 1f);
        mTriangle = new Triangle();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //渲染之后的图形绘制在窗体的哪个部位，openGL的坐标系为正常的数学坐标
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;
        // 这个投影矩阵被应用于对象坐标在onDrawFrame（）方法中
        //透视投影：随观察点的距离变化而变化，观察点越远，视图越小，反之越大，我们可以通过如下方法来设置透视投影：
        //ratio为宽高比来矫正在安卓坐标系上长方形坐标系带来的扭曲（openGL为正方形）
        /**
         * Matrix.frustumM (float[] m, //接收透视投影的变换矩阵
         * int mOffset, //变换矩阵的起始位置（偏移量）
         * float left, //相对观察点近面的左边距
         * float right, //相对观察点近面的右边距
         * float bottom, //相对观察点近面的下边距
         * float top, //相对观察点近面的上边距
         * float near, //相对观察点近面距离
         * float far) //相对观察点远面距离
         */
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // 重绘背景色
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        //定义一个相机视图
        /**
         * Matrix.setLookAtM (float[] rm, //接收相机变换矩阵
         * int rmOffset, //变换矩阵的起始位置（偏移量）
         * float eyeX,float eyeY,float eyeZ, //相机位置
         * float centerX,float centerY,float centerZ, //观察点位置
         * float upX,float upY,float upZ) //up向量在xyz上的分量
         */
        Matrix.setLookAtM(mViewMatrix,0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);


        /**
         * Matrix.multiplyMM (float[] result, //接收相乘结果
         * int resultOffset, //接收矩阵的起始位置（偏移量）
         * float[] lhs, //左矩阵
         * int lhsOffset, //左矩阵的起始位置（偏移量）
         * float[] rhs, //右矩阵
         * int rhsOffset) //右矩阵的起始位置（偏移量）
         */
        //将相机视图和投影设置的数据相乘，便得到一个转换矩阵，然后我们再讲此矩阵传给顶点着色器
        Matrix.multiplyMM(mMVPMatrix,0,mProjectionMatrix,0,mViewMatrix,0);

        mTriangle.draw(mMVPMatrix);

    }
}

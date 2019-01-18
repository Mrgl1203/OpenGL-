package com.gulei.gldemo;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by gl152 on 2019/1/17.
 */

public class RenderSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {
    Triangle mTriangle;

    public RenderSurfaceView(Context context) {
        this(context, null);
    }

    public RenderSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

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
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // 重绘背景色
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        mTriangle.draw();
    }
}

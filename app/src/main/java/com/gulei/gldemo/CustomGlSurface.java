package com.gulei.gldemo;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by gl152 on 2019/2/26.
 */

public class CustomGlSurface extends GLSurfaceView{
    public CustomGlSurface(Context context) {
        this(context,null);
    }

    public CustomGlSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
        setRenderer(new BitmapRender(this));
        // 设置渲染模式为连续模式(会以60fps的速度刷新)  默认为RENDERMODE_CONTINUOUSLY
        //RENDERMODE_WHEN_DIRTY时，仅在创建曲面时或在调用requestRender时才渲染渲染器
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        requestRender();
    }
}

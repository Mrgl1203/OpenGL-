package com.gulei.gldemo.image;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.view.View;

import com.gulei.gldemo.image.filter.AFilter;
import com.gulei.gldemo.image.filter.ColorFilter;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by gl152 on 2019/3/13.
 */
//代理模式
public class SGLRender implements GLSurfaceView.Renderer {
    private AFilter mFilter;
    private Bitmap bitmap;
    private int width, height;
    private boolean refreshFlag = false;
    private EGLConfig config;

    public SGLRender(View mView, AFilter mFilter) {
        if (mFilter == null) {
            this.mFilter = new ColorFilter(mView.getContext(), ColorFilter.Filter.NONE);
        } else {
            this.mFilter = mFilter;
        }
    }

    public void setFilter(AFilter filter) {
        refreshFlag = true;
        mFilter = filter;
        setBitmap(bitmap);
    }

    public void setBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            this.bitmap = bitmap;
            mFilter.setBitmap(bitmap);
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        this.config = config;
        mFilter.onSurfaceCreated(gl, config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width = width;
        this.height = height;
        mFilter.onSurfaceChanged(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (refreshFlag&&width!=0&&height!=0) {
            mFilter.onSurfaceCreated(gl, config);
            mFilter.onSurfaceChanged(gl, width, height);
            refreshFlag = false;
        }
        mFilter.onDrawFrame(gl);
    }

    public void refresh(){
        refreshFlag=true;
    }

    public AFilter getFilter(){
        return mFilter;
    }
}

package com.gulei.gldemo.image;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.gulei.gldemo.R;
import com.gulei.gldemo.image.filter.AFilter;
import com.gulei.gldemo.image.filter.ColorFilter;

/**
 * Created by gl152 on 2019/3/13.
 */

public class SGLView extends GLSurfaceView {
    private SGLRender render;

    public SGLView(Context context) {
        this(context, null);
    }

    public SGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
        render = new SGLRender(this, new ColorFilter(getContext(), ColorFilter.Filter.NONE));
        setRenderer(render);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        render.setBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.fengj));
        requestRender();
    }

    public SGLRender getRender() {
        return render;
    }

    public void setFilter(AFilter filter) {
        render.setFilter(filter);
        requestRender();
    }
}

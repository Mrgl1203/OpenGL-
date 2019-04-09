package com.gulei.gldemo.camera;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLSurfaceView;

import com.gulei.gldemo.filter.AFilter;
import com.gulei.gldemo.filter.GroupFilter;
import com.gulei.gldemo.filter.TextureFilter;
import com.gulei.gldemo.util.MatrixUtils;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by gl152 on 2019/4/9.
 */

public class TextureController implements GLSurfaceView.Renderer {
    private Object surface;     //外部传递进来的surface，surfaceTexture或者surfaceHolder

    private GLView mGlView;
    private Context mContext;

    private Renderer mRenderer;                                 //用户附加的Renderer或用来监听Renderer
    private TextureFilter mEffectFilter;                        //特效处理的Filter
    private GroupFilter mGroupFilter;                           //中间特效
    private AFilter mShowFilter;                                //用来渲染输出的Filter
    private Point mDataSize;                                    //数据的大小
    private Point mWindowSize;                                  //输出视图的大小
    private AtomicBoolean isParamSet=new AtomicBoolean(false);
    private float[] SM=new float[16];                           //用于绘制到屏幕上的变换矩阵
    private int mShowType= MatrixUtils.TYPE_CENTERCROP;          //输出到屏幕上的方式
    private int mDirectionFlag=-1;                               //AiyaFilter方向flag

    private float[] callbackOM=new float[16];                   //用于绘制回调缩放的矩阵


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {

    }

    /**
     * 自定义GLSurfaceView，暴露出onAttachedToWindow
     * 方法及onDetachedFromWindow方法，取消holder的默认监听
     * onAttachedToWindow及onDetachedFromWindow必须保证view
     * 存在Parent
     */
    private class GLView extends GLSurfaceView {

        public GLView(Context context) {
            super(context);
            init();
        }

        private void init() {
            //这句话是必要的，避免GlSurfaceView自带的Surface影响渲染
            getHolder().addCallback(null);
            //指定外部传入的surface为渲染的window surface
            setEGLWindowSurfaceFactory(new EGLWindowSurfaceFactory() {
                @Override
                public EGLSurface createWindowSurface(EGL10 egl, EGLDisplay display, EGLConfig config, Object nativeWindow) {
                    //这里传入的surface为外部传入可以为surface，surfaceTexture，surfaceHolder
                    return egl.eglCreateWindowSurface(display, config, surface, null);
                }

                @Override
                public void destroySurface(EGL10 egl, EGLDisplay display, EGLSurface surface) {
                    egl.eglDestroySurface(display, surface);
                }
            });
            setEGLContextClientVersion(2);
            setRenderer(TextureController.this);
            setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
            ////设置暂停的时候是否保持EglContext
            setPreserveEGLContextOnPause(true);
        }

        public void attachedToWindow() {
            //附加到Window上时被调用，外部不可调用
            super.onAttachedToWindow();
        }

        public void detachedFromWindow() {
            //从Window上被移除时调用，外部不可调用
            super.onDetachedFromWindow();
        }

        public void clear() {
//            try {
//                finalize();
//            } catch (Throwable throwable) {
//                throwable.printStackTrace();
//            }
        }
    }
}

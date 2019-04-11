package com.gulei.gldemo.camera;

import android.content.Context;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gulei.gldemo.filter.AFilter;
import com.gulei.gldemo.filter.GroupFilter;
import com.gulei.gldemo.filter.NoFilter;
import com.gulei.gldemo.filter.TextureFilter;
import com.gulei.gldemo.util.EasyGlUtils;
import com.gulei.gldemo.util.MatrixUtils;

import java.nio.ByteBuffer;
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
    private AtomicBoolean isParamSet = new AtomicBoolean(false);
    private float[] SM = new float[16];                           //用于绘制到屏幕上的变换矩阵
    private int mShowType = MatrixUtils.TYPE_CENTERCROP;          //输出到屏幕上的方式
    private int mDirectionFlag = -1;                               //AiyaFilter方向flag

    private float[] callbackOM = new float[16];                   //用于绘制回调缩放的矩阵

    //创建离屏buffer，用于最后导出数据
    private int[] mExportFrame = new int[1];
    private int[] mExportTexture = new int[1];

    private boolean isRecord = false;                             //录像flag
    private boolean isShoot = false;                              //一次拍摄flag
    private ByteBuffer[] outPutBuffer = new ByteBuffer[3];      //用于存储回调数据的buffer
    private FrameCallback mFrameCallback;                       //回调
    private int frameCallbackWidth, frameCallbackHeight;        //回调数据的宽高
    private int indexOutput = 0;                                  //回调数据使用的buffer索引

    public TextureController(Context context) {
        this.mContext = context;
        init();
    }

    public void surfaceCreated(Object nativeWindow) {
        this.surface = nativeWindow;
        mGlView.surfaceCreated(null);
    }

    public void surfaceChanged(int width, int height) {
        this.mWindowSize.x = width;
        this.mWindowSize.y = height;
        mGlView.surfaceChanged(null, 0, width, height);
    }

    public void surfaceDestoryed() {
        mGlView.surfaceDestroyed(null);
    }

    public Object getOutput() {
        return surface;
    }


    private void init() {
        //创建自定义的GLSurfaceView
        mGlView = new GLView(mContext);

        //GlSurfaceView必须添加到布局中，避免GLView的attachToWindow和detachFromWindow崩溃
        ViewGroup v = new ViewGroup(mContext) {
            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {

            }
        };
        v.addView(mGlView);
        v.setVisibility(View.GONE);

        mEffectFilter = new TextureFilter(mContext.getResources());
        mShowFilter = new NoFilter(mContext.getResources());
        mGroupFilter = new GroupFilter(mContext.getResources());

        //设置默认的DateSize，DataSize由AiyaProvider根据数据源的图像宽高进行设置
        mDataSize = new Point(720, 1280);

        mWindowSize = new Point(720, 1208);
    }

    //在Surface创建前，应该被调用
    public void setDataSize(int width, int height) {
        mDataSize.x = width;
        mDataSize.y = height;
    }


    public SurfaceTexture getTexture() {
        return mEffectFilter.getTexture();
    }

    public void setImageDirection(int flag) {
        this.mDirectionFlag = flag;
    }

    public void setRenderer(Renderer renderer) {
        mRenderer = renderer;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mEffectFilter.create();
        mGroupFilter.create();
        mShowFilter.create();
        if (!isParamSet.get()) {//在多线程中保证原子性，确保只调用一次
            if (mRenderer != null) {
                mRenderer.onSurfaceCreated(gl, config);
            }
            sdkParamSet();//设置状态为true
        }
        calculateCallbackOM();
        mEffectFilter.setFlag(mDirectionFlag);

        //重置纹理
        deleteFrameBuffer();
        GLES20.glGenFramebuffers(1, mExportFrame, 0);
        EasyGlUtils.genTexturesWithParameter(1, mExportTexture, 0, GLES20.GL_RGBA, mDataSize.x,
                mDataSize.y);
    }

    private void deleteFrameBuffer() {
        GLES20.glDeleteFramebuffers(1, mExportFrame, 0);
        GLES20.glDeleteTextures(1, mExportTexture, 0);
    }

    private void calculateCallbackOM() {
        if (frameCallbackHeight > 0 && frameCallbackWidth > 0 && mDataSize.x > 0 && mDataSize.y > 0) {
            //计算输出的变换矩阵
            MatrixUtils.getMatrix(callbackOM, MatrixUtils.TYPE_CENTERCROP, mDataSize.x, mDataSize.y,
                    frameCallbackWidth,
                    frameCallbackHeight);
            MatrixUtils.flip(callbackOM, false, true);
        }
    }

    private void sdkParamSet() {
        if (!isParamSet.get() && mDataSize.x > 0 && mDataSize.y > 0) {
            isParamSet.set(true);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        MatrixUtils.getMatrix(SM, mShowType,
                mDataSize.x, mDataSize.y, width, height);
        mShowFilter.setSize(width, height);
        mShowFilter.setMatrix(SM);
        mGroupFilter.setSize(mDataSize.x, mDataSize.y);
        mEffectFilter.setSize(mDataSize.x, mDataSize.y);
        mShowFilter.setSize(mDataSize.x, mDataSize.y);
        if (mRenderer != null) {
            mRenderer.onSurfaceChanged(gl, width, height);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (isParamSet.get()) {
            //获取离屏渲染的纹理数据传递给GroupFilter，继续在其基础上添加纹理
            mEffectFilter.draw();
            mGroupFilter.setTextureId(mEffectFilter.getOutputTexture());
            mGroupFilter.draw();

            //显示传入的texture上，一般是显示在屏幕上
            GLES20.glViewport(0, 0, mWindowSize.x, mWindowSize.y);
            mShowFilter.setMatrix(SM);
            //绑定最终的组合纹理结果交给单一渲染器直接渲染
            mShowFilter.setTextureId(mGroupFilter.getOutputTexture());
            mShowFilter.draw();
            if (mRenderer != null) {
                mRenderer.onDrawFrame(gl);
            }
            callbackIfNeeded();
        }
    }

    /**
     * 增加滤镜
     *
     * @param filter 滤镜
     */
    public void addFilter(AFilter filter) {
        mGroupFilter.addFilter(filter);
    }

    /**
     * 设置输入图像与输出视图大小不同时，图像的展示方式
     *
     * @param type 展示方式，可选项为：
     *             {@link MatrixUtils#TYPE_CENTERCROP}、{@link MatrixUtils#TYPE_CENTERINSIDE}、
     *             {@link MatrixUtils#TYPE_FITEND}、{@link MatrixUtils#TYPE_FITSTART}、
     *             {@link MatrixUtils#TYPE_FITXY}，与{@link ImageView.ScaleType}对应
     */
    public void setShowType(int type) {
        this.mShowType = type;
        if (mWindowSize.x > 0 && mWindowSize.y > 0) {
            MatrixUtils.getMatrix(SM, mShowType,
                    mDataSize.x, mDataSize.y, mWindowSize.x, mWindowSize.y);
            mShowFilter.setMatrix(SM);
            mShowFilter.setSize(mWindowSize.x, mWindowSize.y);
        }
    }

    public void onPause() {
        mGlView.onPause();
    }

    public void onResume() {
        mGlView.onResume();
    }

    public void startRecord() {
        isRecord = true;
    }

    public void stopRecord() {
        isRecord = false;
    }

    public void takePhoto() {
        isShoot = true;
    }

    public void setFrameCallback(int width, int height, FrameCallback frameCallback) {
        this.frameCallbackWidth = width;
        this.frameCallbackHeight = height;
        if (frameCallbackWidth > 0 && frameCallbackHeight > 0) {
            if (outPutBuffer != null) {
                outPutBuffer = new ByteBuffer[3];
            }
            calculateCallbackOM();
            this.mFrameCallback = frameCallback;
        } else {
            this.mFrameCallback = null;
        }
    }

    //录制或拍照时需要回调，则缩放图片到指定大小，读取数据并回调
    private void callbackIfNeeded() {
        if (mFrameCallback != null && (isRecord || isShoot)) {
            indexOutput = indexOutput++ >= 2 ? 0 : indexOutput;
            if (outPutBuffer[indexOutput] == null) {
                outPutBuffer[indexOutput] = ByteBuffer.allocate(frameCallbackWidth *
                        frameCallbackHeight * 4);
            }
            GLES20.glViewport(0, 0, frameCallbackWidth, frameCallbackHeight);
            EasyGlUtils.bindFrameTexture(mExportFrame[0], mExportTexture[0]);
            mShowFilter.setMatrix(callbackOM);
            mShowFilter.draw();
            frameCallback();
            isShoot = false;
            EasyGlUtils.unBindFrameBuffer();
            mShowFilter.setMatrix(SM);
        }
    }

    //读取数据并回调
    private void frameCallback() {
        //将结果读取到buffer中
        GLES20.glReadPixels(0, 0, frameCallbackWidth, frameCallbackHeight,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, outPutBuffer[indexOutput]);
        mFrameCallback.onFrame(outPutBuffer[indexOutput].array(), 0);
    }

    public void destroy() {
        if (mRenderer != null) {
            mRenderer.onDestroy();
        }
        mGlView.surfaceDestroyed(null);
        mGlView.detachedFromWindow();
        mGlView.clear();
    }

    public void requestRender() {
        mGlView.requestRender();
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

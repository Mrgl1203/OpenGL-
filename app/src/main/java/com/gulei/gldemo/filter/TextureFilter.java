package com.gulei.gldemo.filter;

import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;

import com.gulei.gldemo.util.EasyGlUtils;

import java.nio.ByteBuffer;

/**
 * Created by gl152 on 2019/4/9.
 */

public class TextureFilter extends AFilter {
    private CameraFilter mFilter;
    private int width = 0;
    private int height = 0;

    private int[] fFrame = new int[1];
    private int[] fTexture = new int[1];
    private int[] mCameraTexture = new int[1];

    private SurfaceTexture mSurfaceTexture;
    private float[] mCoordOM = new float[16];

    private ByteBuffer tBuffer;

    public TextureFilter(Resources mRes) {
        super(mRes);
        mFilter = new CameraFilter(mRes);//构造函数内部执行了initBuffer，初始化缓冲区
    }

    public void setCoordMatrix(float[] matrix) {
        mFilter.setCoordMatrix(matrix);
    }

    public SurfaceTexture getTexture() {
        return mSurfaceTexture;
    }


    @Override
    public void setFlag(int flag) {
        mFilter.setFlag(flag);
    }

    @Override
    protected void initBuffer() {
        //不执行父类 AFilter的方法，防止CameraFilter的initBuffer受影响
    }

    @Override
    public void setMatrix(float[] matrix) {
        mFilter.setMatrix(matrix);
    }

    //获取最终的输出纹理
    @Override
    public int getOutputTexture() {
        return fTexture[0];
    }

    /**
     * 所使用的mFilter就是用来渲染相机数据的Filter，该Filter所起的作用就是将相机数据的方向调整正确。然后通过绑定FrameBuffer并制定接受渲染的Texture，就可以将相机数据以一个正确的方向渲染到这个指定的Texture上了。
     */
    @Override
    public void draw() {
        //glEnable(GL_DEPTH_TEST) ：启用了之后，OpenGL在绘制的时候就会检查，当前像素前面是否有别的像素，如果别的像素挡道了它，那它就不会绘制，也就是说，OpenGL就只绘制最前面的一层。
        boolean a = GLES20.glIsEnabled(GLES20.GL_DEPTH_TEST);
        if (a) {//要对多层纹理执行操作，得关闭该模式
            GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        }
        if (mSurfaceTexture != null) {
            //通知更新回调
            mSurfaceTexture.updateTexImage();
            //调用getTransformMatrix()来转换纹理坐标,更新矩阵
            mSurfaceTexture.getTransformMatrix(mCoordOM);
            //再将纹理坐标传递给openGl，下次绘制时重置句柄
            mFilter.setCoordMatrix(mCoordOM);
        }
        //在绘制之前调用这个方法是让我们后续的渲染，渲染到fTexture[0]这个纹理上:FBO离屏渲染
        EasyGlUtils.bindFrameTexture(fFrame[0], fTexture[0]);
        //将相机的数据渲染到屏幕上，我们需要将内容再渲染到制定的窗口上，这里只渲染相机的原始纹理，其他添加的纹理后期离屏处理
        GLES20.glViewport(0, 0, width, height);
        mFilter.setTextureId(mCameraTexture[0]);
        mFilter.draw();

        EasyGlUtils.unBindFrameBuffer();

        if (a) {
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        }
    }

    @Override
    protected void onCreate() {
        //创建基本的相机渲染glsl
        mFilter.create();
        //生成一个纹理
        createOesTexture();
        //将产生的纹理交给SurfaceTexture
        mSurfaceTexture = new SurfaceTexture(mCameraTexture[0]);
    }

    @Override
    protected void onSizeChanged(int width, int height) {
        mFilter.setSize(width, height);
        //宽或高有一个产生变化，纹理重置
        if (this.width != width || this.height != height) {
            this.width = width;
            this.height = height;
            //先删除
            deleteFrameBuffer();
            //再重新创建FrameBuffer和Texture
            GLES20.glGenFramebuffers(1, fFrame, 0);
            //绑定
            EasyGlUtils.genTexturesWithParameter(1, fTexture, 0, GLES20.GL_RGBA, width, height);
        }
    }

    private void deleteFrameBuffer() {
        GLES20.glDeleteFramebuffers(1, fFrame, 0);
        GLES20.glDeleteTextures(1, fTexture, 0);
    }

    /**
     * glGenTextures(GLsizei n, GLuint *textures)函数:是用来生成纹理的函数
     * n：用来生成纹理的数量
     * textures：存储纹理索引的第一个元素指针
     */
    private void createOesTexture() {
        GLES20.glGenTextures(1, mCameraTexture, 0);
    }
}

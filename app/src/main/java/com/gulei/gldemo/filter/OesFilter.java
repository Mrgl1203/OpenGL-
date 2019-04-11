package com.gulei.gldemo.filter;

import android.content.res.Resources;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import java.util.Arrays;

/**
 * Created by gl152 on 2019/3/15.
 * 相机基本预览滤镜
 */

public class OesFilter extends AFilter {
    //纹理句柄
    private int mHCoordMatrix;
    //单位矩阵4*4复制
    private float[] mCoordMatrix = Arrays.copyOf(OM, 16);

    public OesFilter(Resources mRes) {
        super(mRes);
    }

    @Override
    protected void onCreate() {
        //创建openGl program，顶点着色器vertex，片元着色器fragment
        createProgramByAssetsFile("shader/oes_base_vertex.glsl", "shader/oes_base_fragment.glsl");
        //获取纹理句柄
        mHCoordMatrix = GLES20.glGetUniformLocation(mProgram, "vCoordMatrix");
    }

    public void setCoordMatrix(float[] matrix) {
        this.mCoordMatrix = matrix;
    }

    @Override
    protected void onSetExpandData() {
        super.onSetExpandData();
        //将纹理句柄交给片元着色器渲染
        GLES20.glUniformMatrix4fv(mHCoordMatrix, 1, false, mCoordMatrix, 0);
    }
    //绑定纹理操作
    @Override
    protected void onBindTexture() {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + getTextureType());
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, getTextureId());
        GLES20.glUniform1i(mHTexture, getTextureType());
    }

    @Override
    protected void onSizeChanged(int width, int height) {

    }
}

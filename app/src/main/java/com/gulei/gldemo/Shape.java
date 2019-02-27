package com.gulei.gldemo;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.View;

/**
 * Created by gl152 on 2019/2/26.
 */

public abstract class Shape implements GLSurfaceView.Renderer {

    protected View mView;

    public Shape(View mView){
        this.mView=mView;
    }

    public int loadShader(int type, String shaderCode){
        //根据type创建顶点着色器或者片元着色器
        int shader = GLES20.glCreateShader(type);
        //将资源加入到着色器中，并编译
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
}

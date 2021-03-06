package com.gulei.gldemo.filter;

import android.content.res.Resources;
import android.hardware.Camera;

/**
 * Created by gl152 on 2019/4/9.
 * 继承OesFilter实现基本的Camera预览渲染
 */

public class CameraFilter extends OesFilter {
    public CameraFilter(Resources mRes) {
        super(mRes);
    }

    @Override
    protected void initBuffer() {
        super.initBuffer();
        movie();
    }

    //切换摄像头时传入的CameraId，做一个纹理的切换
    @Override
    public void setFlag(int flag) {
        super.setFlag(flag);
        if (getFlag() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            cameraFront();
        } else if (getFlag() == Camera.CameraInfo.CAMERA_FACING_BACK) {
            cameraBack();
        }
    }

    private void cameraFront() {
        float[] coord = new float[]{
                1.0f, 0.0f,
                0.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f,
        };
        mTexBuffer.clear();
        mTexBuffer.put(coord);
        mTexBuffer.position(0);
    }

    private void cameraBack() {
        float[] coord = new float[]{
                1.0f, 0.0f,
                0.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f,
        };
        mTexBuffer.clear();
        mTexBuffer.put(coord);
        mTexBuffer.position(0);
    }

    private void movie() {
        float[] coord = new float[]{
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                1.0f, 1.0f
        };
        mTexBuffer.clear();
        mTexBuffer.put(coord);
        mTexBuffer.position(0);
    }
}

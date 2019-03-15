package com.gulei.gldemo.camera;

import android.graphics.Point;
import android.graphics.SurfaceTexture;

/**
 * Created by gl152 on 2019/3/15.
 */

public interface ICamera {
    class Config {
        float rate;//宽高比
        int minPreviewWidth;
        int minPictureWidth;
    }

    interface TakePhotoCallback {
        void onTakePhoto(byte[] bytes, int width, int height);
    }

    interface PreviewFrameCallback {
        void onPreviewFrame(byte[] bytes, int width, int height);
    }

    boolean open(int cameraId);

    void setConfig(Config config);

    boolean preview();

    boolean switchTo(int cameraId);

    void takePhoto(TakePhotoCallback callback);

    boolean close();

    void setPreviewTexture(SurfaceTexture texture);

    Point getPreviewSize();

    Point getPictureSize();

    void setOnPreviewFrameCallback(PreviewFrameCallback callback);
}

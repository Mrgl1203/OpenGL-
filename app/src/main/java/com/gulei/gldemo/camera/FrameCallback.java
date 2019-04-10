package com.gulei.gldemo.camera;

/**
 * Created by gl152 on 2019/4/10.
 */

public interface FrameCallback {

    void onFrame(byte[] bytes, long time);

}

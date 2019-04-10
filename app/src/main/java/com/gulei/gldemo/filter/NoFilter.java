package com.gulei.gldemo.filter;

import android.content.res.Resources;

/**
 * Created by gl152 on 2019/4/10.
 */

public class NoFilter extends AFilter {

    public NoFilter(Resources mRes) {
        super(mRes);
    }

    @Override
    protected void onCreate() {
        createProgramByAssetsFile("shader/base_vertex.glsl","shader/base_fragment.glsl");
    }

    @Override
    protected void onSizeChanged(int width, int height) {

    }
}

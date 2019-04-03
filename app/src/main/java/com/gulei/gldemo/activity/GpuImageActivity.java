package com.gulei.gldemo.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.gulei.gldemo.R;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageView;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageColorInvertFilter;

public class GpuImageActivity extends AppCompatActivity {
    GPUImageView gpuImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpu_image);
        gpuImageView = findViewById(R.id.GpuImage);
        GPUImage gpuImage = new GPUImage(this);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.fengj);
        gpuImageView.setImage(bitmap);
        gpuImageView.setFilter(new GPUImageColorInvertFilter());
    }
}

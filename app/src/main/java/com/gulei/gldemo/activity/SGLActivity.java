package com.gulei.gldemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.gulei.gldemo.R;
import com.gulei.gldemo.image.SGLView;

public class SGLActivity extends AppCompatActivity {
    SGLView mGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sgl);
        mGLView = findViewById(R.id.glView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
    }
}

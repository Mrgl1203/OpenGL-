package com.gulei.gldemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.gulei.gldemo.activity.BitmapActivity;
import com.gulei.gldemo.activity.SGLActivity;
import com.gulei.gldemo.activity.ShapeActivity;

//参考博客：https://blog.csdn.net/qq_32175491/article/details/79091647
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void drawShape(View view) {
        Intent intent = new Intent(MainActivity.this, ShapeActivity.class);
        startActivity(intent);
    }

    public void drawBitmap(View view) {
        Intent intent = new Intent(MainActivity.this, BitmapActivity.class);
        startActivity(intent);
    }

    public void BitmapChange(View view) {
        Intent intent = new Intent(MainActivity.this, SGLActivity.class);
        startActivity(intent);
    }
}

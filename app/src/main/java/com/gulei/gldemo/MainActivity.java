package com.gulei.gldemo;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.gulei.gldemo.activity.BitmapActivity;
import com.gulei.gldemo.activity.CameraActivity;
import com.gulei.gldemo.activity.GpuImageActivity;
import com.gulei.gldemo.activity.SGLActivity;
import com.gulei.gldemo.activity.ShapeActivity;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;

//参考博客：https://blog.csdn.net/qq_32175491/article/details/79091647
//https://blog.csdn.net/junzia/article/details/52820177
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.requestEach(Manifest.permission.CAMERA
                , Manifest.permission.RECORD_AUDIO
                , Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {

                        } else if (permission.shouldShowRequestPermissionRationale) {
                            Toast.makeText(MainActivity.this, "不给" + permission.name + "不让玩哦", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "哼，不和你玩了", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
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

    public void GpuImageChange(View view) {
        Intent intent = new Intent(MainActivity.this, GpuImageActivity.class);
        startActivity(intent);
    }

    public void toCamera(View view) {
        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
        startActivity(intent);
    }
}

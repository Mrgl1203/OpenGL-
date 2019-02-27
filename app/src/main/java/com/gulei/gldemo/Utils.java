package com.gulei.gldemo;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by gl152 on 2019/2/26.
 */

public class Utils {
    private static final String TAG = "aaa";

    public static String getFromAssets(Context context, String fileName) {
        AssetManager assetManager = context.getAssets();
        try {
            InputStreamReader inputReader = new InputStreamReader(assetManager.open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            StringBuffer Result = new StringBuffer();
            while ((line = bufReader.readLine()) != null) {
                Result.append(line);
            }
            Log.e(TAG, "getFromAssets: " + Result.toString());
            return Result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

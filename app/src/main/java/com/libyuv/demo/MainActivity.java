package com.libyuv.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.libyuv.util.YuvUtil;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image_view);
        findViewById(R.id.process_btn).setOnClickListener(this);
        findViewById(R.id.reset_btn).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        YuvUtil.test();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.process_btn) {
            final Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Log.d(TAG, "onClick: width = " + width + ", height = " + height);

            byte[] i420 = new byte[width * height * 3 / 2];
            Util.fetchI420(bitmap, i420);
            byte[] tempNV21 = new byte[i420.length];

//            YuvUtil.yuvI420ToNV21(i420, width, height, tempNV21);

            int degree = 90;
            int destW = width;
            int destH = height;
            if (degree == 90 || degree == 270) {
                destW = height;
                destH = width;
            }

            long start = System.currentTimeMillis();
            YuvUtil.yuvI420RotateAndToNV21(i420, width, height, tempNV21, degree);
            Log.e(TAG, "onClick: i420 rotate and convert to nv21 consume = " + (System.currentTimeMillis() - start));

            YuvImage yuvImage = new YuvImage(tempNV21, ImageFormat.NV21, destW, destH, null);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            yuvImage.compressToJpeg(new Rect(0, 0, destW, destH), 100, outputStream);
            byte[] array = outputStream.toByteArray();
            final Bitmap tmp = BitmapFactory.decodeByteArray(array, 0, array.length);
            imageView.setImageBitmap(tmp);
            Log.e(TAG, "refresh ui");
        } else if (v.getId() == R.id.reset_btn) {

        }
    }
}

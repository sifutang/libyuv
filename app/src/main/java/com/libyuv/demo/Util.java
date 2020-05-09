package com.image.post.process;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Util {

    private static final String TAG = "Util";

    public static int[] fetchRgba(@NonNull Bitmap bitmap) {
        ByteBuffer byteBuffer = ByteBuffer
                .allocateDirect(bitmap.getByteCount())
                .order(ByteOrder.nativeOrder());
        bitmap.copyPixelsToBuffer(byteBuffer);
        byte[] array = byteBuffer.array();
        int[] rgba = new int[array.length];
        int count = array.length / 4;
        for (int i = 0; i < count; i++) {
            rgba[i * 4] = ((int)array[i * 4]) & 0xff;         // R
            rgba[i * 4 + 1] = ((int)array[i * 4 + 1]) & 0xff; // G
            rgba[i * 4 + 2] = ((int)array[i * 4 + 2]) & 0xff; // B
            rgba[i * 4 + 3] = ((int)array[i * 4 + 3]) & 0xff; // A
        }

        return rgba;
    }

    /**
     * fetch nv21 data from bitmap
     * @param bitmap   bitmap
     * @param nv21     nv21 data from bitmap
     * @param alpha    image alpha data
     */
    public static void fetchNv21(@NonNull Bitmap bitmap, @NonNull byte[] nv21, int[] alpha) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int size = w * h;
        int[] pixels = new int[size];
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h);

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int yIndex = i * w + j;
                int argb = pixels[yIndex];
                int r = (argb >> 16) & 0xff;
                int g = (argb >> 8) & 0xff;
                int b = argb & 0xff;

                int y = ((66 * r + 129 * g + 25 * b + 128) >> 8) + 16;
                y = clamp(y, 16, 255);
                nv21[yIndex] = (byte) y;

                if (i % 2 == 0 && j % 2 == 0) {
                    int u = ((-38 * r - 74 * g + 112 * b + 128) >> 8) + 128;
                    int v = ((112 * r - 94 * g -18 * b + 128) >> 8) + 128;

                    u = clamp(u, 0, 255);
                    v = clamp(v, 0, 255);

                    nv21[size + i / 2 * w + j] = (byte) v;
                    nv21[size + i / 2 * w + j + 1] = (byte) u;
                }

                if (alpha != null) {
                    alpha[yIndex] = (argb >> 24) & 0xff;
                }
            }
        }
    }

    /**
     * merge dest yuv to source yuv by yuv alpha blending algorithm
     * @param startX      merge start x point
     * @param startY      merge start y point
     * @param destW       dest yuv data width
     * @param destH       dest yuv data height
     * @param destYuv     dest yuv data
     * @param alpha       dest yuv alpha data
     * @param sourceW     source yuv data width
     * @param sourceH     source yuv data height
     * @param sourceYuv   source yuv data
     */
    public static void nv21AlphaMerge(int startX, int startY,
                                      int destW, int destH, byte[] destYuv, int[] alpha,
                                      int sourceW, int sourceH, byte[] sourceYuv) {
        long start = System.currentTimeMillis();
        if (startX < 0 || startX >= sourceW || startY < 0 || startY >= sourceH) {
            throw new IllegalArgumentException("startX or startY invalid");
        }

        startX = startX & ~1;
        startY = startY & ~1;

        int sourceWH = sourceW * sourceH;
        int destWH = destW * destH;
        for (int i = 0; i < destH; i++) {
            int destYIndex = i * destW;
            int destVuIndex = destWH + i / 2 * destW;

            int sourceYIndex = startX + (i + startY) * sourceW;
            int sourceVuIndex = sourceWH + startX + (i / 2 + startY / 2) * sourceW;

            for (int j = 0; j < destW; j++) {
                int a = alpha[destYIndex + j];
                if (a == 0) continue;
                // y
                int dest = destYuv[destYIndex + j] & 0xff;
                int source = sourceYuv[sourceYIndex + j] & 0xff;
                sourceYuv[sourceYIndex + j] = (byte) ((dest * a + source * (255 - a)) >> 8);
                if (i % 2 == 0 && j % 2 == 0) {
                    // v
                    dest = destYuv[destVuIndex + j] & 0xff;
                    source = sourceYuv[sourceVuIndex + j] & 0xff;
                    sourceYuv[sourceVuIndex + j] = (byte) ((dest * a + source * (255 - a)) >> 8);
                    // u
                    dest = destYuv[destVuIndex + j + 1] & 0xff;
                    source = sourceYuv[sourceVuIndex + j + 1] & 0xff;
                    sourceYuv[sourceVuIndex + j + 1] = (byte) ((dest * a + source * (255 - a)) >> 8);
                }
            }
        }
        Log.e(TAG, "nv21AlphaMerge consume = " + (System.currentTimeMillis() - start));
    }

    private static void print(byte[] arr, int w, int h) {
        Log.e(TAG, "print: arr.length = " + arr.length + ", w = " + w + ", h = " + h);
        byte lastValue = 0;
        for (byte value : arr) {
            if (lastValue != value) {
                Log.d(TAG, "print: value = " + value + ", to int = " + (value & 0xff));
                lastValue = value;
            }
        }
    }


    private static int clamp(int amount, int low, int high) {
        return amount < low ? low : (Math.min(amount, high));
    }

    public static void test() {
        int w = 8;
        int h = 8;
        int dw = 4;
        int dh = 4;

        int size = w * h;
        int nv21size = w * h * 3 / 2;
        Log.d(TAG, "test: size = " + size + ", nv21size = " + nv21size);
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int yIndex = i * w + j;
                int vIndex = size + (i >> 1) * w + (j & ~1);
                int uIndex = size + (i >> 1) * w + (j & ~1) + 1;
                Log.d(TAG, "test: yIndex = " + yIndex + ", vIndex = " + vIndex + ", uIndex = " + uIndex);
            }
        }

        int startX = 2;
        int startY = 2;
        startX = startX & ~1;
        startY = startY & ~1;
        dw = Math.min(dw, w);
        dh = Math.min(dh, h);
        Log.d(TAG, "test: x = " + startX + ", y = " + startY + ", dw = " + dw + ", dh = " + dh);

        int sourceWH = w * h;
        int destWH = dw * dh;
        for (int i = 0; i < dh; i++) {
            int destYIndex = i * dw;
            int destVuIndex = destWH + i / 2 * dw;

            int sourceYIndex = startX + (i + startY) * w;
            int sourceVuIndex = sourceWH + startX + (i / 2 + startY / 2) * w;
            for (int j = 0; j < dw; j++) {
//                Log.i(TAG, "test y: destYIndex = " + (destYIndex + j) + ", sourceYIndex = " + (sourceYIndex + j));

                if (i % 2 == 0 && j % 2 == 0) {
                    Log.i(TAG, "test: i = " + i + ", j = " + j);
                    Log.e(TAG, "test y: destYIndex = " + (destYIndex + j) + ", sourceYIndex = " + (sourceYIndex + j));
                    Log.i(TAG, "test vu: destVuIndex = " + (destVuIndex + j) + ", sourceVuIndex = " + (sourceVuIndex + j));
                    Log.i(TAG, "test vu: destVuIndex = " + (destVuIndex + j + 1) + ", sourceVuIndex = " + (sourceVuIndex + j + 1));
                }
            }
        }
    }

}

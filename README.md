**libyuv** yuv util: scale, convert, mirror yuv data
 

## Usage
rebuild project, find .so file for your project.

## Jni tips
    /**
     * Yuv crop
     *
     * @param i420Src    yuv source data
     * @param width      yuv width
     * @param height     yuv height
     * @param i420Dst    yuv dest data
     * @param dst_width  yuv dest width
     * @param dst_height yuv dest height
     * @param left       start position of clipped x, must be even
     * @param top        start position of clipped y, must be even
     **/
    public static native void yuvCropI420(byte[] i420Src, int width, int height, byte[] i420Dst, int dst_width, int dst_height, int left, int top);

    /**
     * Yuv scale
     *
     * @param i420Src   yuv source data
     * @param width     yuv width
     * @param height    yuv height
     * @param i420Dst   yuv dest data
     * @param dstWidth  yuv dest width
     * @param dstHeight yuv dest height
     * @param mode      scale mode:[0, 1, 2, 3], the smaller the number, the greater the quality loss
     */
    public static native void yuvScaleI420(byte[] i420Src, int width, int height, byte[] i420Dst, int dstWidth, int dstHeight, int mode);

    /**
     * Yuv rotate
     * @param i420Src   yuv source data
     * @param width     yuv width
     * @param height    yuv height
     * @param i420Dst   yuv dest data
     * @param degree    rotate degree:[0, 90, 180, 270]
     */
    public static native void yuvRotateI420(byte[] i420Src, int width, int height, byte[] i420Dst, int degree);

    public static native void yuvNV21ToI420(byte[] nv21Src, int width, int height, byte[] i420Dst);

    public static native void yuvI420ToNV21(byte[] i420Src, int width, int height, byte[] nv21Dst);

    public static native void yuvNV21ToI420AndRotate(byte[] nv21Src, int width, int height, byte[] i420Dst, int degree);

    public static native void yuvI420ToARGB(byte[] i420Src, int width, int height, int dst_stride, byte[] argbDst);

    public static native void yuvI420ToYUY2(byte[] i420Src, int width, int height, int dst_stride, byte[] yuy2Dst);

    public static native void yuvI420ToUYVY(byte[] i420Src, int width, int height, int dst_stride, byte[] uyvyDst);

    public static native void yuvI420ToYV12(byte[] i420Src, int width, int height, int dst_stride, byte[] yv12Dst);

    public static native void yuvYV12ToI420(byte[] yv12Src, int width, int height, byte[] i420Dst);

    public static native void yuvNV12ToI420(byte[] nv12Src, int width, int height, byte[] i420Dst);

    public static native void yuvI420ToNv12(byte[] i420Src, int width, int height, byte[] nv12Dst);

    public static native void yuvNV12ToI420AndRotate(byte[] nv12Src, int width, int height, byte[] i420Dst, int degree);

    public static native void yuvI420Copy(byte[] i420Src, int width, int height, int dst_stride, byte[] i420Dst);

    public static native void yuvUYVYToI420(byte[] uyvySrc, int width, int height, byte[] i420Dst);

    public static native void yuvYUY2ToI420(byte[] yuy2Src, int width, int height, byte[] i420Dst);

    public static native void yuvMirrorI420LeftRight(byte[] i420Src, int width, int height, byte[] i420Dst);

    public static native void yuvMirrorI420UpDown(byte[] i420Src, int width, int height, byte[] i420Dst);

    public static native void yuvMirrorI420LeftRightAndRotate(byte[] i420Src, int width, int height, byte[] i420Dest, int degree);
package com.musicbean.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 图片处理的工具类
 *
 * @author jiaoyang<br>
 *         email: jiaoyang@360.cn
 * @version 1.0
 * @date 2013-8-22 上午11:08:44
 */
public class BitmapUtils {

    /**
     * 用option.sampleSize进行解码加载图片。<br>
     * 首先会读取图片的大小，然后根据minWidth和minHeight，计算缩放级别，在不失真的情况下进行缩放
     *
     * @param path          图片的路径
     * @param minWidth      最小宽度，为0时返回原图
     * @param minHeight     最小高度，为0时返回原图
     * @param channelConfig 图片的加载质量，用RGB_565会节省加载需要的内存，是ARGB_8888的1/2内存使用量。设置成null则按照默认加载
     */
    public static Bitmap decode(String path, final int minWidth, final int minHeight, Bitmap.Config channelConfig) {
        // 长和宽一个是0，则返回原图
        if (minWidth * minHeight <= 0) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            if (channelConfig != null) {
                options.inPreferredConfig = channelConfig;
            }
            return BitmapFactory.decodeFile(path, options);
        }

        // 读取图片的宽高
        BitmapFactory.Options options = getBitmapOption(path);
        // 计算SampleSize
        options.inSampleSize = (int) calculateInSampleSize(options, minWidth, minHeight);

        // 用SampleSize进行解码
        if (channelConfig != null) {
            options.inPreferredConfig = channelConfig;
        }

        return BitmapFactory.decodeFile(path, options);
    }

    public static Bitmap decodeByteArray(byte[] data, int offset, int length, Bitmap.Config channelConfig, int minWidth,
                                         int minHeight) {
        // 长和宽一个是0，则返回原图
        if (minWidth * minHeight <= 0) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            if (channelConfig != null) {
                options.inPreferredConfig = channelConfig;
            }
            return BitmapFactory.decodeByteArray(data, offset, length, options);
        }

        // 读取图片的宽高
        BitmapFactory.Options options = getBitmapOption(data, offset, length);
        // 计算SampleSize
        options.inSampleSize = (int) calculateInSampleSize(options, minWidth, minHeight);
        // 用SampleSize进行解码
        if (channelConfig != null) {
            options.inPreferredConfig = channelConfig;
        }

        return BitmapFactory.decodeByteArray(data, offset, length, options);
    }

    /**
     * 用于获取图片参数，inJustDecodeBounds=false不需要重新设置
     */
    private static BitmapFactory.Options getBitmapOption(byte[] data, int offset, int length) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, offset, length, options);
        options.inJustDecodeBounds = false;

        return options;
    }

    /**
     * 用于获取图片参数，inJustDecodeBounds=false不需要重新设置
     */
    private static BitmapFactory.Options getBitmapOption(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false;

        return options;
    }

    private static float calculateInSampleSize(BitmapFactory.Options options, final int reqWidth, final int reqHeight) {
        // 读取图片的宽高
        final int height = options.outHeight;
        final int width = options.outWidth;
        float inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // 计算宽高的压缩率
            final float heightRatio = (float) height / (float) reqHeight;
            final float widthRatio = (float) width / (float) reqWidth;

            // 取压缩率较小的作为图片的压缩略，inSampleSize >= 1
            inSampleSize = Math.max(1f, heightRatio < widthRatio ? heightRatio : widthRatio);
        }
        return inSampleSize;
    }

    /**
     * 将图片按比例压缩成指定大小，替换指定路径图片，并将解码出的数据返回
     *
     * @param srcPath       源文件地址
     * @param dstPath       目标文件地址
     * @param minWidth      最小宽度
     * @param minHeight     最小高度
     * @param channelConfig 图片的加载质量，用RGB_565会节省加载需要的内存，是ARGB_8888的1/2内存使用量。设置成null则按照默认加载
     */
    public static Bitmap resizeBitmap(String srcPath, String dstPath, final int minWidth, final int minHeight,
                                      Bitmap.Config channelConfig) {
        Thread currentThread = Thread.currentThread();
        File srcFile = new File(srcPath);
        if (!srcFile.exists()) {
            return null;
        }

        if (currentThread.isInterrupted()) {
            return null;
        }
        // 读取图片的宽高
        BitmapFactory.Options options = getBitmapOption(srcPath);
        // 如果大小相同，则复制或者不做处理
        if (options.outHeight == minWidth && options.outHeight == minHeight) {
            if (!srcPath.equals(dstPath)) {
                try {
                    copyFile(srcPath, dstPath);
                } catch (IOException e) {
                }
            }
            return BitmapFactory.decodeFile(srcPath);
        }

        if (currentThread.isInterrupted()) {
            return null;
        }

        options.inSampleSize = (int) calculateInSampleSize(options, minWidth, minHeight);

        // 用SampleSize进行解码
        if (channelConfig != null) {
            options.inPreferredConfig = channelConfig;
        }

        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, options);

        if (currentThread.isInterrupted()) {
            return null;
        }

        // 计算压缩比例
        float ratio = calculateInSampleSize(options, minWidth, minHeight);
        // 压缩图片
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, (int) (options.outWidth / ratio),
                (int) (options.outHeight / ratio), true);
        bitmap.recycle();

        if (currentThread.isInterrupted()) {
            return null;
        }

        // 保存文件
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(dstPath);
            final int QUALITY = 100;
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return resizedBitmap;
    }

    /**
     * 文件复制
     */
    private static void copyFile(String src, String dst) throws IOException {
        final int BUFFER_SIZE = 1024;
        byte[] buffer = new byte[BUFFER_SIZE];
        FileInputStream fis = new FileInputStream(src);
        FileOutputStream fos = new FileOutputStream(dst);
        Thread currentThread = Thread.currentThread();
        while (fis.read(buffer) != -1) {
            // 请求中断则停止拷贝
            if (currentThread.isInterrupted()) {
                break;
            }
            fos.write(buffer);
        }
        fos.flush();
        fos.close();
        fis.close();

        // 如果说被中断，则需要删除拷贝的目标文件
        if (currentThread.isInterrupted()) {
            new File(src).delete();
        }
    }

    /**
     * 获得类型为RGB565的BitmapFactory.Options
     */
    public static BitmapFactory.Options getRgb565Option() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return options;
    }

    public static void setImageViewBitmap(ImageView imageView, Bitmap bitmap) {
        imageView.setImageDrawable(null);
        imageView.setImageBitmap(bitmap);
        // Bitmap bitmap = ((BitmapDrawable)
        // imageView.getDrawable()).getBitmap();
        // imageView.setImageDrawable(drawable);
        // if((bitmap != null) && (!bitmap.isRecycled())){
        // // bitmap.recycle();
        // bitmap = null;
        // }
    }

    /**
     * 获取bitmap的大小
     *
     * @param bitmap
     * @return
     */
    @SuppressLint("NewApi")
    public static long getBitmapsize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }
        // Pre HC-MR1
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    /**
     * 明星场的头像
     *
     * @param vipBitmap
     * @param headBitmap
     * @return
     */
    public static Bitmap getSatrBitmap(Bitmap vipBitmap, Bitmap headBitmap) {
        try {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.WHITE);
            int radius = headBitmap.getWidth() < headBitmap.getHeight() ? headBitmap.getWidth()
                    : headBitmap.getHeight();
            Bitmap circleBitmap = Bitmap.createBitmap(radius, radius, Bitmap.Config.ARGB_8888);
            Canvas circleCanvas = new Canvas(circleBitmap);
            circleCanvas.drawARGB(0, 0, 0, 0);
            RectF circleRectF = new RectF(0, 0, radius, radius);
            circleCanvas.drawRoundRect(circleRectF, radius / 2, radius / 2, paint);
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            circleCanvas.drawBitmap(headBitmap, 0, 0, paint);
            Paint srcPaint = new Paint();
            paint.setAntiAlias(true);
            srcPaint.setColor(Color.WHITE);
            Bitmap bitmap = Bitmap.createBitmap(vipBitmap.getWidth(), vipBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            RectF rectF = new RectF((vipBitmap.getWidth() * 0.15f / 2), (vipBitmap.getWidth() * 0.15f / 2),
                    vipBitmap.getWidth() - (vipBitmap.getWidth() * 0.15f / 2),
                    (vipBitmap.getWidth() * 0.15f / 2) + (vipBitmap.getWidth() * 0.85f));
            canvas.drawOval(rectF, srcPaint);
            canvas.drawBitmap(circleBitmap, null, rectF, srcPaint);
            canvas.drawBitmap(vipBitmap, 0, 0, srcPaint);
            return bitmap;
        } catch (Exception e) {
            // TODO: handle exception
            return null;
        }
    }

    /**
     * 弹幕会员的头像
     */
    public static Bitmap getVipBitmap(Bitmap vipBitmap, int vipWidth, Bitmap headBitmap, int headWidth) {
        try {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.WHITE);
            int radius = headWidth;
            Bitmap circleBitmap = Bitmap.createBitmap(radius, radius, Bitmap.Config.ARGB_8888);
            Canvas circleCanvas = new Canvas(circleBitmap);
            circleCanvas.drawARGB(0, 0, 0, 0);
            RectF circleRectF = new RectF(0, 0, radius, radius);
            circleCanvas.drawRoundRect(circleRectF, radius / 2, radius / 2, paint);
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

            float sx = (float) headWidth / (float) headBitmap.getWidth();
            float sy = (float) headWidth / (float) headBitmap.getHeight();
            circleCanvas.save();
            circleCanvas.scale(sx, sy);
            circleCanvas.drawBitmap(headBitmap, 0, 0, paint);
            circleCanvas.restore();

            Paint bpaint = new Paint();
            bpaint.setAntiAlias(true);
            bpaint.setColor(Color.parseColor("#FFDF6E"));
            bpaint.setStyle(Style.STROKE);
            int bound = 2;
            bpaint.setStrokeWidth(bound);
            int delta = bound / 2;
            circleRectF = new RectF(delta, delta, radius - delta, radius - delta);
            circleCanvas.drawRoundRect(circleRectF, (radius - bound) / 2, (radius - bound) / 2, bpaint);

            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));
            RectF dst = new RectF(radius - vipWidth, radius - vipWidth, radius, radius);
            circleCanvas.drawBitmap(vipBitmap, null, dst, paint);

            return circleBitmap;
        } catch (Exception e) {
            // TODO: handle exception
            return null;
        }
    }

    /**
     * 圆形头像
     *
     * @param srcBitmap
     * @return
     */
    public static Bitmap getCircleBitmap(Bitmap srcBitmap) {
        try {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.WHITE);
            int radius = srcBitmap.getWidth() > srcBitmap.getHeight() ? srcBitmap.getHeight() : srcBitmap.getWidth();
            Bitmap bitmap = Bitmap.createBitmap(radius, radius, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawARGB(0, 0, 0, 0);
            RectF rectF = new RectF(0, 0, radius, radius);
            canvas.drawRoundRect(rectF, radius / 2, radius / 2, paint);
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas.drawBitmap(srcBitmap, 0, 0, paint);
            return bitmap;
        } catch (Exception e) {
            // TODO: handle exception
            return null;
        }

    }

    public static Bitmap getCircleBitmapWithBound(Bitmap srcBitmap, float bound, int with) {
        try {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.WHITE);
            int radius = with;// srcBitmap.getWidth() > srcBitmap.getHeight() ?
            // srcBitmap.getHeight() : srcBitmap.getWidth();
            Bitmap bitmap = Bitmap.createBitmap(radius, radius, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawARGB(0, 0, 0, 0);
            RectF rectF = new RectF(0, 0, radius, radius);
            canvas.drawRoundRect(rectF, radius / 2, radius / 2, paint);
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            float sx = (float) with / (float) srcBitmap.getWidth();
            float sy = (float) with / (float) srcBitmap.getHeight();
            canvas.save();
            canvas.scale(sx, sy);
            canvas.drawBitmap(srcBitmap, 0, 0, paint);
            canvas.restore();

            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.WHITE);
            paint.setStyle(Style.STROKE);
            paint.setStrokeWidth(bound);
            float delta = bound / 2;
            rectF = new RectF(delta, delta, radius - delta, radius - delta);
            canvas.drawRoundRect(rectF, (radius - bound) / 2, (radius - bound) / 2, paint);

            return bitmap;
        } catch (Exception e) {
            // TODO: handle exception
            return null;
        }

    }

    public static Bitmap getCircleBitmapCenterinside(Bitmap srcBitmap, int with) {
        try {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.WHITE);
            int radius = with;// srcBitmap.getWidth() > srcBitmap.getHeight() ?
            // srcBitmap.getHeight() : srcBitmap.getWidth();
            Bitmap bitmap = Bitmap.createBitmap(radius, radius, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawARGB(0, 0, 0, 0);
            RectF rectF = new RectF(0, 0, radius, radius);
            canvas.drawRoundRect(rectF, radius / 2, radius / 2, paint);
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));
            float sx = (float) with / (float) srcBitmap.getWidth();
            float sy = (float) with / (float) srcBitmap.getHeight();
            float ss = sx > sy ? sy : sx;
            canvas.save();
            canvas.scale(ss, ss);
            canvas.translate((with / ss - srcBitmap.getWidth()) / 2f,
                    (with / ss - srcBitmap.getHeight()) / 2f);
            canvas.drawBitmap(srcBitmap, 0, 0, paint);
            canvas.restore();

            // paint = new Paint();
            // paint.setAntiAlias(true);
            // paint.setColor(Color.WHITE);
            // paint.setStyle(Style.STROKE);
            // paint.setStrokeWidth(bound);
            // float delta = bound / 2;
            // rectF = new RectF(delta, delta, radius - delta, radius - delta);
            // canvas.drawRoundRect(rectF, (radius - bound) / 2, (radius -
            // bound) / 2, paint);

            return bitmap;
        } catch (Exception e) {
            // TODO: handle exception
            return null;
        }

    }

    @SuppressWarnings("deprecation")
    public static String getImageUriPath(Uri uri, Activity activity) {
        String scheme = uri.getScheme();
        if (scheme.equals("file")) {
            return uri.getPath();
        }

        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            return null;
        }

    }

    public static boolean saveBitmap(Bitmap bitmap, File dstfile) {
        try {
            FileOutputStream out = new FileOutputStream(dstfile.getAbsolutePath());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
            out = null;
            bitmap.recycle();
            bitmap = null;
            return true;
        } catch (Exception e) {
            // TODO: handle exception
            return false;
        } finally {

        }
    }
}

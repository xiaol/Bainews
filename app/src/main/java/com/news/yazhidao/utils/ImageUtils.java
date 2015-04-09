package com.news.yazhidao.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by Berkeley on 3/30/15.
 */
public class ImageUtils {

    private static final String TAG = "ImageUtils";

    public static Bitmap getRoundCornerBitmap(Bitmap bitmap, float roundPX){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap bitmap2 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap2);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);

        paint.setColor(color);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, roundPX, roundPX, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return bitmap2;
    }


    public static Bitmap getRoundedCornerBitmap(Context context, Bitmap input, int pixels , boolean isSquareTL, boolean isSquareTR, boolean isSquareBL, boolean isSquareBR  ) {
        int w=input.getWidth();
        int h=input.getHeight();
        Bitmap output = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, w, h);
        final RectF rectF = new RectF(rect);

        //make sure that our rounded corner is scaled appropriately
        final float roundPx = pixels*densityMultiplier;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);


        //draw rectangles over the corners we want to be square
        if (!isSquareTL ){
            canvas.drawRect(0, 0, w/2, h/2, paint);
        }
        if (!isSquareTR ){
            canvas.drawRect(w/2, 0, w, h/2, paint);
        }
        if (!isSquareBL ){
            canvas.drawRect(0, h/2, w/2, h, paint);
        }
        if (!isSquareBR ){
            canvas.drawRect(w/2, h/2, w, h, paint);
        }

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(input, 0,0, paint);

        return output;
    }

    /**
     * 高和宽等比例缩放
     *
     * @param bm
     * @param newWidth
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap bm, int newWidth) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        Logger.e(TAG, ">>>>>>>scaleWidth>>" + scaleWidth);
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleWidth);//横竖都按照水平方向来缩放
        // 得到新的图片bitmap
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    /**
     * 高和宽等比例缩放
     *
     * @param bm
     * @param screenWidth
     * @return
     */
    public static Bitmap zoomBitmap2(Bitmap bm, int screenWidth,int screenHeight) {
        Bitmap bitmap_big = zoomBitmap(bm,screenWidth);

        int newHeight = bitmap_big.getHeight();

        if(newHeight > screenHeight * 0.27){
            newHeight = (int)(screenHeight * 0.27);
        }

        Bitmap bitmap = Bitmap.createBitmap(bitmap_big,0,0,screenWidth,newHeight);

        return bitmap;
    }

}

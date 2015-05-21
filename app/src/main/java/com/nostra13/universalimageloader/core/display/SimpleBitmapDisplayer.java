/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.nostra13.universalimageloader.core.display;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;

import com.news.yazhidao.common.GlobalParams;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.FastBlur;
import com.news.yazhidao.utils.ImageUtils;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;

/**
 * Just displays {@link android.graphics.Bitmap} in {@link com.nostra13.universalimageloader.core.imageaware.ImageAware}
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.5.6
 */
public final class SimpleBitmapDisplayer implements BitmapDisplayer {

    private static final int TYPE_TEXTVIEW_EXTEND = 1;
    private static final int TYPE_TEXTVIEW_VERTICAL = 2;


    @Override
    public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
        imageAware.setImageBitmap(bitmap);
    }

    @Override
    public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom ,View tv_title) {

//        if(tv_title instanceof TextViewExtend) {
//            bitmap = ImageUtils.zoomBitmap2(bitmap, GlobalParams.screenWidth, GlobalParams.screenHeight, TYPE_TEXTVIEW_EXTEND);
//
//            if (tv_title != null) {
//                blur(bitmap, tv_title);
//            }
//
//        }else if(tv_title instanceof TextViewVertical){
//
//
//            int height = bitmap.getHeight();
//            int width = bitmap.getWidth();
//
//            if(width >= GlobalParams.screenWidth && height >= (int)(GlobalParams.screenHeight * 0.4)){
//                bitmap = Bitmap.createBitmap(bitmap, 0, 0, GlobalParams.screenWidth, (int) (GlobalParams.screenHeight * 0.4));
//            }else if(width >= GlobalParams.screenWidth && height < (int)(GlobalParams.screenHeight * 0.4)){
//
//
//
//                // 计算缩放比例
//                float scale = ((float) (GlobalParams.screenHeight * 0.4)) / height;
//
//                // 取得想要缩放的matrix参数
//                Matrix matrix = new Matrix();
//                matrix.postScale(scale,scale);//横竖都按照水平方向来缩放
//
//                // 得到新的图片bitmap
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inJustDecodeBounds = true;
//
//                Bitmap newbm = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
//                bitmap = Bitmap.createBitmap(newbm, 0, 0, GlobalParams.screenWidth, (int) (GlobalParams.screenHeight * 0.4));
//
//            }else if(width < GlobalParams.screenWidth && height >= (int)(GlobalParams.screenHeight * 0.4)){
//                bitmap = ImageUtils.zoomBitmap2(bitmap, GlobalParams.screenWidth, GlobalParams.screenHeight, TYPE_TEXTVIEW_VERTICAL);
//            }else if(width < GlobalParams.screenWidth && height < (int)(GlobalParams.screenHeight * 0.4)){
//                bitmap = ImageUtils.zoomBitmap2(bitmap, GlobalParams.screenWidth, GlobalParams.screenHeight, TYPE_TEXTVIEW_VERTICAL);
//
//                if(bitmap.getHeight() < GlobalParams.screenHeight * 0.4){
//                    float scaleHeight = (float)(GlobalParams.screenHeight * 0.4 / bitmap.getHeight());
//
//                    bitmap = ImageUtils.zoomBitmap3(bitmap, GlobalParams.screenWidth, GlobalParams.screenHeight, TYPE_TEXTVIEW_VERTICAL);
//                }
//
//            }
//
//        }else{
//
//            int width = DensityUtil.dip2px(GlobalParams.context,110);
//            int height = DensityUtil.dip2px(GlobalParams.context,100);
//
//            //按照宽度是110的规格来等比例缩放
//            bitmap = ImageUtils.zoomBitmap(bitmap,width);
//
//            //如果高度 小于100 进行2次缩放
//            if(bitmap.getHeight() < height){
//                int bitmap_width = height * width /bitmap.getHeight();
//                bitmap = ImageUtils.zoomBitmap(bitmap,bitmap_width);
//
//                int shift_hori = DensityUtil.dip2px(GlobalParams.context,27);
//
//                //如果加上偏移量超过图片的宽度
//                if(shift_hori + width >= bitmap.getWidth()){
//                    bitmap = Bitmap.createBitmap(bitmap, shift_hori, bitmap.getWidth() - width, bitmap.getWidth(), height);
//                }else {
//                    //反之
//                    bitmap = Bitmap.createBitmap(bitmap, shift_hori, 0, width + shift_hori, height);
//                }
//            }else {
//
//                int shift = DensityUtil.dip2px(GlobalParams.context, 10);
//
//                //如果偏移量超过图片的高度
//                if (shift + height >= bitmap.getHeight()) {
//                    bitmap = Bitmap.createBitmap(bitmap, 0, bitmap.getHeight() - height, width, bitmap.getHeight());
//                } else {
//                    //反之
//                    bitmap = Bitmap.createBitmap(bitmap, 0, shift, width, shift + height);
//                }
//            }
//
//
//        }

        imageAware.setImageBitmap(bitmap);

    }

    private void blur(Bitmap bkg, View view) {
        long startMs = System.currentTimeMillis();
        float scaleFactor = 3;
        float radius = 1;

        Bitmap overlay = null;

        if (view.getMeasuredHeight() == 0 || view.getMeasuredHeight() == 0) {
            overlay = Bitmap.createBitmap(200,200, Bitmap.Config.ARGB_8888);
        } else {
            overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth() / scaleFactor),
                    (int) (view.getMeasuredHeight() / scaleFactor), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(overlay);
//        canvas.translate(-2 / scaleFactor, 0);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);//消除锯齿

        if (canvas != null && bkg != null && paint != null) {

            int px = DensityUtil.dip2px(GlobalParams.context, 61);

            canvas.drawBitmap(bkg, 0, px, paint);
            canvas.drawColor(new Color().parseColor("#66000000"));
        }

        overlay = FastBlur.doBlur(overlay, (int) radius, true);
        overlay = ImageUtils.getRoundedCornerBitmap(GlobalParams.context, overlay, 1, false, true, false, true);
        view.setBackgroundDrawable(new BitmapDrawable(overlay));

        if (overlay != null) {
            overlay = null;
        }

        Log.e("xxxx", System.currentTimeMillis() - startMs + "ms");
    }


}
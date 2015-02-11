package com.here.superaitaotaotv.mydiary;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by superaitaotaoTV on 15/10/14.
 */
public class Rounder {

    public static Bitmap getRoundedShape(Bitmap scaleBitmapImage, int width, int height) {
        int targetWidth = width;
        int targetHeight = height;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight,
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);

        Path path = new Path();

        float padding = 5;

        RectF rectF = new RectF( padding, padding, targetWidth-padding, targetHeight-padding);

        path.addRoundRect(rectF, (float)0.4*width, (float)0.3*width, Path.Direction.CCW);

        canvas.clipPath(path);

        Bitmap sourceBitmap = scaleBitmapImage;

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor("#FFFFFF"));
        paint.setStrokeWidth(padding+10);

        canvas.drawBitmap(sourceBitmap, new Rect(0, 0, sourceBitmap.getWidth(),
                sourceBitmap.getHeight()), new Rect(0, 0, targetWidth-5,
                        targetHeight-5), paint);

        //canvas.drawCircle(targetWidth/2, targetHeight/2,targetHeight/2, paint);


        return targetBitmap;
    }

}

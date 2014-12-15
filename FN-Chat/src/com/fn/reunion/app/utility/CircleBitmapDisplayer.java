package com.fn.reunion.app.utility;


import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.shapes.RoundRectShape;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;

public class CircleBitmapDisplayer implements BitmapDisplayer {

	private float borderWidth = 0;
	private int borderColor;

	public CircleBitmapDisplayer() {
		super();
	}

	public CircleBitmapDisplayer(int borderColor, int borderWidth){
		super();
		this.borderColor = borderColor;
		this.borderWidth = borderWidth;
	}
	
	public Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        final Paint paint = new Paint();
        float radius = bitmap.getWidth() / 2f < bitmap.getHeight() / 2f ? bitmap.getWidth() / 2f : bitmap.getHeight() / 2f;
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Bitmap output = Bitmap.createBitmap(Math.round(radius * 2f), Math.round(radius * 2f), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        RoundRectShape rrs = new RoundRectShape(new float[]{radius, radius, radius, radius, radius, radius, radius, radius}, null, null);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setAntiAlias(true);
        paint.setColor(0xFF000000);
        rrs.resize(radius * 2f, radius * 2f);
        rrs.draw(canvas, paint);
        paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        
      //--ADD BORDER IF NEEDED
        if(this.borderWidth > 0){
            final Paint paint2 = new Paint();
            paint2.setAntiAlias(true);
            paint2.setColor(this.borderColor);
            paint2.setStrokeWidth(this.borderWidth);
            paint2.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, (float) (bitmap.getWidth() / 2 - Math.ceil(this.borderWidth / 2)), paint2);
        }
        return output;
    }


	@Override
    public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
			Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);

            Canvas canvas = new Canvas(output);

            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(0xFF000000);

            //--CROP THE IMAGE
            canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2 - 1, paint);
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);

            //--ADD BORDER IF NEEDED
            if(this.borderWidth > 0){
	            final Paint paint2 = new Paint();
	            paint2.setAntiAlias(true);
	            paint2.setColor(this.borderColor);
	            paint2.setStrokeWidth(this.borderWidth);
	            paint2.setStyle(Paint.Style.STROKE);
	            canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, (float) (bitmap.getWidth() / 2 - Math.ceil(this.borderWidth / 2)), paint2);
            }
            imageAware.setImageBitmap(output);
    }
    
  

}
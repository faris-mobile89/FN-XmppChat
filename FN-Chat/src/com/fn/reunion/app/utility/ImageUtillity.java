package com.fn.reunion.app.utility;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.fn.reunion.app.R;

import java.io.ByteArrayOutputStream;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Faris on 12/18/14.
 */
public class ImageUtillity {

    /**
     *  show image on Dialog
     * @param mContext
     * @param imageView
     */

    public static void showImage(Context mContext, ImageView imageView) {
        PhotoViewAttacher mAttacher;
        ImageView tempImageView = imageView;

        Dialog imageDialog = new Dialog(mContext,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        imageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.BLACK));
        imageDialog.setCancelable(true);
        LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        imageDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View view = inflater.inflate(R.layout.custom_fullimage_dialog,null);

        ImageView image = (ImageView) view.findViewById(R.id.fullimage);
        image.setImageDrawable(tempImageView.getDrawable());
        imageDialog.addContentView(view, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        mAttacher = new PhotoViewAttacher(image);
        mAttacher.canZoom();

	      /*  imageDialog.setPositiveButton(mContext.getResources().getString(R.string.ok_button), new DialogInterface.OnClickListener(){
	            public void onClick(DialogInterface dialog, int which) {
	                dialog.dismiss();
	            }
	        });*/


        imageDialog.show();
    }

    /**
     *
     * @param bitmap
     * @return
     */
    public static String getBase64Image(Bitmap bitmap){

        Bitmap selectedImage =  bitmap;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        String strBase64 = Base64.encodeToString(byteArray, 0);

        return  strBase64;

    }

    /**
     *
     * @param strBase64
     * @return
     */

    public static Bitmap getBitmap(String  strBase64){

        byte[] decodedString = Base64.decode(strBase64, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return  decodedByte;
    }


}

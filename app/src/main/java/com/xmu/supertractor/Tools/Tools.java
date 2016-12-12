package com.xmu.supertractor.Tools;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;



public class Tools {

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj) {
        return (T) obj;
    }



    private static void rceycleBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

   public static void rceycleBitmapDrawable(BitmapDrawable bitmapDrawable) {
        if (bitmapDrawable != null) {
            Bitmap bitmap = bitmapDrawable.getBitmap();
            rceycleBitmap(bitmap);
        }
   }
}

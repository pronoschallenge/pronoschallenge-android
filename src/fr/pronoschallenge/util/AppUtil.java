package fr.pronoschallenge.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Created by IntelliJ IDEA.
 * User: Thomas Delhoménie
 * Date: 18/12/10
 * Time: 18:41
 */
public class AppUtil {
    public static Drawable resizeImage(Context ctx, int resId, int w, int h) {

        // load the origial Bitmap
        Bitmap bitmapOrg = BitmapFactory.decodeResource(ctx.getResources(),
                resId);

        int width = bitmapOrg.getWidth();
        int height = bitmapOrg.getHeight();

        // calculate the scale
        float scaleWidth = ((float) w) / width;
        float scaleHeight = ((float) h) / height;

        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the Bitmap
        matrix.postScale(scaleWidth, scaleHeight);
        // if you want to rotate the Bitmap
        // matrix.postRotate(45);

        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0,
                width, height, matrix, true);

        // make a Drawable from Bitmap to allow to set the Bitmap
        // to the ImageView, ImageButton or what ever
        return new BitmapDrawable(resizedBitmap);

    }
}

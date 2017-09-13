package abat.android.boxpickergoogleglass.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.IOException;

/**
 * Created by DAJ on 06.02.2016.
 */
public class DrawableUtil {
    public static int getDrawableFromName(Context context, String name){
        return context.getResources().getIdentifier(
                name,
                "drawable",
                context.getPackageName());
    }

    public static int getBitmapOrientation(String photoPath) throws IOException {
        ExifInterface ei = new ExifInterface(photoPath);
        return ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);
    }

    public static Bitmap getCorreclyRotatedImage(String photoPath, Bitmap image) throws IOException {
        int orientation = getBitmapOrientation(photoPath);

        Bitmap rotatedImage = image;

        switch(orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedImage = rotateImage(image, 90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedImage = rotateImage(image, 180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedImage = rotateImage(image, 270);
                break;
            case ExifInterface.ORIENTATION_NORMAL:
                //image is correctly rotated
            default:
                break;
        }
        return rotatedImage;
    }

    public static Bitmap rotateImage(Bitmap image, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(),
                matrix, true);
    }
}

package abat.android.boxpickergoogleglass.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DisplayHelper {

    private static float scale;
    private static Typeface font;

    public enum Layout {LINEAR_LAYOUT, RELATIVE_LAYOUT};

    @SuppressLint("NewApi")
    public static void setAlpha(TextView textview, float aplha){
        if(Integer.valueOf(Build.VERSION.SDK_INT) >= 11){
            textview.setAlpha(aplha);
        }
    }

    /**
     * Setzt die uebergebene dp Textsize als px Textsize
     */
    public static int getSizePxFromDp(Context context, float size_in_dp){
        //Textsize entsprechend Display einstellen
        if(scale == 0){
            scale = context.getResources().getDisplayMetrics().density;
        }
        return (int) (size_in_dp * scale + 0.5f);
    }

    public static Point getDisplayDimensions(Context context){
        WindowManager windowsManager = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
        Display display = windowsManager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        // since SDK_INT = 1;
        Point size = new Point();
        size.x = metrics.widthPixels;
        size.y = metrics.heightPixels;
        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17){
            try {
                size.x = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                size.y = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            } catch (Exception ignored) {
            }
        }
        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 17){
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(display, realSize);
                size.x = realSize.x;
                size.y = realSize.y;
            } catch (Exception ignored) {
            }
        }
        return size;
    }

    public static void scaleOneImageForHeigth(Layout layout, Context context, int imageid, View view, float ratio){
        Drawable imageAsDrawable = context.getResources().getDrawable(imageid);
        Bitmap imageAsBitmap = ((BitmapDrawable) imageAsDrawable).getBitmap();

        Point size = DisplayHelper.getDisplayDimensions(context);
//	    //TODO Faktor in values auslagern
        int intendedHeight = (int) ((size.y) / ratio);  // deprecated

        // Gets the downloaded image dimensions
        int originalWidth = imageAsBitmap.getWidth();
        int originalHeight = imageAsBitmap.getHeight();

        // Calculates the new dimensions
        float scale = (float) intendedHeight / originalHeight;
        int newWidth = (int) Math.round(originalWidth * scale);

        scaleImage(layout, intendedHeight, newWidth, view);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void scaleSomeImageForHeigth(Layout layout, Activity activity, int imageid, int count, View view, float ratio){

        Drawable imageAsDrawable = activity.getResources().getDrawable(imageid);
        Bitmap imageAsBitmap = ((BitmapDrawable) imageAsDrawable).getBitmap();

        Point size = DisplayHelper.getDisplayDimensions(activity);

        //TODO move magic number into definitions
        float scale = 0.85f;
        int maxWidth = (int) ((size.x * scale) / count);

//	    int allSize = size.x * count;
//
////	//TODO Faktor in values auslagern
        int intendedHeight = (int) ((size.y) / ratio);  // deprecated

        // Gets the downloaded image dimensions
        int originalWidth = imageAsBitmap.getWidth();
        int originalHeight = imageAsBitmap.getHeight();

        // Calculates the new dimensions
        float scaleMaxWidth = (float) maxWidth / originalWidth;
//	    float scaleHeight = (float) intendedHeight / originalHeight;
        int newWidth;
//	    if(scaleMaxWidth > scaleHeight){
//	    	newWidth = (int) Math.round(originalWidth * scaleHeight);
//	    }else{
        newWidth = maxWidth;
        intendedHeight = (int) Math.round(originalHeight * scaleMaxWidth);
//	    }

        scaleImage(layout, intendedHeight, newWidth, view);
    }

    public static void scaleImage(Layout layout, int newHeight, int newWidth, View view){

        // Resizes mImageView. Change "FrameLayout" to whatever layout mImageView is located in.
        switch (layout) {
            case LINEAR_LAYOUT:
                view.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                break;
            case RELATIVE_LAYOUT:
//			RelativeLayout.LayoutParams labelLayoutParams
//			= new RelativeLayout.LayoutParams(
//					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//		    labelLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
//		    view.setLayoutParams(labelLayoutParams);
//		    view.setLayoutParams(new RelativeLayout.LayoutParams(
//		    		RelativeLayout.LayoutParams.WRAP_CONTENT,
//		    		RelativeLayout.LayoutParams.WRAP_CONTENT));
                break;
            default:
                break;
        }
        view.getLayoutParams().width = newWidth;
        view.getLayoutParams().height = newHeight;
    }
}

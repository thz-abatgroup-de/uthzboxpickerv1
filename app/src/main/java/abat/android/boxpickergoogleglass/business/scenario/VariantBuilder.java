package abat.android.boxpickergoogleglass.business.scenario;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import abat.android.boxpickergoogleglass.R;


/**
 * Created by sedi on 08.02.2017.
 */

public class VariantBuilder {
    static List<LinearLayout> mShelf = new ArrayList<>();

    public static int setVariantLayout(Context context) {
        if (isVariant1(context) == true) {
            return R.layout.activity_fullscreen_scenario1;
        } else {
            return R.layout.activity_fullscreen;
        }
    }

   public static TypedArray getProducts(Context context) {
       TypedArray products;
       if (isVariant1(context)) {
           products = context.getResources().obtainTypedArray(getResources(context).products_150px);
       } else {
           products = context.getResources().obtainTypedArray(getResources(context).products);
       }
       return products;
   }

    public static boolean isVariant1(Context context){
        return context.getResources().getBoolean(getResources(context).is_variant1);
    }

    private static ScenarioResource getResources(Context context){
        return ScenarioResource.getScenarioResource(context.getString(R.string.scenario));
    }
}

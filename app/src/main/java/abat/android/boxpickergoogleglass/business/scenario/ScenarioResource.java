package abat.android.boxpickergoogleglass.business.scenario;

import abat.android.boxpickergoogleglass.R;

/**
 * Created by sedi on 08.02.2017.
 */

public class ScenarioResource {

    protected ScenarioResource(){}

    public static ScenarioResource getScenarioResource(String scenario){
        switch (scenario){
            case "Mexabat":{
                return new ScenarioMexAbatResource();
            }
            case "Logimat":{
                return new ScenarioLogimatResource();
            }
            case "Demo":{
                return new ScenarioResource();
            }
            default:
                return new ScenarioResource();
        }
    }

    public int shelf_columns = R.integer.shelf_columns;
    public int shelf_rows = R.integer.shelf_rows;
    public int is_variant1 = R.bool.is_variant_1;
    public int top_margin = R.dimen.top_margin;
    public int products = R.array.products;
    public int products_150px = R.array.products_150px;
    public int products_in_shelfs = R.array.products_in_shelfs;
    public int split_sign = R.string.split_sign;
    public int choosing_probability = R.array.choosing_probability;
}

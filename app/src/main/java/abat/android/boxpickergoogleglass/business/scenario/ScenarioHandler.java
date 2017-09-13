package abat.android.boxpickergoogleglass.business.scenario;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import abat.android.boxpickergoogleglass.R;
import abat.android.boxpickergoogleglass.business.MainActivity;

/**
 * Created by sedi on 09.02.2017.
 */

public class ScenarioHandler{

    public static final String TAG = "abat.ScenarioHandler";

    ScenarioListener mListener;
    ScenarioEnum mCurrentState;
    ScenarioController scenarioController;

    public ScenarioHandler(ScenarioListener listener){
        mListener = listener;
        mCurrentState = ScenarioEnum.START;
    }

    public void doNext(Context context){
        if(mCurrentState == ScenarioEnum.START) {
            mCurrentState = ScenarioEnum.PICK;
            showPick(context);
           // writeData(string, context);
        } else {
            mCurrentState = ScenarioEnum.START;
            mListener.showStartScreen();
        }
    }

    private void showPick(Context context) {
        Resources res = context.getResources();
        ScenarioResource mResource = ScenarioResource.getScenarioResource(res.getString(R.string.scenario));

/*        int maxShelfs = res.getInteger(mResource.shelf_rows) * res.getInteger(mResource.shelf_columns);
        int indexOfShelf = getRandomInt(maxShelfs - 1);
        TypedArray productsInShelf = res.obtainTypedArray(mResource.products_in_shelfs);
        String[] productsWithinShelf = productsInShelf.getString(indexOfShelf).split(res.getString(mResource.split_sign));
        int indexOfProduct = Integer.parseInt(productsWithinShelf[getRandomInt(productsWithinShelf.length - 1)]);*/

        Item pick = new RandomSelector(getProbabilityArray(context)).getRandom();
        Log.i(TAG, String.format("Pick shelf %d product %d", pick.getIndexOfShelf(), pick.getIndexOfProduct()));
        String matNr = String.valueOf(pick.getIndexOfProduct());
        String shelfNr = String.valueOf(pick.getIndexOfShelf());
        ScenarioController.createPickingUnit(matNr, shelfNr);
        mListener.showPick(pick.getIndexOfShelf(), pick.getIndexOfProduct());
        //TODO SEDI  scenarioController.setState(ScanningShelfTask.INSTANCE);
    }


    private int getRandomInt(int max) {
        return getRandomInt(0, max);
    }

    private int getRandomInt(int min, int max) {
        int randomInt = (int) Math.round((Math.random() * (max - min))) + min;
        return randomInt;
    }

    private List<Item> getProbabilityArray(Context context) {
        Resources res = context.getResources();
        ScenarioResource mResource = ScenarioResource.getScenarioResource(res.getString(R.string.scenario));
        TypedArray productsInShelf = res.obtainTypedArray(mResource.products_in_shelfs);
        TypedArray choosingProbability = res.obtainTypedArray(mResource.choosing_probability);

        List<Item> items = new ArrayList<>();
        for (int indexOfShelf = 0; indexOfShelf < choosingProbability.length(); indexOfShelf++) {
            String[] probabilitiesOfProducts = choosingProbability.getString(indexOfShelf).split(res.getString(mResource.split_sign));
            String[] productsWithinShelf = productsInShelf.getString(indexOfShelf).split(res.getString(mResource.split_sign));
            for (int indexOfProduct = 0; indexOfProduct < probabilitiesOfProducts.length; indexOfProduct++) {
                int probability = Integer.parseInt(probabilitiesOfProducts[indexOfProduct]);
               // if(probability > 0) {
                    items.add(new Item(indexOfShelf, Integer.parseInt(productsWithinShelf[indexOfProduct]), probability));
               // }
            }
        }
        return items;
    }

    class Item {
        private int relativeProb;
        private int shelf;
        private int productWithinShelf;

        public Item(int shelf, int productWithinShelf, int relativeProb){
            this.shelf = shelf;
            this.productWithinShelf = productWithinShelf;
            this.relativeProb = relativeProb;
        }

        public int getIndexOfShelf(){
            return shelf;
        }

        public int getIndexOfProduct(){
            return productWithinShelf;
        }

        public int getRelativeProb() {
            return relativeProb;
        }
    }

    class RandomSelector {
        List<Item> items;
        Random rand = new Random();
        int totalSum = 0;
        List<Item> probList= new ArrayList<>();

        RandomSelector(List<Item> items) {
            this.items = items;
            for (Item item : items) {
                totalSum = totalSum + item.getRelativeProb();
                for (int i = 0; i < item.getRelativeProb(); i++) {
                    if (item.getRelativeProb() > 0) {
                        probList.add(item);
                    }else{
                        //do nothing
                    }
                }
            }
        }

        public Item getRandom() {
            return this.probList.get(rand.nextInt(this.probList.size()));
        }

        /*   List<Item> items;
        Random rand = new Random();
        int totalSum = 0;

        RandomSelector(List<Item> items) {
            this.items = items;
            for(Item item : items) {
                totalSum = totalSum + item.relativeProb;
            }
        }

        public Item getRandom() {

            int index = rand.nextInt(totalSum);
            int sum = 0;
            int i=0;
            while(sum < index ) {
                sum = sum + items.get(i++).relativeProb;
            }
            return items.get(Math.max(0,i-1));
        }*/
    }
    public ScenarioEnum getState(){
        return mCurrentState;
    }

    public void writeData(String toWrite, Context context){
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(toWrite);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public String readData(Context context){
        String read = "";

        try {
            InputStream inputStream = context.openFileInput("config.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                read = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return read;
    }

    public int getDefaultDelay() {
        return 2000;
    }
}
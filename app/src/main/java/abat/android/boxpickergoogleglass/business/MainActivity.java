package abat.android.boxpickergoogleglass.business;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.zxing.integration.android.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import abat.android.boxpickergoogleglass.R;
import abat.android.boxpickergoogleglass.barcode.BarcodeScanner;
import abat.android.boxpickergoogleglass.business.scenario.DelayExecutor;
import abat.android.boxpickergoogleglass.business.scenario.ScanStockyardTask;
import abat.android.boxpickergoogleglass.business.scenario.ScanningProductTask;
import abat.android.boxpickergoogleglass.business.scenario.ScanningShelfTask;
import abat.android.boxpickergoogleglass.business.scenario.ScenarioController;
import abat.android.boxpickergoogleglass.business.scenario.ScenarioHandler;
import abat.android.boxpickergoogleglass.business.scenario.ScenarioListener;
import abat.android.boxpickergoogleglass.business.scenario.ScenarioResource;
import abat.android.boxpickergoogleglass.business.scenario.ScenarioEnum;
import abat.android.boxpickergoogleglass.business.scenario.VariantBuilder;
import abat.android.boxpickergoogleglass.util.DisplayHelper;
import abat.android.boxpickergoogleglass.voicerecognition.KeyRecognizeListener;
import abat.android.boxpickergoogleglass.voicerecognition.VoiceKey;
import abat.android.boxpickergoogleglass.voicerecognition.VoiceRecognitionHandler;


public class MainActivity extends AppCompatActivity implements KeyRecognizeListener {

    public static final String TAG = "abat.glass.main";

    private ScenarioResource mResource;

    //private TableLayout mContentView;
    private LinearLayout mContentView;

    private List<LinearLayout> mShelf = new ArrayList<>();

    private Handler mHandler;

    //current displayed text, icon and attribute
    private String currentText;
    private int currentIcon;
    private int currentAttributionIcon;
    private boolean isDelayedInfoShowed;
    IntentIntegrator scanner = new IntentIntegrator(this);
    IntentResult barcodeResult;
    static String STATE = "START";
    public static final String BARCODE_SYMBOL = "##";
    public String pickingShelf, pickingProduct;


    ScenarioListener mScenarioListener = new ScenarioListener() {

        @Override
        public void showPick(int indexOfShelf, int indexOfProduct) {
            pickingProduct = String.valueOf(indexOfProduct);
            pickingShelf = String.valueOf(indexOfShelf);
            Resources res = getResources();
            TypedArray products = VariantBuilder.getProducts(context);
            Drawable pickProduct = products.getDrawable(indexOfProduct);

            ImageView imageView = new ImageView(context);
            imageView.setImageDrawable(pickProduct);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            //display pick on glass
            if (VariantBuilder.isVariant1(context) == true) {
                for (int i = 0; i < mShelf.size(); i++) {
                    if (i == indexOfShelf) {
                        mShelf.get(i).setBackgroundColor(ContextCompat.getColor(context, R.color.blueabat));
                    } else {
                        mShelf.get(i).setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                    }
                }
                ((ImageView) findViewById(R.id.productView)).setImageDrawable(pickProduct);
            } else {
                for (int i = 0; i < mShelf.size(); i++) {
                    if (i == indexOfShelf) {
                        mShelf.get(i).addView(imageView);
                    } else {
                        mShelf.get(i).removeAllViews();
                    }
                }
            }
            String scanShelf = getResources().getString(R.string.text_scan_shelf);
            scanShelf = String.format(scanShelf, pickingShelf);
            String initPicking = scanShelf + "\r\n\n" + getResources().getText(R.string.text_scanby_touch);
            ((TextView) findViewById(R.id.message)).setText(initPicking);
            ((TextView) findViewById(R.id.message)).setTextColor(Color.parseColor("#FFFFFF"));
            setState("SCAN_SHELF");
        }

        @Override
        public void showStartScreen() {
            if (VariantBuilder.isVariant1(context) == true) {
                for (int i = 0; i < mShelf.size(); i++) {
                    mShelf.get(i).setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                }
                ((ImageView) findViewById(R.id.productView)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_scan_white_48dp));
            } else {
                for (int i = 0; i < mShelf.size(); i++) {
                    mShelf.get(i).removeAllViews();
                }
            }
            ((TextView) findViewById(R.id.message)).setText(getResources().getText(R.string.text_start));
            ((TextView) findViewById(R.id.message)).setTextColor(Color.parseColor("#FFFFFF"));
        }

        @Override
        public void onStateChange(int stateIcon) {
            ((ImageView) findViewById(R.id.productView)).setImageDrawable(ContextCompat.getDrawable(context, stateIcon));
        }

        @Override
        public void startBarcodeScanning() {
            scanBarcode();
        }

        @Override
        public void onUserMessage(String message) {
            ((TextView) findViewById(R.id.message)).setText(message);
            ((TextView) findViewById(R.id.message)).setTextColor(Color.parseColor("#FFFFFF"));
        }

        @Override
        public void onUserMessage(String message, int iconId) {
            ((ImageView) findViewById(R.id.productView)).setImageDrawable(ContextCompat.getDrawable(context, iconId));
            ((TextView) findViewById(R.id.message)).setText(message);
            ((TextView) findViewById(R.id.message)).setTextColor(Color.parseColor("#FFFFFF"));
        }
    };

    private BarcodeScanner mBarcodeScanner;
    private VoiceRecognitionHandler mVoiceRecognitionHandler;
    Context context = this;

    public ScenarioHandler mScenarioHandler;
    ScenarioEnum mCurrentState;
    ScenarioController mScenarioController = new ScenarioController(this, mScenarioListener);

    private View cardView;
    private int MY_PERMISSIONS_REQUEST_RECORD_AUDIO;
    private int MY_PERMISSIONS_REQUEST_CAMERA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mResource = ScenarioResource.getScenarioResource(getResources().getString(R.string.scenario));
        setContentView(VariantBuilder.setVariantLayout(this));

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContentView = (LinearLayout) findViewById(R.id.fullscreen_content_layout);

        List<KeyRecognizeListener> keyRecognitionHandlerList = new LinkedList<>();
        keyRecognitionHandlerList.add(this);
        mVoiceRecognitionHandler = new VoiceRecognitionHandler(this, keyRecognitionHandlerList);
        mBarcodeScanner = new BarcodeScanner(this, mScenarioController);
        //get size of shelf
        Resources res = getResources();
        int numberOfShelfColoums = res.getInteger(mResource.shelf_columns);
        int numberOfShelfRows = res.getInteger(mResource.shelf_rows);
        int marginTop = res.getDimensionPixelSize(mResource.top_margin);
        int displayHeight = DisplayHelper.getDisplayDimensions(this).y - marginTop;
        int displayWidth = (DisplayHelper.getDisplayDimensions(this).x) / 2;

        /**
         *  @param marginTop
         */

        int shelfHeight = (displayHeight - (marginTop * numberOfShelfRows)) / numberOfShelfRows;
        int shelfWidth = ((displayWidth - (marginTop * numberOfShelfColoums)) / (2 * numberOfShelfColoums));

        //create rows displayed on
        for (int i = 0; i < numberOfShelfRows; i++) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            for (int j = 0; j < numberOfShelfColoums; j++) {
                View shelfEntry = inflater.inflate(R.layout.shelf_entry, mContentView, false);
                shelfEntry.getLayoutParams().height = shelfHeight;
                shelfEntry.getLayoutParams().width = shelfWidth;
                mShelf.add((LinearLayout) shelfEntry);
                row.addView(shelfEntry);
            }
            mContentView.addView(row);
        }
        askForPermissions();
        mScenarioHandler = new ScenarioHandler(mScenarioListener);
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (STATE.startsWith("SCAN")) {
            if (keycode == KeyEvent.KEYCODE_DPAD_CENTER) {
                scanBarcode();
                // user tapped touchpad, do something
                return true;
            }

        }else if (STATE == "START"){
            if (keycode == KeyEvent.KEYCODE_DPAD_CENTER) {
                mScenarioHandler.doNext(this);
                // user tapped touchpad, do something
                return true;
            }
            return super.onKeyDown(keycode, event);
        }else {

        }
        return super.onKeyDown(keycode, event);
    }

    static void setState(String state){
        STATE = state;
    }

    public void scanBarcode() {
        scanner.initiateScan();
    }

//    /**
//     * Chooses a random shelf space and product
//     */
//    public void showNextPick() {
//        Resources res = getResources();
//        //pick random shelf
//
//       // products = mVariantBuilder.getProducts(getResources().getBoolean(mResource.scenario_toggle));
//        /*if (isScenario1() == true) {
//            products = res.obtainTypedArray(mResource.products_150px);
//        } else {
//            products = res.obtainTypedArray(mResource.products);
//        }*/
//        int maxShelfs = res.getInteger(mResource.shelf_rows) * res.getInteger(mResource.shelf_columns);
//        int pickShelf = getRandomInt(maxShelfs - 1);
//
//        //pick random product
//        TypedArray productsInShelf = res.obtainTypedArray(mResource.products_in_shelfs);
//        String[] productsWithinShelf = productsInShelf.getString(pickShelf).split(res.getString(mResource.split_sign));
//        int indexOfProduct = Integer.parseInt(productsWithinShelf[getRandomInt(productsWithinShelf.length - 1)]);
//
//    }
//
//    /**
//     * Returns a random number between 0 and max
//     *
//     * @param max
//     * @return
//     */
//    private int getRandomInt(int max) {
//        return getRandomInt(0, max);
//    }
//
//    /**
//     * Returns a random number between min and max
//     *
//     * @param max
//     * @return
//     */
//    private int getRandomInt(int min, int max) {
//        int randomInt = (int) Math.round((Math.random() * (max - min))) + min;
//        Log.v(TAG, "min: " + min + " max: " + max + " random: " + randomInt);
//        return randomInt;
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Changes the displayed text
     *
     * @param message
     */
    private void setText(String message) {
        currentText = message;
        updateView();
    }

    /**
     * Changes the displayed text
     *
     * @param message
     * @param iconId
     */
    private void setText(String message, int iconId) {
        currentText = message;
        currentIcon = iconId;
        Log.i(TAG, "setText: " + message);
        updateView();
    }

    /**
     * Changes the displayed attributicon
     *
     * @param attributIcon
     */
    private void setAttributionIcon(int attributIcon) {
        currentAttributionIcon = attributIcon;
        updateView();
    }

    /**
     * updates the view
     */
    private void updateView() {
        TextView message = (TextView) findViewById(R.id.message);
        message.setText(currentText);

        ImageView icon = (ImageView) findViewById(R.id.icon);
        icon.setImageDrawable(ContextCompat.getDrawable(this, currentIcon));
    }

    @Override
    public void onStart() {
        super.onStart();
        SpeechRecognizer.createSpeechRecognizer(context);
        Log.i(TAG, String.valueOf(SpeechRecognizer.isRecognitionAvailable(context)));
        
        mVoiceRecognitionHandler.startListening();

//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client.connect();
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "Main Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://abat.android.warehousepickerglassapp.business/http/host/path")
//        );
//        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        mVoiceRecognitionHandler.stopListening();
        super.onStop();

//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "Main Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://abat.android.warehousepickerglassapp.business/http/host/path")
//        );
//        AppIndex.AppIndexApi.end(client, viewAction);
//        client.disconnect();
    }

    @Override
    protected void onPause() {
        mVoiceRecognitionHandler.stopListening();
        super.onPause();
    }

    /**
     * @author SEDI
     * 7.2.17
     * <p>
     * methods to convert px to dp or in other way around
     */

    private int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    private int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public int getDefaultDelay() {
        return getApplicationContext().getResources().getInteger(R.integer.delay);
    }

    public void showDelayedInfo(final String firstText, final String secondText) {
        DelayExecutor de = (DelayExecutor) new DelayExecutor(getDefaultDelay()) {
            @Override
            public void taskPostExecute() {
                if (mScenarioHandler.getState() == ScenarioEnum.PICK) {
                    ((TextView) findViewById(R.id.message)).setText(firstText);
                   DelayExecutor de2 = (DelayExecutor) new DelayExecutor(getDefaultDelay()) {
                        @Override
                        public void taskPostExecute() {
                            if (mScenarioHandler.getState() == ScenarioEnum.PICK) {
                                ((TextView) findViewById(R.id.message)).setText(secondText);
                                showDelayedInfo(firstText, secondText);
                            }
                        }
                    }.execute();
                }
            }
        }.execute();
    }

    /**
     * on ActivityResult method
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == scanner.REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                barcodeResult = scanner.parseActivityResult(requestCode, resultCode, data);
                String barcode = barcodeResult.getContents().toString();
                if(barcode.startsWith(BARCODE_SYMBOL)){
                    barcode = barcode.substring(0 + BARCODE_SYMBOL.length(), barcode.length());
                }
                if(barcode.endsWith(BARCODE_SYMBOL)){
                    barcode = barcode.substring(0, barcode.length()-BARCODE_SYMBOL.length());
                }
                Log.e(TAG, barcode);
                if(STATE == "SCAN_PRODUCT"){
                    isRightProduct(barcode);
                }else if(STATE == "SCAN_SHELF"){
                    isRightShelf(barcode);
                }
            } else {
                //TODO inform user
            }
        }
    }

    private boolean isRightProduct(String barcode) {
        if( pickingProduct.equals(barcode)){
            //((TextView) findViewById(R.id.message)).setText(getResources().getText(R.string.text_start));
            ((TextView) findViewById(R.id.message)).setText(getResources().getText(R.string.right_product));
            ((TextView) findViewById(R.id.message)).setTextColor(Color.parseColor("#00CD00"));
            setState("START");
            new DelayExecutor(getDefaultDelay()) {
                @Override
                public void taskPostExecute() {
                    new DelayExecutor(getDefaultDelay()) {
                        @Override
                        public void taskPostExecute() {
                            mScenarioHandler.doNext(context);
                        }
                    }.execute();
                    ((TextView) findViewById(R.id.message)).setText(getResources().getText(R.string.end_of_picking));
                    ((TextView) findViewById(R.id.message)).setTextColor(Color.parseColor("#FFFFFF"));
                }
            }.execute();
            return true;
        }else{
            String falseProd = getResources().getString(R.string.false_product).toString();
            falseProd = String.format(falseProd, pickingProduct);
            String falseProdScan = falseProd + "\r\n\n" + getResources().getText(R.string.text_scanby_touch);
            ((TextView) findViewById(R.id.message)).setText(falseProdScan);
            ((TextView) findViewById(R.id.message)).setTextColor(Color.parseColor("#FF3030"));
            return false;
        }
    }

    private boolean isRightShelf(String barcode){
        if(pickingShelf.equals(barcode)){
            setState("SCAN_PRODUCT");
            //((TextView) findViewById(R.id.message)).setText(getResources().getText(R.string.text_start));
            ((TextView) findViewById(R.id.message)).setText(getResources().getText(R.string.right_shelf));
            ((TextView) findViewById(R.id.message)).setTextColor(Color.parseColor("#00CD00"));
            new DelayExecutor(getDefaultDelay()) {
                @Override
                public void taskPostExecute() {
                    if (mScenarioHandler.getState() == ScenarioEnum.PICK) {
                        String scanProd = getResources().getString(R.string.text_scan_product).toString();
                        scanProd = String.format(scanProd, pickingProduct);
                        String scanProdText = scanProd + "\r\n\n" + getResources().getText(R.string.text_scanby_touch);
                        ((TextView) findViewById(R.id.message)).setText(scanProdText);
                        ((TextView) findViewById(R.id.message)).setTextColor(Color.parseColor("#FFFFFF"));
                    }
                }
            }.execute();
            return true;
        }else{
            String falseShelf = getResources().getString(R.string.false_shelf).toString();
            falseShelf = String.format(falseShelf, pickingShelf);
            String falseShelfScan = falseShelf + "\r\n\n" + getResources().getText(R.string.text_scanby_touch);
            ((TextView) findViewById(R.id.message)).setText(falseShelfScan);
            ((TextView) findViewById(R.id.message)).setTextColor(Color.parseColor("#FF3030"));
            return false;
        }
    }


    @Override
    public void keyRecognized(VoiceKey recognizedKey) {
        //Class currentTask = mScenarioController.getCurrentState();
        //Toast.makeText(this, "getCurrentState: " + mScenarioController.getCurrentState(), Toast.LENGTH_SHORT).show();
        if(ScanStockyardTask.class == mScenarioController.getCurrentState()
                || ScanningProductTask.class == mScenarioController.getCurrentState()
                || ScanningShelfTask.class == mScenarioController.getCurrentState()) {
            switch (recognizedKey) {
                case BARCODE_SCAN:
                    scanBarcode();
                    break;
            }
        }
    }

    private void askForPermissions(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, this.MY_PERMISSIONS_REQUEST_CAMERA);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }
}


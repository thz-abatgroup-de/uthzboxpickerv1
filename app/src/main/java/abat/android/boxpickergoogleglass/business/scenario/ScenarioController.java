package abat.android.boxpickergoogleglass.business.scenario;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;
import android.view.SoundEffectConstants;

import java.util.LinkedList;
import java.util.Queue;

import abat.android.boxpickergoogleglass.business.Item;
import abat.android.boxpickergoogleglass.R;
import abat.android.boxpickergoogleglass.barcode.BarcodeListener;
import abat.android.boxpickergoogleglass.business.util.TypeOfData;
import abat.android.boxpickergoogleglass.voicerecognition.KeyRecognizeListener;
import abat.android.boxpickergoogleglass.voicerecognition.VoiceKey;

/**
 * Created by DAJ on 27.01.2016.
 */
public class ScenarioController implements BarcodeListener, KeyRecognizeListener {
    public static final String TAG = "abat.glass.scenario";

    public static final Integer SOUND_SCAN_WRONG = SoundEffectConstants.CLICK;
    public static final Integer SOUND_SCAN_CORRECT = SoundEffectConstants.NAVIGATION_DOWN;

    private static Context applicationContext;

    private ScenarioState ml_currentState;
    private ScenarioListener listener;

    private String StockyardTask;

    public static Product product;

    public ScenarioController(Context context, ScenarioListener listener){
        //applicationContext = context.getApplicationContext();
        this.listener = listener;
        setState(InitTask.INSTANCE);
    }

    /**
     * Sets the passed state as current state and invokes initial behavior
     * @param state
     */
    public void setState(ScenarioState state){
        ml_currentState = state;
        ml_currentState.handleStateChanged(this);
    }

    /**
     * Returns the Name of the current state
     * @return
     */
    public Class getCurrentState(){
        return ml_currentState.getClass();
    }


    public Context getApplicationContext(){
        return applicationContext;
    }

    public ScenarioListener getListener() {
        return listener;
    }

    public String getStockyardTask() {
        return StockyardTask;
    }

    public void setStockyardTask(String stockyardTask) {
        StockyardTask = stockyardTask;
    }

    protected void userFeedback(String text, Integer iconId, Integer soundId){
        //play sound
        if(soundId != null) {
            AudioManager audio
                    = (AudioManager) getApplicationContext()
                    .getSystemService(Context.AUDIO_SERVICE);
            audio.playSoundEffect(soundId);
        }

        //update view
        if(iconId == null){
            getListener().onUserMessage(text);
        } else {
            getListener().onUserMessage(text, iconId);
        }

        //log current state
        Log.i(ScenarioController.TAG, text);
    }

    @Override
    public void onBarcodeReceived(String barcode) {
        if(getApplicationContext().getString(R.string.init_number).equals(barcode)){
            ml_currentState.handleInitRequest(this);
        }else{
            ml_currentState.handleBarcodeScanned(this, barcode);
        }
    }

    /**
     * get invoked if user expresses voice command
     */
    @Override
    public void keyRecognized(VoiceKey recognizedKey) {
        ml_currentState.handleVoiceKeyRecognized(this, recognizedKey);
    }

    public int getDefaultDelay(){
        return getApplicationContext().getResources().getInteger(R.integer.delay);
    }

    /**
     * Starts the scanning via the internal camera for a barcode
     */
    public void scanBarcode() {
        listener.startBarcodeScanning();
    }

    public static void createPickingUnit(String productNr, String shelfNr){
        Product productToPick = new Product(productNr, shelfNr, "224208", "1");
        product = productToPick;
    }

    public Product getProduct(){
        return this.product;
    }

    public void quitPicking(){
        this.product = null;
    }

    public void initializeController() {
        StockyardTask = "";
        product = null;
        //TODO SEDI setState(DeterminingProductsTask.INSTANCE);
    }
}

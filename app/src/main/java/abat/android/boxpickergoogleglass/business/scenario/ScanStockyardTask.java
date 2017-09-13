package abat.android.boxpickergoogleglass.business.scenario;

import android.util.Log;

import abat.android.boxpickergoogleglass.R;
import abat.android.boxpickergoogleglass.business.util.TypeOfData;
import abat.android.boxpickergoogleglass.voicerecognition.VoiceKey;

/**
 * Created by DAJ on 27.01.2016.
 */
public class ScanStockyardTask implements ScenarioState{
    protected static final ScenarioState INSTANCE = new ScanStockyardTask();
    private ScanStockyardTask(){}

    @Override
    public void handleStateChanged(ScenarioController scenarioController){
        scenarioController.getListener().onUserMessage(
                scenarioController.getApplicationContext().getString(R.string.scan_box),
                R.drawable.ic_scan_black_48dp);
        Log.i(ScenarioController.TAG,
                scenarioController.getApplicationContext().getString(R.string.scan_box));

        //TODO send barcode scanner intent
        //scenarioController.scanBarcode();
    }

    @Override
    public void handleBarcodeScanned(final ScenarioController scenarioController, String barcode) {
        if(barcode.equals(scenarioController.getApplicationContext().getString(R.string.box_number))){
            correctBoxScanned(scenarioController);
        }else{
            wrongBoxScanned(scenarioController);
//            new DelayExecutor(scenarioController.getDefaultDelay()){
//                @Override
//                public void taskPostExecute() {
//                    scenarioController.scanBarcode();
//                }
//            }.execute();

        }
    }

    @Override
    public void handleInitRequest(ScenarioController scenarioController) {
        scenarioController.initializeController();
    }

    @Override
    public void handleVoiceKeyRecognized(ScenarioController scenarioController, VoiceKey recognizedKey) {
    }

    private void correctBoxScanned(ScenarioController scenarioController){
        scenarioController.userFeedback(
                scenarioController.getApplicationContext().getString(R.string.scan_box),
                R.drawable.ic_done_green_48dp,
                ScenarioController.SOUND_SCAN_CORRECT);
        //scenarioController.setState(WorkingOnProductsTask.INSTANCE);
        scenarioController.setState(ScanningShelfTask.INSTANCE);
    }

    private void wrongBoxScanned(ScenarioController scenarioController){
        scenarioController.userFeedback(
                scenarioController.getApplicationContext().getString(R.string.scan_box),
                R.drawable.ic_wrong_red_48dp,
                ScenarioController.SOUND_SCAN_WRONG);
    }
}

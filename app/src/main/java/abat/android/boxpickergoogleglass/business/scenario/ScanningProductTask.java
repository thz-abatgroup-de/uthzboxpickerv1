package abat.android.boxpickergoogleglass.business.scenario;

import abat.android.boxpickergoogleglass.R;
import abat.android.boxpickergoogleglass.business.util.TypeOfData;
import abat.android.boxpickergoogleglass.voicerecognition.VoiceKey;

/**
 * Created by SEDI on 08.07.2016
 */
public class ScanningProductTask implements ScenarioState {
    protected static final ScenarioState INSTANCE = new ScanningProductTask();

    //SEDI
    @Override
    public void handleStateChanged(final ScenarioController scenarioController) {userInfoProduct(scenarioController);}

    @Override
    public void handleBarcodeScanned(final ScenarioController scenarioController, String barcode) {
        if(barcode.equals(scenarioController.getProduct().getMatnr())){
            userInfoCorrectProduct(scenarioController);
            new DelayExecutor(scenarioController.getDefaultDelay()){
                @Override
                public void taskPostExecute() {
                    scenarioController.setState(ConfirmPickUnitsTask.INSTANCE);
                }
            }.execute();
        }else{
            userInfoWrongProduct(scenarioController);
        }
    }
    private void goToNextState(final ScenarioController scenarioController){
        scenarioController.quitPicking();
    }

    @Override
    public void handleInitRequest(ScenarioController scenarioController) {//do nothing
    }

    @Override
    public void handleVoiceKeyRecognized(ScenarioController scenarioController, VoiceKey recognizedKey) {
    }

    protected void userInfoProduct(ScenarioController scenarioController) {
        userInfoProduct(scenarioController, R.drawable.ic_scan_black_48dp, null);
    }

    private void userInfoCorrectProduct(ScenarioController scenarioController) {
        userInfoProduct(
                scenarioController,
                R.drawable.ic_done_green_48dp,
                ScenarioController.SOUND_SCAN_CORRECT);
    }

    private void userInfoWrongProduct(ScenarioController scenarioController) {
        userInfoProduct(
                scenarioController,
                R.drawable.ic_wrong_red_48dp,
                ScenarioController.SOUND_SCAN_WRONG);
    }

    public void userInfoProduct(ScenarioController scenarioController, Integer iconId, Integer soundId) {
        String productText = getText(scenarioController, R.string.scan_material, scenarioController.getProduct().getMatnr());
        scenarioController.userFeedback(productText, iconId, soundId);
    }

    private String getText(ScenarioController scenarioController, int stringId, String value) {
        String productText = scenarioController.getApplicationContext().getString(stringId);
        return productText.replace("#1", value);
    }
}


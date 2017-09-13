package abat.android.boxpickergoogleglass.business.scenario;

import abat.android.boxpickergoogleglass.R;
import abat.android.boxpickergoogleglass.business.util.TypeOfData;
import abat.android.boxpickergoogleglass.voicerecognition.VoiceKey;

/**
 * Created by SEDI on 08.07.2016.
 */
public class ScanningShelfTask implements ScenarioState {
    protected static final ScenarioState INSTANCE = new ScanningShelfTask();
    private ScanningShelfTask(){}

    @Override
    public void handleStateChanged(final ScenarioController scenarioController) {
        new DelayExecutor(scenarioController.getDefaultDelay()) {
            @Override
            public void taskPostExecute() {
                userInfoStockyardId(scenarioController);
            }
        }.execute();
    }

    @Override
    public void handleBarcodeScanned(final ScenarioController scenarioController, String barcode) {
        if (barcode.equals(scenarioController.getProduct().getShelf())) {
            scenarioController.getProduct().setHuNo(
                    scenarioController.getApplicationContext().getString(R.string.default_hu_no));
            userInfoCorrectStockyardId(scenarioController);
            new DelayExecutor(scenarioController.getDefaultDelay()) {
                @Override
                public void taskPostExecute() {
                    userInfoStockyardId(scenarioController);
                    scenarioController.setState(ScanningProductTask.INSTANCE);
                }
            }.execute();
        }else{
            userInfoWrongStockyardId(scenarioController);
        }
    }

    @Override
    public void handleVoiceKeyRecognized(ScenarioController scenarioController, VoiceKey recognizedKey) {
    }

    @Override
    public void handleInitRequest(ScenarioController scenarioController) {
        scenarioController.initializeController();
    }

    protected void userInfoStockyardId(ScenarioController scenarioController) {
        userInfoStockyardId(scenarioController, R.drawable.ic_scan_black_48dp, null);
    }

    private void userInfoCorrectStockyardId(ScenarioController scenarioController) {
        userInfoStockyardId(
                scenarioController,
                R.drawable.ic_done_green_48dp,
                ScenarioController.SOUND_SCAN_CORRECT);
    }

    private void userInfoWrongStockyardId(ScenarioController scenarioController) {
        userInfoStockyardId(
                scenarioController,
                R.drawable.ic_wrong_red_48dp,
                ScenarioController.SOUND_SCAN_WRONG);
    }

    protected void userInfoStockyardId(ScenarioController scenarioController, Integer iconId, Integer soundId) {
        String stockyardText
                = getText(scenarioController,
                R.string.scan_stockyard,
                scenarioController.getProduct().getShelf());

        scenarioController.userFeedback(
                stockyardText,
                iconId,
                soundId);
    }

    private String getText(ScenarioController scenarioController, int stringId, String value) {
        String productText
                = scenarioController.getApplicationContext().getString(stringId);
        return productText.replace("#1", value);
    }
}

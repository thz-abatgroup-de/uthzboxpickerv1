package abat.android.boxpickergoogleglass.business.scenario;

import abat.android.boxpickergoogleglass.R;
import abat.android.boxpickergoogleglass.business.MainActivity;
import abat.android.boxpickergoogleglass.business.util.TypeOfData;
import abat.android.boxpickergoogleglass.voicerecognition.VoiceKey;

import static abat.android.boxpickergoogleglass.voicerecognition.VoiceKey.CONFIRM_PICK_UNITS;

/**
 * Created by sedi on 17.08.2016.
 */
public class ConfirmPickUnitsTask implements ScenarioState {
    protected static final ScenarioState INSTANCE = new ConfirmPickUnitsTask();
    private ConfirmPickUnitsTask(){}

    @Override
    public void handleStateChanged(final ScenarioController scenarioController) {
        showPickUnits(scenarioController);
    }

    @Override
    public void handleBarcodeScanned(ScenarioController scenarioController, String barcode) {
//do nothing
    }

    @Override
    public void handleInitRequest(ScenarioController scenarioController) {
//do nothing
    }

    @Override
    public void handleVoiceKeyRecognized(ScenarioController scenarioController, VoiceKey recognizedKey) {
        switch (recognizedKey) {
            case CONFIRM_PICK_UNITS:
                scenarioController.quitPicking();
                if (scenarioController.getProduct() != null) {
                    scenarioController.setState(ScanningShelfTask.INSTANCE);
                } else {
                    //TODO SEDI
                }
                break;
            default:
                break;
        }
    }

    private String getText(ScenarioController scenarioController, int stringId, String value) {
        String productText = scenarioController.getApplicationContext().getString(stringId);
        return productText.replace("#1", value);
    }

    public void showPickUnits(ScenarioController scenarioController){                                                    //SEDI
        String productText = getText(scenarioController, R.string.pick_units, scenarioController.getProduct().getPUnits());
        scenarioController.userFeedback(productText, null, null);
    }
}

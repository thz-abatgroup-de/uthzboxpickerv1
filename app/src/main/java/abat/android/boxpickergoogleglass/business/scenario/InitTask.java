package abat.android.boxpickergoogleglass.business.scenario;

import abat.android.boxpickergoogleglass.voicerecognition.VoiceKey;

/**
 * Created by DAJ on 27.01.2016.
 */
public class InitTask implements ScenarioState {
    protected static final ScenarioState INSTANCE = new InitTask();
    private InitTask(){}

    @Override
    public void handleStateChanged(ScenarioController scenarioController){
        //do nothing
    }

    @Override
    public void handleBarcodeScanned(ScenarioController scenarioController, String barcode) {
        //do nothing
    }

    @Override
    public void handleInitRequest(ScenarioController scenarioController) {
        scenarioController.initializeController();
    }

    @Override
    public void handleVoiceKeyRecognized(ScenarioController scenarioController, VoiceKey recognizedKey) {
      //do nothing
    }
}

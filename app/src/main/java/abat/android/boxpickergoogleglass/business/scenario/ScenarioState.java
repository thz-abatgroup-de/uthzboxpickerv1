package abat.android.boxpickergoogleglass.business.scenario;

import abat.android.boxpickergoogleglass.business.util.TypeOfData;
import abat.android.boxpickergoogleglass.voicerecognition.VoiceKey;

/**
 * Created by DAJ on 27.01.2016.
 */
public interface ScenarioState {
    /**
     * has to be invoked after state change
     */
    public void handleStateChanged(ScenarioController scenarioController);

    /**
     * get invoked if barcode was scanned
     */
    public void handleBarcodeScanned(ScenarioController scenarioController, String barcode);

    /**
     * get invoked if user wants to exit current taskPostExecute
     */
    public void handleInitRequest(ScenarioController scenarioController);

    /**
     * get invoked if user expresses voice command
     */
    public void handleVoiceKeyRecognized(final ScenarioController scenarioController, VoiceKey recognizedKey);
}

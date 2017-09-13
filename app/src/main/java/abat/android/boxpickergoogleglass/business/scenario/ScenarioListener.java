package abat.android.boxpickergoogleglass.business.scenario;

/**
 * Created by sedi on 09.02.2017.
 */

public interface ScenarioListener {
    public void showPick(int indexOfShelf, int indexOfProduct);
    public void showStartScreen();
    public void onStateChange(int stateIcon);
    /**
     * Gets invoked if the user should scan a barcode
     */
    public void startBarcodeScanning();
    /**
     * Gets invoked if scenario reaches state that needs interaction with the user
     * @param message
     */
    public void onUserMessage(String message);
    /**
     * Gets invoked if scenario reaches state that needs interaction with the user
     * @param message
     */
    public void onUserMessage(String message, int iconId);
}

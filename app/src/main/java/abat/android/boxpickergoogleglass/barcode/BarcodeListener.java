package abat.android.boxpickergoogleglass.barcode;

import abat.android.boxpickergoogleglass.business.util.TypeOfData;

/**
 * Created by DAJ on 28.01.2016.
 */
public interface BarcodeListener {

    public String barcodeIdentifierPrefix = "BARCODE";

    /**
     * gets invoked if a barcode is received
     * @param barcode
     */
    public void onBarcodeReceived(String barcode);
    /**
     * gets invoked if a response to a OData request was received
     * @param response
     */
}

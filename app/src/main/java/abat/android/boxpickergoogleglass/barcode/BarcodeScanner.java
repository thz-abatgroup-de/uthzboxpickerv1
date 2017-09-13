package abat.android.boxpickergoogleglass.barcode;

import android.content.Context;
import android.content.Intent;

/**
 * Creates a barcode scan intent and receives scanned barcode
 * Created by DAJ on 30.06.2016.
 */
public class BarcodeScanner {
    public static final String BARCODE_SYMBOL = "##";
    public static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    public static final int ACTION_SCAN_REQUEST = 1;

    private BarcodeListener barcodeListener;
    private Context context;

    /**
     * Constructor
     * @param context
     * @param barcodeListener
     */
    public BarcodeScanner(Context context, BarcodeListener barcodeListener){
        this.context = context.getApplicationContext();
        this.barcodeListener = barcodeListener;
    }

    /**
     * Returns the scan intent
     * @return
     */
    public Intent startScanning(){
        return new Intent(ACTION_SCAN);
    }

    /**
     * Handles received barcode
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    public void scanAnswerReceived(int requestCode, int resultCode, Intent intent) {
        String scanResult = intent.getStringExtra("SCAN_RESULT");
        if(scanResult.startsWith(BARCODE_SYMBOL)){
            scanResult = scanResult.substring(0 + BARCODE_SYMBOL.length(), scanResult.length());
        }

        if(scanResult.endsWith(BARCODE_SYMBOL)){
            scanResult = scanResult.substring(0, scanResult.length()-BARCODE_SYMBOL.length());
        }

        barcodeListener.onBarcodeReceived(scanResult);
    }
}

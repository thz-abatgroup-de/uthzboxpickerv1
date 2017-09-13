package abat.android.boxpickergoogleglass.voicerecognition;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DAJ on 07.07.2016.
 */
public class VoiceRecognitionHandler implements RecognitionListener {
    private List<KeyRecognizeListener> mKeyRecognitionListener;
    private SpeechRecognizer mSpeechRecognizer;
    private Context context;

    /**
     * Creates a speech recognizer
     * @param context
     */
    public VoiceRecognitionHandler(Context context, List<KeyRecognizeListener> mKeyRecognitionListener){
        this.mKeyRecognitionListener = mKeyRecognitionListener;
        this.context = context;

        //create speech recognizer
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        mSpeechRecognizer.setRecognitionListener(this);
    }

    //What is the result of SpeechRecognizer.isRecognitionAvailable()? What recognition providers are listed on the device under the Android Voice Search Settings?
    /**
     * Starts listening
     */
    public void startListening(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.getPackageName());

        mSpeechRecognizer.startListening(intent);
        Log.v(Context.AUDIO_SERVICE,"start listening");
    }

    /**
     * Stops the listening
     */
    public void stopListening(){
        mSpeechRecognizer.stopListening();
        Log.v(Context.AUDIO_SERVICE,"stop listening");
    }

    /**
     * Vuzix M100 v1.0.8 only can support 'onPartialResults'
     * Supported keywords:
     *      move left/right/up/down
     *      go left/right/up/down
     *      left/right/up/down
     *      next, previous, forward, back
     *      select, cancel
     *      complete, stop, exit, go home
     *      menu, volume up/down
     *      mute, call, dial, hang up, answer
     *      ignore, end, redial, call back
     *      contacts, favorites, pair, unpair
     *      sleep, shut down
     *      set clock/time
     *      cut, copy, paste, delete
     *      0, 1, 2, 3, 4, 5, 6, 7, 8, 9
     * @param partialResults
     */
    @Override
    public void onPartialResults(Bundle partialResults) {
        ArrayList<String> recData = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String receivedData = new String();
        VoiceKey recognizedKey = null;

        // Show filtered keyword
        for (String partOfReceivedData: recData) {
            //build full raw result
            receivedData += partOfReceivedData + ",";

            //loop over known commands and check if one matches
            for (VoiceKey voiceKey : VoiceKey.values()){
                if (partOfReceivedData.equals(voiceKey.getKeyAsCommand())) {
                    recognizedKey = voiceKey;
                    //notify listener about recognized key
                    for (KeyRecognizeListener listener :mKeyRecognitionListener) {
                        listener.keyRecognized(recognizedKey);
                    }
                    break;
                }
            }
        }

        // Raw Result
        Log.v(Context.AUDIO_SERVICE,"Result: " + receivedData);
        if(recognizedKey != null){
            Toast.makeText(context, "Recognized: " + recognizedKey.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.v(Context.AUDIO_SERVICE,"Please speak command.");
        Toast.makeText(context, "Please speak command.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.v(Context.AUDIO_SERVICE,"Voice Recognition Ready.");
        Toast.makeText(context, "Voice Recognition Ready.", Toast.LENGTH_SHORT).show();
        //iv.setImageResource(R.drawable.ready);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.v(Context.AUDIO_SERVICE,"recieved");
    }

    @Override
    public void onResults(Bundle results) {
        // Vuzix M100 v1.0.8 only can support 'onPartialResults'
        Log.v(Context.AUDIO_SERVICE,"onResults called");
    }
    @Override
    public void onError(int error) {Log.v(Context.AUDIO_SERVICE,"Error: " + error);}

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.v(Context.AUDIO_SERVICE,"onEvent called");
    }
    @Override
    public void onRmsChanged(float rmsdB) {
        Log.v(Context.AUDIO_SERVICE,"recieve : " + rmsdB + "dB");
    }

    @Override
    public void onEndOfSpeech() {
        Log.v(Context.AUDIO_SERVICE,"finished.");
    }
}

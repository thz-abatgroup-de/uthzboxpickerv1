package abat.android.boxpickergoogleglass.voicerecognition;

/**
 * Created by DAJ on 07.07.2016.
 */
public enum VoiceKey {

    /**
     * Barcode should be scanned
     */
    BARCODE_SCAN("select"),
    /**
     * Scanning barcode should be canceled
     */
    CANCEL_BARCODE_SCAN("cancel"),
    /**
     * Listening should be finished
     */
    FINISHED("end"),

    //confirm pick units to finish position
    CONFIRM_PICK_UNITS("next");

    /**
     * Corresponding command of VoiceKey
     * TODO maybe this should be a list of commands
     */
    private String command;

    /**
     * Instanziates the Enum with an additonal attribut
     * @param command
     */
    private  VoiceKey(String command){
        this.command = command;
    }

    /**
     * Returns text of enum
     * @return text
     */
    public String getKeyAsCommand() {
        return command;
    }
}


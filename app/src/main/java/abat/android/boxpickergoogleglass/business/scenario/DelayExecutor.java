package abat.android.boxpickergoogleglass.business.scenario;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by DAJ on 04.02.2016.
 */
public abstract class DelayExecutor extends AsyncTask<Void, Void, Void> {
    private final int delay;

    protected DelayExecutor(int delay) {
        this.delay = delay;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        taskInBackground();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        taskPostExecute();
    }

    public abstract void taskPostExecute();

    public void taskInBackground(){}
}

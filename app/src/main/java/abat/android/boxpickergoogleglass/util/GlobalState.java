package abat.android.boxpickergoogleglass.util;

import android.app.Activity;
import android.app.Application;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by DAJ on 01.07.2016.
 */
public class GlobalState extends Application {

    private Activity activeActivity;

    public Activity getCurrentActivity(){
        return activeActivity;
    }

    public void setCurrentActivity(Activity activity){
        activeActivity = activity;
    }

    @Override
    public void onCreate (){
        super.onCreate();
        registerActivityLifecycleCallbacks(new MyActivityLifecycleCallbacks());
    }

    @Override
    public void onTerminate (){
        super.onTerminate();
    }

    @Override
    public void onConfigurationChanged (Configuration newConfig){
        super.onConfigurationChanged(newConfig);
    }

    private final class MyActivityLifecycleCallbacks implements ActivityLifecycleCallbacks {

        public void onActivityCreated(Activity activity, Bundle bundle) {
            GlobalState.this.setCurrentActivity(activity);
            Log.e("","onActivityCreated:" + activity.getLocalClassName());
        }

        public void onActivityStarted(Activity activity) {
            GlobalState.this.setCurrentActivity(activity);
            Log.e("","onActivityStarted:" + activity.getLocalClassName());
        }

        public void onActivityResumed(Activity activity) {
            GlobalState.this.setCurrentActivity(activity);
            Log.e("","onActivityResumed:" + activity.getLocalClassName());
        }

        public void onActivityPaused(Activity activity) {
            Log.e("","onActivityPaused:" + activity.getLocalClassName());
        }

        public void onActivityStopped(Activity activity) {
            Log.e("","onActivityStopped:" + activity.getLocalClassName());
        }

        public void onActivityDestroyed(Activity activity) {
            Log.e("","onActivityDestroyed:" + activity.getLocalClassName());
        }

        public void onActivitySaveInstanceState(Activity activity,
                                                Bundle outState) {
            Log.e("", "onActivitySaveInstanceState:" + activity.getLocalClassName());
        }
    }
}

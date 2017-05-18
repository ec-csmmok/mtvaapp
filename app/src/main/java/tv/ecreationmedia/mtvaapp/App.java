package tv.ecreationmedia.mtvaapp;

import android.app.Application;
import android.content.Context;

/**
 * Created by cmok on 18/5/2017.
 */

public class App extends Application {
    public static Context context;

    @Override public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}

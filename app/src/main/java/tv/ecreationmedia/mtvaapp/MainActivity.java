package tv.ecreationmedia.mtvaapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


/**
 * Created by cmok on 13/4/2017.
 */

public class MainActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        final Context context = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Intent intent = new Intent(context, WebViewActivity.class);
        startActivity(intent);

    }

}

package tv.ecreationmedia.mtvaapp;

import java.io.*;
import java.util.*;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.view.KeyEvent;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.*;

/**
 * Created by cmok on 16/4/2017.
 */

public class WebViewActivity extends Activity {
    public static final String tenantFile = "launch_urls";
    public static final String current = "current";
    public static final String tenantList ="http://px2.ecreationmedia.tv/content/launch_urls";
    //public static final String tenantList ="http://192.168.2.1:8080/launch_urls";
    private String[] defaultTenants = {"Arris#http://px2.ecreationmedia.tv/?brand=default&model=webkit&tenant=arris",
            "TalkTalk#http://px2.ecreationmedia.tv/?brand=default&model=webkit&tenant=talktalk"};

    private WebView webView;
    private final int[] code = {KeyEvent.KEYCODE_BACK, KeyEvent.KEYCODE_BACK, KeyEvent.KEYCODE_BACK, KeyEvent.KEYCODE_BACK, KeyEvent.KEYCODE_BACK,
            KeyEvent.KEYCODE_BACK, KeyEvent.KEYCODE_BACK, KeyEvent.KEYCODE_BACK, KeyEvent.KEYCODE_BACK, KeyEvent.KEYCODE_BACK};
    private List<Integer> history = new ArrayList<>();
    private List<String> tenantNames = new ArrayList<>();
    private List<String> urls = new ArrayList<>();
    private String launchUrl = "";
    //private List<Pair<String, String>> tenant = new ArrayList<>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new DownloadFileAsync().execute(tenantList);
        this.populateLaunchUrl();

        setContentView(R.layout.webview);

        webView = (WebView) findViewById(R.id.webView1);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public Bitmap getDefaultVideoPoster() {
                return Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setInitialScale(1);
        //webView.loadUrl("http://192.168.2.1:8000/js_keycode.html");
        //webView.loadUrl("http://192.168.2.1:8000/html5.html");
        Log.d("MTVA", "Launch url: " + launchUrl);
        webView.loadUrl(launchUrl);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            this.detectKeyCombo(keyCode);
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        webView.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                        webView.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
                        //finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    private void updateCurrent(String url) {
        try {
            File dir = App.context.getDir("", Context.MODE_PRIVATE);
            File currentFile = new File(dir, current);
            OutputStream output = new FileOutputStream(currentFile);
            output.write(url.getBytes());
        } catch(Exception e) {
            Log.d("MTVA", e.toString());
        }
    }
    private void populateLaunchUrl() {
        try {
            File filesDir = getDir("", Context.MODE_PRIVATE);
            Scanner input = new Scanner(new File(filesDir, current));
            launchUrl = input.next();
        } catch(Exception e) {
            populateTenants();
            launchUrl = urls.toArray()[0].toString();
            updateCurrent(launchUrl);
        }
    }

    private void populateTenants() {
        tenantNames.clear();
        urls.clear();
        try {
            File filesDir = getDir("", Context.MODE_PRIVATE);
            Scanner input = new Scanner(new File(filesDir, tenantFile)).useDelimiter("[\n]");
            while(input.hasNext()) {
                String line = input.next();
                String[] parts = line.split("#");
                tenantNames.add(parts[0]);
                urls.add(parts[1]);
            }
        } catch(Exception e) {
            for(int i = 0; i < defaultTenants.length; i++) {
                String[] parts = defaultTenants[i].split("#");
                tenantNames.add(parts[0]);
                urls.add(parts[1]);
            }
        }

        for(int i = 0; i < tenantNames.size(); i++){
            Log.d("MTVA", tenantNames.get(i) + " " + urls.get(i));
        }
    }

    private void detectKeyCombo(int keyCode) {
        this.history.add(keyCode);
        if(this.history.size() == this.code.length + 1)
            this.history.remove(0);
        if (this.history.size() == this.code.length) {
            Object[] objArr = this.history.toArray();
            Integer[] intArray = Arrays.copyOf(objArr, objArr.length, Integer[].class);
            int[] historyArray = new int[this.code.length];
            for (int i = 0; i < this.code.length; i++) {
                historyArray[i] = intArray[i];
            }
            if (Arrays.equals(historyArray, this.code)) {
                this.history.clear();
                this.populateTenants();
                this.drawPopup();
            }
        }
    }

    private void drawPopup() {
        CharSequence tenants[] = new CharSequence[tenantNames.size()];
        tenants = this.tenantNames.toArray(tenants);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select new launch tenant:");
        builder.setItems(tenants, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateCurrent(urls.toArray()[which].toString());
                finishAndRemoveTask();
                System.exit(0);
            }
        });
        builder.show();
    }
}

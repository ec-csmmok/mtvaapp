package tv.ecreationmedia.mtvaapp;

import java.io.*;
import java.net.*;
import android.os.AsyncTask;
import android.content.Context;
import android.util.*;

/**
 * Created by cmok on 18/5/2017.
 */

public class DownloadFileAsync extends AsyncTask<String, String, String> {
    @Override
    protected String doInBackground(String... aurl) {
        int count;

        try {
            URL url = new URL(aurl[0]);
            URLConnection conexion = url.openConnection();
            conexion.connect();

            int lenghtOfFile = conexion.getContentLength();
            Log.d("MTVA", "Lenght of file: " + lenghtOfFile);

            InputStream input = new BufferedInputStream(url.openStream());
            File dir = App.context.getDir("", Context.MODE_PRIVATE);
            File launchUrls = new File(dir, WebViewActivity.tenantFile);
            OutputStream output = new FileOutputStream(launchUrls);

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                publishProgress(""+(int)((total*100)/lenghtOfFile));
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
            Log.d("MTVA", "Download failed, error: " + e.toString());
        }
        return null;

    }
}

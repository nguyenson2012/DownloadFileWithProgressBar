package com.nguyenthanhson.downloadimagewithprogressbar;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    // button to show progress dialog
    Button btnShowProgress;
    EditText mLink;
    VideoView mVideoView;
    int random;

    // Progress Dialog
    private ProgressDialog pDialog;
    ImageView my_image;
    // Progress dialog type (0 - for Horizontal progress bar)
    public static final int progress_bar_type = 0;

    // File url to download
    private static String file_url = "http://www.v3.co.uk/IMG/542/225542/fibre-broadband-image-540x334.jpg?1443447935";
    private static String file_url1 = "https://wordpress.org/plugins/about/readme.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // show progress bar button
        btnShowProgress = (Button) findViewById(R.id.btnProgressBar);
        mLink=(EditText)findViewById(R.id.imgLink);
        mVideoView=(VideoView)findViewById(R.id.videoView);
        // Image view to show image after downloading
        my_image = (ImageView) findViewById(R.id.my_image);
        /**
         * Show Progress bar click event
         * */
        Random r=new Random();
        random=r.nextInt(100);
        btnShowProgress.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // starting new Async Task
                new DownloadFileFromURL().execute("https://redirector.googlevideo.com/videoplayback?signature=89769FF3229CCDB1A591A32FBC75EA0901DD5282.7DCA54ACF7C1F8FE2890CCCFFB92B594306BF12C&mime=video/mp4&key=yt6&fexp=9405185,9412914,9416126,9418750,9420095,9420311,9420452,9422540,9422596,9423282,9423662,9423845,9424115,9424372,9424416,9424631,9425141,9425619&nh=IgpwcjAzLmRmdzA2KgkxMjcuMC4wLjE&lmt=1428142342165364&initcwndbps=76250&upn=TBiD2pXrGyg&source=youtube&id=o-AA_JYx_01xxK-Och1ws8WwzmUm8qJvJBgTO3RN4Yq9d5&pl=22&itag=18&mm=31&mn=sn-q4f7snez&ip=23.91.70.73&requiressl=yes&ms=au&mt=1450624357&sparams=dur,id,initcwndbps,ip,ipbits,itag,lmt,mime,mm,mn,ms,mv,nh,pl,ratebypass,requiressl,source,upn,expire&mv=m&sver=3&ipbits=0&dur=101.006&ratebypass=yes&expire=1450646134&title=Improve%20Your%20Pronunciation%20with%20BBC%20Learning%20English%20-%20[www.getlinkyoutube.com]");
            }
        });
    }
    /**
     * Showing Dialog
     * */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }
    /**
     * Background Async Task to download file
     * */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {
        File mainfile;

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                File direct = new File(Environment.getExternalStorageDirectory() + "/MyFolder");

                if (!direct.exists()) {
                    File wallpaperDirectory = new File("/sdcard/MyFolder/");
                    wallpaperDirectory.mkdirs();
                }

                mainfile = new File(new File("/sdcard/MyFolder/"), "sound.mp4");
                if (mainfile.exists()) {
                    mainfile.delete();
                }
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // this will be useful so that you can show a tipical 0-100% progress bar
                int lenghtOfFile = conection.getContentLength();
                // download the file
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                // Output stream
                OutputStream output = new FileOutputStream(mainfile);
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress(""+(int)((total*100)/lenghtOfFile));
                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            dismissDialog(progress_bar_type);

            // Displaying downloaded image into image view
            // Reading image path from sdcard
//            String imagePath = Environment.getExternalStorageDirectory().toString() + "/downloadedfile.jpg";
//            // setting downloaded into image view
//            my_image.setImageDrawable(Drawable.createFromPath(imagePath));
            //Creating MediaController
            MediaController mediaController= new MediaController(MainActivity.this);
            mediaController.setAnchorView(mVideoView);

            //specify the location of media file
            Uri uri=Uri.parse(Environment.getExternalStorageDirectory().toString()+"/MyFolder/sound.mp4");

            //Setting MediaController and URI, then starting the videoView
            mVideoView.setMediaController(mediaController);
            mVideoView.setVideoURI(uri);
            mVideoView.requestFocus();
            mVideoView.start();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

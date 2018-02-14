package com.videodownloader.fb.service;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.videodownloader.fb.MainActivity;
import com.videodownloader.fb.R;
import com.videodownloader.fb.Webview2Activity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by android2 on 18/4/17.
 */

public class DownloadService extends Service {


    private int result = Activity.RESULT_CANCELED;
    public static final String URL = "urlpath";
    public static final String FILENAME = "filename";
    public static final String FILEPATH = "filepath";
    public static final String RESULT = "result";
    public static final String NOTIFICATION = "service receiver";
    private boolean running;

    int progress = 0;
    Notification notification;
    NotificationManager notificationManager;
    int id = 10;
    SharedPreferences sharedpreferences;

   /* public DownloadService() {
        super("DownloadService");
    }*/

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        //  sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        if(intent != null && intent.getStringExtra(URL) != null) {

            SharedPreferences.Editor editor = getBaseContext().getSharedPreferences("Filename", MODE_PRIVATE).edit();
            editor.putString("path", intent.getStringExtra(URL));
            editor.putString("name", intent.getStringExtra(FILENAME));
            editor.commit();

            SharedPreferences pref = getBaseContext().getSharedPreferences("Filename", MODE_PRIVATE);
            if(pref != null){
                String urlPath = pref.getString("path", null);
                String fileName = pref.getString("name", null);
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                // new DownloadFileAsync2(fileName).execute(urlPath);
                new DownloadFileAsync(fileName, timeStamp).execute(urlPath);
            }
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class DownloadFileAsync extends AsyncTask<String, String, String> {


        String filename;
        String timeStamp;

        public DownloadFileAsync(String filename, String timeStamp) {
            this.filename = filename;
            this.timeStamp = timeStamp;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showDialog(DIALOG_DOWNLOAD_PROGRESS);
        }

        @Override
        protected String doInBackground(String... aurl) {
            int count;

            try {

                URL url = new URL(aurl[0]);
                URLConnection conexion = url.openConnection();
                conexion.connect();
                float file_size = conexion.getContentLength();
                if(filename == null)
                    filename = String.valueOf(R.string.app_name);
                addNotification(getApplicationContext(),filename, "downloading...", file_size);

                int lenghtOfFile = conexion.getContentLength();
                Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);
                String date = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                InputStream input = new BufferedInputStream(url.openStream());
                File folder = new File(Environment.getExternalStorageDirectory() +
                        File.separator + "FTD Video");
                boolean success = true;
                if (!folder.exists()) {
                    success = folder.mkdirs();
                }
                OutputStream output;
                if (success) {
                    output = new FileOutputStream("/sdcard/FTD Video/" + "FT_" + date + ".mp4");
                } else {
                    output = new FileOutputStream("/sdcard/" + "FT_" + date + ".mp4");
                }


                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                e.printStackTrace();
                if(filename == null)
                    filename = String.valueOf(R.string.app_name);
                failedNotification(getApplicationContext(),filename, "failed to download try again");

            }
            return null;

        }

        @Override
        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC", progress[0]);

          /* try {
                Intent intent = new Intent();
                final PendingIntent pendingIntent = PendingIntent.getActivity(
                        getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                notification = new Notification(android.R.drawable.stat_sys_download,
                        "Dowloading", System.currentTimeMillis());
                notification.flags = notification.flags
                        | Notification.FLAG_ONGOING_EVENT;
                notification.contentView = new RemoteViews(getApplicationContext()
                        .getPackageName(), R.layout.layout_item);
                notification.contentIntent = pendingIntent;
                notification.contentView.setImageViewResource(R.id.iv_icon,
                        android.R.drawable.stat_sys_download);
                notification.contentView.setTextViewText(R.id.tv_title,
                        "Dowloading...");
                notification.contentView.setProgressBar(R.id.progressBar1, 100,
                        Integer.parseInt(progress[0]), false);
                getApplicationContext();
                notificationManager = (NotificationManager) getApplicationContext()
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(id, notification);
            } catch (Exception e) {
                e.printStackTrace();
            }
*/
            //  mProgressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String unused) {
            // dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
            //notification.flags = notification.flags| Notification.FLAG_AUTO_CANCEL;
            addNotification(getApplicationContext(), filename, "download completed", 0);
            //Toast.makeText(getApplicationContext(), "Completed", Toast.LENGTH_LONG).show();
            if (unused != null) {
                Intent intent = new Intent(NOTIFICATION);
                intent.putExtra(FILEPATH, "  ");
                intent.putExtra(RESULT, result);
                sendBroadcast(intent);
            } else {

            }
        }
    }

    private void addNotification(Context context,String filename, String status, float file_size) {
        Bitmap largicon = BitmapFactory.decodeResource(context.getResources(),
                R.mipmap.ic_launcher);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        //.setSmallIcon(android.R.drawable.stat_sys_download)
                        .setLargeIcon(largicon)
                        .setAutoCancel(true)
                        .setContentTitle(filename);
        if (status.equalsIgnoreCase("downloading..."))
            builder.setContentText(status + " (" + file_size / 1000000 + "Mb)");
        else
            builder.setContentText(status);
        if (status.equalsIgnoreCase("downloading..."))
            builder.setAutoCancel(false);
        else
            builder.setAutoCancel(true);
        if (status.equalsIgnoreCase("downloading..."))
            builder.setSmallIcon(android.R.drawable.stat_sys_download);
        else
            builder.setSmallIcon(android.R.drawable.stat_sys_download_done);

        Intent notificationIntent = new Intent(context, Webview2Activity.class);
        if (status.equalsIgnoreCase("downloading..."))
            notificationIntent.setFlags(Notification.FLAG_ONGOING_EVENT);
        else
            notificationIntent.setFlags(Notification.DEFAULT_ALL);
        builder.setOngoing(running);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(context,  (int) System.currentTimeMillis(), notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(running ? android.R.drawable.stat_sys_download
                        : android.R.drawable.stat_sys_download_done,
                running ? "pause"
                        : "start",
                contentIntent);
        builder.setContentIntent(contentIntent);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(0, builder.build());
        }
    }

    private void completedNotification(Context context, String status) {
        Bitmap largicon = BitmapFactory.decodeResource(context.getResources(),
                R.mipmap.ic_launcher);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(android.R.drawable.stat_sys_download_done)
                        .setLargeIcon(largicon)
                        .setAutoCancel(true)
                        .setContentTitle("FTD Video downloader")
                        .setContentText(status);

        Intent notificationIntent = new Intent(context, Webview2Activity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(0, builder.build());
        }
    }

    private void failedNotification(Context context,String filename, String status) {
        Bitmap largicon = BitmapFactory.decodeResource(context.getResources(),
                R.mipmap.ic_launcher);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(android.R.drawable.ic_delete)
                        .setLargeIcon(largicon)
                        .setAutoCancel(true)
                        .setContentTitle(filename)
                        .setContentText(status);

        Intent notificationIntent = new Intent(context, Webview2Activity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(0, builder.build());
        }
    }


}
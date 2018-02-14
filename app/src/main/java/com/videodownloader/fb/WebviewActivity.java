package com.videodownloader.fb;

import android.*;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnErrorListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import com.videodownloader.fb.fragment.BrowserFragment;
import com.videodownloader.fb.fragment.HomeFragment;
import com.videodownloader.fb.service.DownloadService;
import com.videodownloader.fb.service.UtilService;

import org.jsoup.Jsoup;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class
WebviewActivity extends AppCompatActivity {

    public static FloatingActionButton fab, fab_y;

    public static final String ROOT_PLAY_STORE_DEVICE = "market://details?id=";
    String currentVersion;

    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private int savedPosition;

    ArrayList<String> videoURl = new ArrayList<String>();
    ArrayList<String> videoID = new ArrayList<String>();
    String linkUrl;
    String linkName;
    Notification notification;
    NotificationManager notificationManager;
    int id = 10;
    private int result = Activity.RESULT_CANCELED;
    RelativeLayout frameLayout;
    public static boolean doubleBackToExitPressedOnce = false;

    InterstitialAd mInterstitialAd;
    public static WebviewActivity activity;
    public static ActionBar ab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ab = getSupportActionBar();
        frameLayout = (RelativeLayout) findViewById(R.id.rv_layout);

        activity = this;

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = new HomeFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();

        try {
            currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (currentVersion != null)
            new GetVersionCode().execute();

        permission();

        deleteCache(WebviewActivity.this);
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (BrowserFragment.back) {
                        if (BrowserFragment.wv1.canGoBack()) {
                            BrowserFragment.nextvideo = true;
                            BrowserFragment.wv1.goBack();
                        } else {
                            super.onBackPressed();
                        }
                        return true;
                    } else {
                        if (doubleBackToExitPressedOnce) {
                            finish();
                        }
                        doubleBackToExitPressedOnce = true;
                        Snackbar.make(frameLayout, "Press back again to Exit", Snackbar.LENGTH_LONG).show();
                        //Toast.makeText(getApplicationContext(), "Press again to exit", Toast.LENGTH_SHORT).show();

                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                doubleBackToExitPressedOnce = false;
                            }
                        }, 2000);

                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    private class GetVersionCode extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... voids) {

            String newVersion = null;
            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" +
                        WebviewActivity.this.getPackageName() + "&hl=it")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div[itemprop=softwareVersion]")
                        .first()
                        .ownText();
                return newVersion;
            } catch (Exception e) {
                return newVersion;
            }
        }

        @Override
        protected void onPostExecute(String onlineVersion) {
            super.onPostExecute(onlineVersion);
            if (onlineVersion != null && !onlineVersion.isEmpty()) {
                if (Float.valueOf(currentVersion) < Float.valueOf(onlineVersion)) {
                    //show dialog
                    new android.support.v7.app.AlertDialog.Builder(WebviewActivity.this)
                            .setTitle("New Version")
                            .setIcon(R.mipmap.ic_launcher)
                            .setCancelable(false)
                            .setMessage("New version available, are you want to update?")
                            .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    goToMarket(WebviewActivity.this);
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                } else {
                    // Toast.makeText(getApplicationContext(), "0", Toast.LENGTH_LONG).show();
                }
            } else {

            }
        }

    }

    private static void goToMarket(Context mContext) {
        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                .parse(ROOT_PLAY_STORE_DEVICE
                        + mContext.getPackageName())));

    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(getApplicationContext(),
                    "You are not access this App without Storage permission",
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void permission() {
        if (Build.VERSION.SDK_INT >= 23) {
            ArrayList<String> listPermissionsNeeded = new ArrayList<String>();
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                listPermissionsNeeded
                        .add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            if (!listPermissionsNeeded.isEmpty())
                ActivityCompat.requestPermissions(this, listPermissionsNeeded
                        .toArray(new String[listPermissionsNeeded.size()]), 1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();
        if (id == android.R.id.home) {
           // finish();
        }
        switch (id) {
            case R.id.homscreen:
                if (BrowserFragment.back) {
                    Fragment fragment = new HomeFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame, fragment);
                    fragmentTransaction.commit();
                }
                break;
            case R.id.rateus:
                goToMarket(WebviewActivity.this);
                break;
            case R.id.downloads:
                startActivity(new Intent(getBaseContext(), Webview2Activity.class));
                break;

            case R.id.search:
                AlertDialog.Builder alert = new AlertDialog.Builder(WebviewActivity.this);
                alert.setTitle("Type your url"); //Set Alert dialog title here
                // alert.setMessage("Type your url"); //Message here

                final EditText input = new EditText(WebviewActivity.this);
                alert.setView(input);
                input.setHint("www.example.com");

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        String srt = input.getEditableText().toString();
                        if (!srt.trim().isEmpty()) {
                            Fragment fragment = new BrowserFragment();
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            Bundle bundle = new Bundle();
                            bundle.putString("url", srt);
                            fragment.setArguments(bundle);
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.frame, fragment);
                            fragmentTransaction.commit();
                        } else {
                            Snackbar.make(frameLayout, "Enter url", Snackbar.LENGTH_LONG).show();
                        }
                    } // End of onClick(DialogInterface dialog, int whichButton)
                });
                alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                        dialog.cancel();
                    }
                }); //End of alert.setNegativeButton
                AlertDialog alertDialog = alert.create();
                alertDialog.show();
                break;

            case R.id.r_reload:
                if (BrowserFragment.back)
                    BrowserFragment.wv1.reload();
                break;

        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}

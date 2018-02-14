package com.videodownloader.fb.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ObservableWebView;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.videodownloader.fb.R;
import com.videodownloader.fb.Webview2Activity;
import com.videodownloader.fb.WebviewActivity;
import com.videodownloader.fb.service.DownloadService;
import com.videodownloader.fb.service.UtilService;


public class BrowserFragment extends Fragment  implements ObservableScrollViewCallbacks {

    public static boolean back;
    public static WebView wv1;
    ProgressBar progressBar;
    public static boolean nextvideo = false;
    FloatingActionButton fab;
    String linkUrl;
    String linkName;
    String url = "www.google.com";
    private ObservableWebView webView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_browser, container, false);

        Bundle bundle = getArguments();
        if (bundle != null)
            url = bundle.getString("url");
        wv1 = (WebView) view.findViewById(R.id.webView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.VISIBLE);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        WebSettings webSettings = wv1.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wv1.setWebViewClient(new MyBrowser());
        wv1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        wv1.getSettings().setSupportMultipleWindows(true);

        wv1.getSettings().setLoadsImagesAutomatically(true);
        wv1.getSettings().setJavaScriptEnabled(true);
        wv1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        wv1.getSettings().setLoadWithOverviewMode(true);
        if(url.contains("http://")){
            wv1.loadUrl(url);
        } else {
            wv1.loadUrl("http://" + url);
        }

        wv1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        if (!v.hasFocus()) {
                            v.requestFocus();
                        }
                        break;
                }
                return false;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (new UtilService().isNetworkAvailable(getActivity())) {
                    if (linkUrl != null) {
                        Intent intent = new Intent(WebviewActivity.activity, DownloadService.class);
                        intent.putExtra(DownloadService.FILENAME, linkName);
                        intent.putExtra(DownloadService.URL,
                                linkUrl);
                        getActivity().startService(intent);
                        Toast.makeText(getActivity(), "Downloading...", Toast.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(v, "No download option/ try later", Snackbar.LENGTH_LONG)
                                .setAction("Dismiss", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                    }
                                }).show();
                    }
                } else {
                    Snackbar.make(v, "No internet connection", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        webView = (ObservableWebView) view.findViewById(R.id.web);
        webView.setScrollViewCallbacks(this);
        webView.setWebViewClient(new MyBrowser());
        webView.getSettings().setJavaScriptEnabled(true); // enable javascript
        webView.loadUrl("http://" + url);


        return view;
    }
    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

        if (WebviewActivity.ab == null) {
            return;
        }
        if (scrollState == ScrollState.UP) {
            if (WebviewActivity.ab.isShowing()) {
                WebviewActivity.ab.hide();
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (!WebviewActivity.ab.isShowing()) {
                WebviewActivity.ab.show();
            }
        }
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            wv1 = view;
            view.loadUrl(url);
            progressBar.setVisibility(View.VISIBLE);
            return true;
        }

        public void onPageFinished(WebView view, String url) {
            Log.e("Finished loading", url);
            nextvideo = true;
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            nextvideo = true;
            if (nextvideo) {
                String[] separated = url.split("");

               if (url.contains(".mp4")) {
                    // final String url2 = "https://m.youtube.com/watch?v=" + url.substring(url.lastIndexOf("=") + 1);
                    //extaract(url.substring(url.lastIndexOf("=") + 1));
                    String name = url.replace(".mp4", "");
                    linkName = name.substring(name.lastIndexOf("/") + 1);
                    linkUrl = url;
                    fab.setVisibility(View.VISIBLE);
                }

                nextvideo = false;
            }
            super.onLoadResource(view, url);
        }

        @Override
        public void onPageCommitVisible(WebView view, String url) {

            super.onPageCommitVisible(view, url);
        }

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item=menu.findItem(R.id.homscreen);
        item.setVisible(true);
        MenuItem item2 = menu.findItem(R.id.r_reload);
        item2.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {

        super.onResume();
        back = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        back = false;
    }
}

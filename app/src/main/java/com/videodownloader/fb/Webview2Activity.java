package com.videodownloader.fb;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.videodownloader.fb.service.UtilService;

import java.io.File;
import java.util.ArrayList;


public class Webview2Activity extends AppCompatActivity {

    ArrayList<File> allTXT = new ArrayList<>();
    RecyclerView historylist;
    HistoryAdapter adapter;
    TextView empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview2);

        String path = Environment.getExternalStorageDirectory().toString()+"/FTD Video";
        searchTXT(new File(path));
       // searchTXT(new File(Environment.getExternalStorageDirectory().getPath()));

        historylist = (RecyclerView) findViewById(R.id.rv_historylist);
        empty = (TextView) findViewById(R.id.tv_empty);
        empty.setVisibility(View.GONE);

        if (allTXT != null && allTXT.size() > 0) {
            adapter = new HistoryAdapter(this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            historylist.setLayoutManager(mLayoutManager);
            historylist.setItemAnimator(new DefaultItemAnimator());
            historylist.setAdapter(adapter);
            empty.setVisibility(View.GONE);
        } else {
            empty.setVisibility(View.VISIBLE);
        }

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

    private void searchTXT(File dir) {
        File[] files = dir.listFiles();
        if(files != null) {
            for (File file : files) {
                if (file.isFile() && isTXT(file)) {
                    allTXT.add(file);
                    Log.i("TXT", file.getName());
                } else if (file.isDirectory()) {
                    searchTXT(file.getAbsoluteFile());
                }
            }
        } else {
           // Toast.makeText(getApplicationContext(), "File Not found", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isTXT(File file) {
        boolean is = false;
        if (file.getName().endsWith(".mp4")) {
            is = true;
            Log.e("", "mp4 files -->" + is);
        }
        return is;
    }

    public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

        final CharSequence[] items = {"Share", "Delete"};
        public HistoryAdapter(Activity activity) {

        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            View containerView;
            ImageButton delete, share;


            public MyViewHolder(View view) {
                super(view);
                containerView = view;
                title = (TextView) view.findViewById(R.id.tv_name);
                delete = (ImageButton) view.findViewById(R.id.iv_delete);
                share = (ImageButton) view.findViewById(R.id.iv_share);

            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.downloaded_items, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {

            if (allTXT.get(position) != null) {
                String name = allTXT.get(position).toString();
                holder.title.setText(name.substring(name.lastIndexOf("/") + 1));
            }
            holder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = allTXT.get(position).toString();
                 /*   Intent obj = new Intent(Webview2Activity.this, PlayerActivity.class);
                    obj.putExtra("file", allTXT.get(position).toString());
                    startActivity(obj);*/
                    Uri intentUri = Uri.parse(name);
                 String fpath = name.replace(name.substring(name.lastIndexOf("/") + 1), "");
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(intentUri, "video/mp4");
                    startActivity(intent);
                }
            });
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Webview2Activity.this);
                    // builder.setTitle("select");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            if(items[item].equals("Share")){
                                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                                Uri screenshotUri = Uri.parse(allTXT.get(position).toString());

                                sharingIntent.setType("video/mp4");
                                sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                                startActivity(Intent.createChooser(sharingIntent, "Share image using"));
                            }else if(items[item].equals("Delete")){
                                File file = new File(allTXT.get(position).toString());
                                file.delete();
                                allTXT.remove(position);
                                adapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });
        }


        @Override
        public int getItemCount() {
            return allTXT.size();
        }

    }
}
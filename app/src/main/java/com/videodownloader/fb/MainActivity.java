package com.videodownloader.fb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;




import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final int RECOVERY_DIALOG_REQUEST = 1;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

           // Toast.makeText(getApplicationContext(), files.toString(), Toast.LENGTH_LONG).show();



        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);



    }


    @Override
    public void onBackPressed() {
            super.onBackPressed();
    }
}

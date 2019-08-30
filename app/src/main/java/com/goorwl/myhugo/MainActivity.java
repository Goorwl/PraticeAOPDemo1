package com.goorwl.myhugo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.goorwl.hugo.DebugLog;
import com.goorwl.hugo.DoubleClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int sum = getSum(2, 3);
        Log.e(TAG, "onCreate: "+ sum);


        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            @DoubleClick
            public void onClick(View v) {
                Log.e(TAG, "onClick: 111");
            }
        });
        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            @DoubleClick
            public void onClick(View v) {
                Log.e(TAG, "onClick: 222");
            }
        });
    }

    @DebugLog
    public int getSum(int a, int b) {
        return a + b;
    }
}

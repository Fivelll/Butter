package com.example.lzl.butterknifelzl;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import butter.ButterKnife;
import butter.RunningTimeButterKnife;


public class MainActivity extends AppCompatActivity {

    @Bind(R.id.tv_1)
    TextView tv1;

    @Bind(R.id.lv)
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.binding(this);
        //RunningTimeButterKnife.binding(this);
        tv1.setText("123");
    }

    @Click(R.id.tv_1)
    void onClick(){
        tv1.setText("456");
    }
}

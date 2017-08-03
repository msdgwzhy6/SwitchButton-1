package com.switchbutton;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SwitchButton btn;
    private SwitchButton btn1;
    private SwitchButton btn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        btn = (SwitchButton) findViewById(R.id.btn);
        btn.setOnClickListener(this);
        btn1 = (SwitchButton) findViewById(R.id.btn1);
        btn1.setOnClickListener(this);
        btn2 = (SwitchButton) findViewById(R.id.btn2);
        btn2.setBackgroudColor(Color.BLACK);
        btn2.setAnimatorTime(2000);
        btn2.setPadding(8);
        btn2.setCloseColor(Color.RED);
        btn2.setOpenColor(Color.GREEN);
        btn2.setCircleColor(Color.YELLOW);
        btn2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn:
                btn.onclick();
                break;
            case R.id.btn1:
                btn1.onclick();
                break;
            case R.id.btn2:
                btn2.onclick();
                break;
        }
    }
}

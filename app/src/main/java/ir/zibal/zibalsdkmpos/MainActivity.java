package ir.zibal.zibalsdkmpos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


import ir.zibal.zibalsdk.IdToZibalServer;
import ir.zibal.zibalsdk.ZibalAPI;

public class MainActivity extends AppCompatActivity {

    Button btn_balance;
    Button btn_inject_keys;
    Button btn_config_zibal;
    Button btn_bluetooth;
    Button btn_payment;
    ZibalAPI zibalAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        zibalAPI = new ZibalAPI(this);
    }

    void initViews() {
        btn_balance = findViewById(R.id.btn_balance);
        btn_inject_keys = findViewById(R.id.btn_inject_keys);
        btn_config_zibal = findViewById(R.id.btn_config);
        btn_bluetooth = findViewById(R.id.btn_bluetooth);
        btn_payment = findViewById(R.id.btn_payment);

        btn_balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zibalAPI.getBalance();
            }
        });

        btn_inject_keys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zibalAPI.injectKeys();
            }
        });

        btn_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zibalAPI.navigateToBluetooth(MainActivity.this);
            }
        });

        btn_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IdToZibalServer(MainActivity.this,MainActivity.this).execute("17");

            }
        });

    }

}

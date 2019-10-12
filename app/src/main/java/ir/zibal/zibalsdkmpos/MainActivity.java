package ir.zibal.zibalsdkmpos;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import ir.zibal.zibalsdk.DeviceList;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_BT_DISCOVER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final DeviceList test = new DeviceList(this);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                test.getBalance();
            }
        });
        test.initDevice();

        Intent intent = new Intent(this,DeviceListActivity.class);
        startActivityForResult(intent, REQUEST_BT_DISCOVER);
    }



}

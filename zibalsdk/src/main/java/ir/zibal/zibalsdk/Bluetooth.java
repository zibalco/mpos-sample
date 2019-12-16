package ir.zibal.zibalsdk;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.tosantechno.mpos.pax.d180.Configuration;
import com.tosantechno.mpos.pax.d180.DeviceBasic;
import com.tosantechno.mpos.pax.d180.DeviceException;

import java.io.IOException;
import java.util.ArrayList;

public class Bluetooth extends AppCompatActivity {

    private final int REQUEST_BT_ENABLE = 1;
    private final int REQUEST_BT_DISCOVER = 2;
    private BluetoothAdapter btAdapter;
    private String tempBtMac;
    private ArrayList<BluetoothDevice> btScannedDevs;
    private Configuration cfgMgr;
    private DeviceBasic bsMngr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        initBluetooth();

        checkBluetooth();
    }

    private void initBluetooth(){
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        bsMngr = DeviceBasic.getInstance(this);
        btScannedDevs = new ArrayList<BluetoothDevice>();
        cfgMgr = Configuration.getInstance(this);
    }

    public void checkBluetooth() {

        Intent intent;
        if (!btAdapter.isEnabled()) {

            intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_BT_ENABLE);
        } else {

            intent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(intent, REQUEST_BT_DISCOVER);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("tag", "onActivityResult: "+requestCode);
        switch (requestCode) {
            case REQUEST_BT_ENABLE:
                if (resultCode == Activity.RESULT_OK) {
                    Intent intent = new Intent(Bluetooth.this, DeviceListActivity.class);
                    startActivityForResult(intent, REQUEST_BT_DISCOVER);
                }
                break;
            case REQUEST_BT_DISCOVER:    //bt scan

                if (resultCode == Activity.RESULT_OK) {
                    try {
                        btScannedDevs.clear();
                        String address = data.getExtras()
                                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                        tempBtMac = address;

                        ArrayList<String> devices = data.getExtras()
                                .getStringArrayList(DeviceListActivity.ALL_DEVICE_ADDRESS);


                        cfgMgr.commType = "bluetooth";
                        cfgMgr.bluetoothMac = tempBtMac;
                        cfgMgr.save();

                        beep();

                        blutoothStatus(true);
                        finish();
                        Toast.makeText(Bluetooth.this, "Connected", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                break;
        }
    }

    private void blutoothStatus(boolean b) {

        //todo implement this
//        injectKeyBtn.setEnabled(b);
    }

    /***/
    public void beep() {
        try {
            bsMngr.beep();
        } catch (DeviceException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

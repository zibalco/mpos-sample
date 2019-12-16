package ir.zibal.zibalsdk;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;


public class DeviceListActivity extends Activity {
    // Debugging
    private static final String TAG = "DeviceListActivity";
    private static final boolean D = true;

    // Return Intent extra
    public static String EXTRA_DEVICE_ADDRESS = "extra_device_address";
    public static String ALL_DEVICE_ADDRESS = "all_device_address";

    // Member fields
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
            setContentView(R.layout.device_list);

            setResult(Activity.RESULT_CANCELED);

            Button scanButton = (Button) findViewById(R.id.button_scan);
            mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
            mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);


            ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
            pairedListView.setAdapter(mPairedDevicesArrayAdapter);
            pairedListView.setOnItemClickListener(mDeviceClickListener);

            ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
            newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
            newDevicesListView.setOnItemClickListener(mDeviceClickListener);

            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            this.registerReceiver(mReceiver, filter);

            filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            this.registerReceiver(mReceiver, filter);

            filter = new IntentFilter(BluetoothDevice.ACTION_NAME_CHANGED);
            this.registerReceiver(mReceiver, filter);

            mBtAdapter = BluetoothAdapter.getDefaultAdapter();

            Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
                findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
                for (BluetoothDevice device : pairedDevices) {
                    mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            } else {
                String noDevices = getResources().getText(R.string.none_paired).toString();
                mPairedDevicesArrayAdapter.add(noDevices);
            }

            doDiscovery();
            scanButton.setVisibility(View.GONE);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }
        this.unregisterReceiver(mReceiver);
    }

    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {
        if (D) Log.d(TAG, "doDiscovery()");

        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);

        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
        try {

            mBtAdapter.startDiscovery();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            mBtAdapter.cancelDiscovery();

            String info = ((TextView) v).getText().toString();
            
            if (info.equals(getString(R.string.none_found))) {
            	return;
            }
            
            String address = info.substring(info.length() - 17);

            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
            
            ArrayList<String> devices = new ArrayList<String>();

            for (int i = 0, size = mNewDevicesArrayAdapter.getCount(); i < size; i++) {
            	String item = mNewDevicesArrayAdapter.getItem(i);
                if (item.equals(getString(R.string.none_found))) {
                	continue;
                }
            	devices.add(item);
            }

            try {
                intent.putStringArrayListExtra(ALL_DEVICE_ADDRESS, devices);

                setResult(Activity.RESULT_OK, intent);
                finish();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {

                    mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
                if (mNewDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    mNewDevicesArrayAdapter.add(noDevices);
                }
            } else if (BluetoothDevice.ACTION_NAME_CHANGED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String newName = "";
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                	for (int i = 0; i < mNewDevicesArrayAdapter.getCount(); i++) {
                		String info = mNewDevicesArrayAdapter.getItem(i);
                		String[] s = info.split("\n");
                		if (s.length>1 && s[1].equals(device.getAddress())) {
                			newName = device.getName() + "\n" + s[1];
                			mNewDevicesArrayAdapter.insert(newName, i);
                			mNewDevicesArrayAdapter.remove(info);
                			break;
                		}
                	}
                }
            }
        }
    };

}

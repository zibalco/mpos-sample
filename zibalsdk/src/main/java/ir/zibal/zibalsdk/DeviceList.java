package ir.zibal.zibalsdk;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.tosantechno.mpos.pax.d180.Configuration;
import com.tosantechno.mpos.pax.d180.DeviceBasic;
import com.tosantechno.mpos.pax.d180.DeviceDisplay;
import com.tosantechno.mpos.pax.d180.DeviceException;
import com.tosantechno.mpos.pax.d180.DeviceKeypad;
import com.tosantechno.mpos.pax.d180.GetMposResponse;
import com.tosantechno.mpos.pax.d180.ParsException;
import com.tosantechno.mpos.pax.d180.RequestTrnParam;
import com.tosantechno.mpos.pax.d180.TrnManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class DeviceList {

    private final int REQUEST_BT_DISCOVER = 2;
    private Context context;
    private ArrayList<BluetoothDevice> btScannedDevs;
    private BluetoothAdapter btAdapter;
    private String tempBtMac;
    private Configuration cfgMgr;
    private DeviceBasic bsMngr;
    private TrnManager trMngr;
    private DeviceDisplay dpMngr;
    private DeviceKeypad kpMngr;
    private byte language = 1;

    public DeviceList(Context context) {
        this.context = context;
    }

    public void getBalance() {


        try {

//            mySpinnerDialog.show();
            Random random = new Random();
            int rand = 100000 + random.nextInt(900000);
            String ReserveNum = rand + "";
            rand = 70000 + random.nextInt(900000);
            String traceNo = rand + "";


            RequestTrnParam requestTrnParam = new RequestTrnParam();
            requestTrnParam.RequestTrnParamBalance(trMngr.PROCESSING_CODE_BALANCE, traceNo, "364", language, 600000);
            Log.w("requestTrnParam_proce", requestTrnParam.processingCode);
            Log.w("requestTrnParam_traceNO", requestTrnParam.traceNO);



            GetMposResponse getMposResponse = trMngr.getTransaction(requestTrnParam);

//            doTrnsPayment(getMposResponse.data, getMposResponse.pinBlk, bsMngr.readSN(), getMposResponse.ksn, ReserveNum, "", "EN_BALANCE","","","");


        } catch (IOException e) {
            Toast.makeText(context, "عملیات لغو شد!", Toast.LENGTH_LONG).show();
//            mySpinnerDialog.hide();
            e.printStackTrace();
        } catch (ParsException e) {
            Toast.makeText(context, "عملیات لغو شد!", Toast.LENGTH_LONG).show();
//            mySpinnerDialog.hide();
            e.printStackTrace();
        } catch (DeviceException e) {
            Toast.makeText(context, "عملیات لغو شد!", Toast.LENGTH_LONG).show();
//            mySpinnerDialog.hide();
            e.printStackTrace();
        }

    }

//    private void doTrnsPayment(final String secureData, final String pinBlockStr, final String pinPadSerial, final String PINBLK_KSN_KEY, final String ReserveNum, final String Amount, final String TransType,final String BillID,final String PayID,final String TopUpMobileNo) {
//        runOnUiThread(new Runnable() {
//
//
//            @Override
//            public void run() {
//
//                try {
//
//
//                    readImei();
//                    readImei();
//
//
//                    pay.mposPaymentTransRequest(TerminalSerial, secureData, pinBlockStr, PINBLK_KSN_KEY, pinPadSerial, ReserveNum, Amount, TransType,BillID,PayID,TopUpMobileNo, new Handler.Callback() {
//                        @Override
//                        public boolean handleMessage(Message msg) {
//                            final JSONObject resp = (JSONObject) msg.obj;
//
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//
//
//                                    try {
//                                        mySpinnerDialog.hide();
//                                        String data = resp.getString("SecureResponse");
//
//                                        Log.e("data", data);
//
//                                        String res = trMngr.giveResponse(new ResponseTrnParam(data, language, 600000));
//
//                                        Log.e("giveResponse", res);
//
//                                        if (res.equals("00")) {
//                                            if(!(TransType.equals("EN_BALANCE"))) {
//                                                paySuccess(resp.toString());}
//
//                                        } else {
//                                            Toast.makeText(getApplicationContext(), "تراکنش تایید نشد تراکنش ناموفق", Toast.LENGTH_LONG).show();
//
//
//                                        }
//
//                                    } catch (Exception e) {
//                                        mySpinnerDialog.hide();
//                                        e.printStackTrace();
//                                    }
//
//                                }
//                            });
//                            return false;
//                        }
//                    }, new Handler.Callback() {
//                        @Override
//                        public boolean handleMessage(final Message msg) {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    try {
//                                        mySpinnerDialog.hide();
//                                    } catch (Exception ex) {
//                                    }
//                                    Main.notify(getApplicationContext(), (String) msg.obj.toString());
//                                }
//                            });
//                            return false;
//                        }
//                    });
//
//
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//
//            }
//        });
//    }


    /***/
    public void initDevice() {

        btScannedDevs = new ArrayList<BluetoothDevice>();
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        bsMngr = DeviceBasic.getInstance(context);
        trMngr = TrnManager.getInstance(context);
        cfgMgr = Configuration.getInstance(context);
        dpMngr = DeviceDisplay.getInstance(context);
        kpMngr = DeviceKeypad.getInstance(context);
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

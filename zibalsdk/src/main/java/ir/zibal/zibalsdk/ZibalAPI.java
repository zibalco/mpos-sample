package ir.zibal.zibalsdk;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Random;

public class ZibalAPI {

    private Context context;
    private Context appCtx;
    private Activity activity;
    private DeviceBasic bsMngr;
    private TrnManager trMngr;
    private TrnPayment pay;
    private DeviceDisplay dpMngr;
    private DeviceKeypad kpMngr;
    private byte language = 1;
    static String  TerminalSerial="";
    private byte manualFlag = 0;
    private byte topUpSecMobileNoFlag = 0;


    public ZibalAPI(Context context) {

        this.context = context;
        this.activity = (Activity) context;
        this.appCtx = context.getApplicationContext();
        this.initDevice();
        this.defaultSettings();
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



    /***/
    private void initDevice() {

        bsMngr = DeviceBasic.getInstance(context);
        trMngr = TrnManager.getInstance(context);
        dpMngr = DeviceDisplay.getInstance(context);
        kpMngr = DeviceKeypad.getInstance(context);
        pay = new TrnPayment(context);
    }


    public void navigateToBluetooth(Activity activity){
        Intent intent = new Intent(activity, Bluetooth.class);
        activity.startActivity(intent);
    }

    private void makeRequest() {
        ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.READ_PHONE_STATE},1111);
    }

    public static void notify(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }

    void readImei(){

        try {


            int Perm= ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
            if (Perm!= PackageManager.PERMISSION_GRANTED)
                makeRequest();
            else {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

                TerminalSerial = telephonyManager.getDeviceId();



                //Log.i("TerminalSerial",TerminalSerial);
            }

        }catch (Exception ex){
            //  mySpinnerDialog.hide();
            notify(context,"خطا در خواندن سریال دستگاه");

        }


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

    /*/*/
    public void injectKeys() {
        //mySpinnerDialog.show();
        String PinPadSerial = "";
        try {

            PinPadSerial = bsMngr.readSN();



            readImei();

            if (TerminalSerial=="")
            {
                // mySpinnerDialog.hide();
                Toast.makeText(context,"خطا در خواندن سریال دستگاه",Toast.LENGTH_LONG).show();
                return;
            }


            pay.mposInitRequest(TerminalSerial, PinPadSerial, new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    final JSONObject result = (JSONObject) msg.obj;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //     mySpinnerDialog.hide();
                            ZibalAPI.notify(appCtx, "در حال بارگذاری کلید");
                            try {

                                FileOutputStream outputStream = appCtx.openFileOutput("terminalINF.DAT", Context.MODE_PRIVATE);
                                ObjectOutputStream stream = new ObjectOutputStream(outputStream);

                                stream.writeObject(new TerminalINF( result.getString("terminalId").toString(),result.getString("merchantId").toString()));
                                stream.flush();
                                stream.close();
                                outputStream.flush();
                                outputStream.close();


                            } catch (JSONException e) {

                                e.printStackTrace();
                            } catch (FileNotFoundException e) {

                                e.printStackTrace();
                            } catch (IOException e) {

                                e.printStackTrace();
                            }

                            injectKeysToDevice(result);
                        }
                    });
                    return false;
                }
            }, new Handler.Callback() {
                @Override
                public boolean handleMessage(final Message msg) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //     mySpinnerDialog.hide();
                            ZibalAPI.notify(appCtx, "دریافت کلید انجام نشد" + "\n" + msg.obj.toString());
                        }
                    });
                    return false;
                }
            });


        } catch (IOException e) {
            //  mySpinnerDialog.hide();
            e.printStackTrace();
        } catch (DeviceException e) {
            //  mySpinnerDialog.hide();
            e.printStackTrace();
        }


    }

    /***/
    private void injectKeysToDevice(JSONObject KEYS) {
        try {


            beep();
            trMngr.injectSessionKey(trMngr.KEY_TYPE_DATA_KEY, KEYS.getString("dpKey"));
            trMngr.injectSessionKey(trMngr.KEY_TYPE_PIN_KEY, KEYS.getString("pinKey"));
            trMngr.injectSessionKey(trMngr.KEY_TYPE_MAC_KEY, KEYS.getString("macKey"));
            trMngr.injectSessionKey(trMngr.KEY_TYPE_TIK_KSN_KCV_KEY, KEYS.getString("tikKey"), KEYS.getString("ksnKey"), KEYS.getString("kcvKey"));
            notify(appCtx, "کلیدگذاری انجام پذیرفت");
            beep();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParsException e) {
            e.printStackTrace();
        } catch (DeviceException e) {
            e.printStackTrace();
        }
    }

    public void getData() {
        try {
            FileInputStream fileInputStream = appCtx.openFileInput("config.DAT");
            ObjectInputStream stream = new ObjectInputStream(fileInputStream);
            ConfigParam param = (ConfigParam) stream.readObject();
            stream.close();
            fileInputStream.close();
            if (param.mposLanguage.equals("fa"))
                language = dpMngr.TERMINAL_LANGUAGE_FARSI;

            else
                language = dpMngr.TERMINAL_LANGUAGE_ENGLISH;


            if (param.mposManualFlag == false)
                manualFlag = RequestTrnParam.MANUAL_FLAG_DEACTIVE;
            else
                manualFlag = RequestTrnParam.MANUAL_FLAG_ACTIVE;

            if (param.topUpSecMobileNo == false)
                topUpSecMobileNoFlag = RequestTrnParam.RECEIPT_MOBILE_DISABLE;
            else
                topUpSecMobileNoFlag = RequestTrnParam.RECEIPT_MOBILE_ENABLE;


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void defaultSettings(){
        try {
            FileOutputStream outputStream = appCtx.openFileOutput("config.DAT", Context.MODE_PRIVATE);
            ObjectOutputStream stream = new ObjectOutputStream(outputStream);

            ConfigParam param = new ConfigParam("fa", context.getString(R.string.web_service_url), "0",false,false);
            stream.writeObject(param);
            stream.flush();
            stream.close();
            outputStream.flush();
            outputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileInputStream fileInputStream = appCtx.openFileInput("config.DAT");
            ObjectInputStream stream = new ObjectInputStream(fileInputStream);
            ConfigParam param = (ConfigParam) stream.readObject();
            stream.close();
            fileInputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Toast.makeText(appCtx, "تنظیمات با موفقیت ذخیره شد", Toast.LENGTH_LONG).show();

    }
}

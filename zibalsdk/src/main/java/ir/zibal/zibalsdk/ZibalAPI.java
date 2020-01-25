package ir.zibal.zibalsdk;

import android.Manifest;
import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.tosantechno.mpos.pax.d180.Configuration;
import com.tosantechno.mpos.pax.d180.DeviceBasic;
import com.tosantechno.mpos.pax.d180.DeviceDisplay;
import com.tosantechno.mpos.pax.d180.DeviceException;
import com.tosantechno.mpos.pax.d180.DeviceKeypad;
import com.tosantechno.mpos.pax.d180.GetMposResponse;
import com.tosantechno.mpos.pax.d180.ParsException;
import com.tosantechno.mpos.pax.d180.RequestTrnParam;
import com.tosantechno.mpos.pax.d180.ResponseTrnParam;
import com.tosantechno.mpos.pax.d180.TrnManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import ir.zibal.zibalsdk.datatypes.PardakhtNovinAppResponse;
import ir.zibal.zibalsdk.datatypes.ZibalInitialResponse;

public class ZibalAPI {

    private static ZibalAPI zibalAPIInstance;
    private Context context;
    private Context appCtx;
    private Activity activity;
    private DeviceBasic bsMngr;
    private TrnManager trMngr;
    private TrnPayment pay;
    private DeviceDisplay dpMngr;
    private DeviceKeypad kpMngr;
    private byte language = 1;
    static String TerminalSerial = "";
    String RefNum = "";
    private byte manualFlag = 0;
    private byte topUpSecMobileNoFlag = 0;


    public ZibalAPI(Context context) {

        this.context = context;
        this.activity = (Activity) context;
        this.appCtx = context.getApplicationContext();

        pay = new TrnPayment(context);
        this.initDevice();
        this.defaultSettings();
    }

    public static ZibalAPI getInstance(Context context) {
        if (zibalAPIInstance == null) {
            zibalAPIInstance = new ZibalAPI(context);
            return zibalAPIInstance;
        }
        return zibalAPIInstance;
    }

    public void startPayment(String amount,String zibalId) {


        if (amount.length() <= 0) {
            Toast.makeText(context, "مبلغ نمی تواند کمتر از ۱۰۰۰ ریال باشد",
                    Toast.LENGTH_SHORT).show();

            return;
        }
        int amountRQ = Integer.parseInt(amount);
        if (amountRQ < 1000) {
            Toast.makeText(context, "مبلغ نمی تواند کمتر از ۱۰۰۰ ریال باشد",
                    Toast.LENGTH_SHORT).show();
        } else {

            try {

                // mySpinnerDialog.show();
                Random random = new Random();
                int rand = 100000 + random.nextInt(900000);
                String ReserveNum = rand + "";
                rand = 70000 + random.nextInt(900000);
                String traceNo = rand + "";


                RequestTrnParam requestTrnParam = new RequestTrnParam();
                requestTrnParam.RequestTrnParamGoods(trMngr.PROCESSING_CODE_GOODS_AND_SERVICE, amount, "0", traceNo, "364", language, 600000);
                GetMposResponse getMposResponse = trMngr.getTransaction(requestTrnParam);

                doTrnsPayment(zibalId,getMposResponse.data, getMposResponse.pinBlk, bsMngr.readSN(), getMposResponse.ksn, ReserveNum, getMposResponse.amount, "EN_GOODS", "", "", "");

            } catch (IOException e) {
                Toast.makeText(context.getApplicationContext(), "عملیات لغو شد!", Toast.LENGTH_LONG).show();
//                mySpinnerDialog.hide();
                e.printStackTrace();
            } catch (ParsException e) {
                Toast.makeText(context.getApplicationContext(), "عملیات لغو شد!", Toast.LENGTH_LONG).show();
//                mySpinnerDialog.hide();
                e.printStackTrace();
            } catch (DeviceException e) {
                Toast.makeText(context.getApplicationContext(), "عملیات لغو شد!", Toast.LENGTH_LONG).show();
//                mySpinnerDialog.hide();
                e.printStackTrace();
            }
        }

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

            doTrnsPayment("",getMposResponse.data, getMposResponse.pinBlk, bsMngr.readSN(), getMposResponse.ksn, ReserveNum, "", "EN_BALANCE", "", "", "");


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


    private void doTrnsPayment(final String zibalId,final String secureData, final String pinBlockStr, final String pinPadSerial, final String PINBLK_KSN_KEY, final String ReserveNum, final String Amount, final String TransType, final String BillID, final String PayID, final String TopUpMobileNo) {
        this.activity.runOnUiThread(new Runnable() {


            @Override
            public void run() {

                try {

                    readImei();

                    pay.mposPaymentTransRequest(TerminalSerial, secureData, pinBlockStr, PINBLK_KSN_KEY, pinPadSerial, ReserveNum, Amount, TransType, BillID, PayID, TopUpMobileNo, new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            final JSONObject resp = (JSONObject) msg.obj;

                            ZibalAPI.this.activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


                                    try {
//                                        mySpinnerDialog.hide();
                                        String data = resp.getString("SecureResponse");

                                        Log.e("data", data);

                                        String res = trMngr.giveResponse(new ResponseTrnParam(data, language, 600000));

                                        Log.e("giveResponse", res);

                                        if (res.equals("00")) {
                                            if (!(TransType.equals("EN_BALANCE"))) {
                                                paySuccess(zibalId,resp.toString());
                                            }

                                        } else {
                                            Toast.makeText(ZibalAPI.this.activity, "تراکنش تایید نشد تراکنش ناموفق", Toast.LENGTH_LONG).show();


                                        }

                                    } catch (Exception e) {
//                                        mySpinnerDialog.hide();
                                        e.printStackTrace();
                                    }

                                }
                            });
                            return false;
                        }
                    }, new Handler.Callback() {
                        @Override
                        public boolean handleMessage(final Message msg) {
                            ZibalAPI.this.activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
//                                        mySpinnerDialog.hide();
                                    } catch (Exception ex) {
                                    }
                                    ZibalAPI.notify(ZibalAPI.this.activity, (String) msg.obj.toString());
                                }
                            });
                            return false;
                        }
                    });


                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });
    }

    private void paySuccess(String zibalId,String resp) {

        try {
            JSONObject jRes = new JSONObject((String) resp);
            String result = jRes.getString("Result");

            if (result.equals("erSucceed")) {


                RefNum = getJsonData(jRes, "ReferenceNum");
                VerifyMobTransResult(zibalId,jRes);
                Toast.makeText(context, "پرداخت با موفقیت انجام پذیرفت", Toast.LENGTH_LONG).show();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void VerifyMobTransResult(String zibalId,JSONObject response) {

        //todo call verify ZIBAL API
        new PushTransaction(zibalId).execute(response);
//        pay.mposVerifyTransRequest(RefNum, new Handler.Callback() {
//            @Override
//            public boolean handleMessage(Message msg) {
//
//
//                final JSONObject result = (JSONObject) msg.obj;
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                });
//
//                return false;
//            }
//        }, new Handler.Callback() {
//            @Override
//            public boolean handleMessage(Message msg) {
//                ZibalAPI.notify(appCtx, msg.obj.toString());
//                return false;
//            }
//        });

    }

    private class PushTransaction extends AsyncTask<JSONObject, Void, Boolean> {

        byte[] encryptedResponse;
        String zibalId;

        public PushTransaction(String zibalId) {
            this.zibalId = zibalId;
        }

        @Override
        protected Boolean doInBackground(JSONObject... jsonObjects) {
            ZibalServer zibalServer = new ZibalServer();
            String terminalID = "";
            try {
                FileInputStream fileInputStream = null;
                fileInputStream = context.openFileInput("terminalINF.DAT");
                ObjectInputStream stream = new ObjectInputStream(fileInputStream);
                TerminalINF terminalINF = (TerminalINF) stream.readObject();
                stream.close();
                fileInputStream.close();
                terminalID = (terminalINF.terminalID);


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            HashMap<String,String> response = zibalServer.taiidPardakht(zibalId,
                    jsonObjects[0].optString("Rrn"),
                    jsonObjects[0].optString("MaskPan"),
                    jsonObjects[0].optString("referenceNum"),
                    terminalID,
                    ((System.currentTimeMillis()/1000) + ""));
            if (response != null && !response.containsKey("isConnectionStablished") && response.get("9F00").equals("01"))
                return true;
            else{
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            //todo toast zibal response
        }
    }

    private String getJsonData(JSONObject jRes, String ID) throws JSONException {
        return jRes.has(ID) ? jRes.getString(ID) : "";
    }


    /***/
    private void initDevice() {

        bsMngr = DeviceBasic.getInstance(context);
        trMngr = TrnManager.getInstance(context);
        dpMngr = DeviceDisplay.getInstance(context);
        kpMngr = DeviceKeypad.getInstance(context);
        pay = new TrnPayment(context);
    }


    public void navigateToBluetooth(Activity activity) {
        Intent intent = new Intent(activity, Bluetooth.class);
        activity.startActivity(intent);
    }

    private void makeRequest() {
        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_PHONE_STATE}, 1111);
    }

    public static void notify(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }

    void readImei() {

        try {


            int Perm = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
            if (Perm != PackageManager.PERMISSION_GRANTED)
                makeRequest();
            else {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

//                TerminalSerial = telephonyManager.getDeviceId();
                TerminalSerial = "09912772610";


                //Log.i("TerminalSerial",TerminalSerial);
            }

        } catch (Exception ex) {
            //  mySpinnerDialog.hide();
            notify(context, "خطا در خواندن سریال دستگاه");

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

            if (TerminalSerial == "") {
                // mySpinnerDialog.hide();
                Toast.makeText(context, "خطا در خواندن سریال دستگاه", Toast.LENGTH_LONG).show();
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

                                stream.writeObject(new TerminalINF(result.getString("terminalId").toString(), result.getString("merchantId").toString()));
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

    private void defaultSettings() {
        try {
            FileOutputStream outputStream = appCtx.openFileOutput("config.DAT", Context.MODE_PRIVATE);
            ObjectOutputStream stream = new ObjectOutputStream(outputStream);

            ConfigParam param = new ConfigParam("fa", context.getString(R.string.web_service_url), "0", false, false);
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

package ir.zibal.zibalsdk;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;

import ir.zibal.zibalsdk.util.RestClient;


public class TrnPayment {

    private static String merchant_mobile;
    private static String webservice_URL;
    private static String mposInitRequest;
    private static String mposPaymentTransRequest;
    private static String mposVerifyTransRequest;

    private static String merchantID = null;
    private static String terminalID = null;
    private static String Language = null;

    private RestClient restClient;
    private static Context ctx;


    public TrnPayment(Context applicationContext) {

        ctx = applicationContext;
        restClient = new RestClient(ctx);


    }


    void initConfig() {

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = ctx.openFileInput("config.DAT");
            ObjectInputStream stream = new ObjectInputStream(fileInputStream);
            ConfigParam param = (ConfigParam) stream.readObject();
            stream.close();
            fileInputStream.close();
            Language = param.mposLanguage;
            merchant_mobile = (param.merchant_mobile);
            webservice_URL = (param.webServiceURL);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (OptionalDataException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    void getterminalID() {
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = ctx.openFileInput("terminalINF.DAT");
            ObjectInputStream stream = new ObjectInputStream(fileInputStream);
            TerminalINF terminalINF = (TerminalINF) stream.readObject();
            stream.close();
            fileInputStream.close();
            terminalID = (terminalINF.terminalID);
            merchantID = (terminalINF.merchantID);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (OptionalDataException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    static {
        mposPaymentTransRequest = "mpos/doMPOSTrans/";
        mposInitRequest = "mpos/MPOSInit/";
        mposVerifyTransRequest = "mpos/verifyMPOSTrans/";

    }


    /*********************************************************************************************/
    /***/
    public void mposInitRequest(
            String terminalSerial,
            String pinPadSerial,
            final Handler.Callback onSuccess,
            final Handler.Callback onError
    ) {
//        todo uncomment
        initConfig();
        JSONObject params = new JSONObject();
        try {
            params.put("IMEISerial", terminalSerial);
            params.put("MPOSSerial", pinPadSerial);
            params.put("isSystemUser", "true");

        } catch (JSONException e) {
            e.printStackTrace();
            onError.handleMessage(restClient.getMsg("erFailure1"));
        }
        String Url = "";
        if (webservice_URL == "" || webservice_URL == null) {
//            Main.mySpinnerDialog.hide();//todo uncomment
            Toast.makeText(ctx, "پیکربندی انجام نشده", Toast.LENGTH_LONG).show();
            Log.e("webservice_URL", "پیکربندی انجام نشده");
            return;
        }
        Url = webservice_URL + mposInitRequest;
        try {
            restClient.request(Url, params, onSuccess, onError);
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }


    /********************************************************************************************/

    /***/
    public void mposPaymentTransRequest(
            String TerminalSerial,
            String secureData,
            String PinBlock,
            String KSN_PinBlock,
            String pinPadSerial,
            String ReserveNum,
            String Amount, String TransType,
            String BillID, String PayID, String TopUpMobileNo,
            final Handler.Callback onSuccess,
            final Handler.Callback onError
    ) {

        initConfig();
        getterminalID();
        if (terminalID == "" || terminalID == null) {
//            Main.mySpinnerDialog.hide();//todo uncomment
            Toast.makeText(ctx, "کلید گذاری انجام نشده", Toast.LENGTH_LONG).show();
//            return;
        }
        JSONObject CardData = new JSONObject();
        JSONObject params = new JSONObject();

        try {

            CardData.put("SecureData", secureData);
            CardData.put("PinBlock", PinBlock);
            CardData.put("KSN", KSN_PinBlock);
            //CardData.put("KSN", KSN_PinBlock);
/////////////////////////////////////////////////////////////////

            params.put("TerminalSerial", TerminalSerial);
            params.put("CardData", CardData);
            params.put("PinPadSerial", pinPadSerial);
            params.put("TransType", TransType);
            params.put("TerminalID", terminalID);
            params.put("MerchantID", merchantID);
            params.put("Lang", Language);
            params.put("ReserveNum", ReserveNum);


            if (!(TransType.equals("EN_BALANCE")))
                params.put("Amount", Amount);

            if (TransType.equals("EN_BILL_PAY")) {
                params.put("BillId", BillID);
                params.put("PayId", PayID);
            }

            if (TransType.equals("EN_TOP_UP"))
                params.put("TopUpMobileNo", TopUpMobileNo);


        } catch (JSONException e) {
            e.printStackTrace();
        }


        String Url = "";

        if (webservice_URL == "" || webservice_URL == null) {
//            Main.mySpinnerDialog.hide();//todo uncomment
            Toast.makeText(ctx, "پیکربندی انجام نشده", Toast.LENGTH_LONG).show();
            Log.e("webservice_URL", "پیکربندی انجام نشده");
            return;
        }
        Url = webservice_URL + mposPaymentTransRequest;


        try {
            restClient.request(Url, params, onSuccess, onError);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /********************************************************************************************/
    public void mposVerifyTransRequest(
            String RefNum,
            final Handler.Callback onSuccess,
            final Handler.Callback onError
    ) {

        initConfig();
        JSONObject params = new JSONObject();

        try {
            params.put("ReferenceNum", RefNum);

        } catch (JSONException e) {
            e.printStackTrace();
            onError.handleMessage(restClient.getMsg("erFailure2"));
        }


        String Url = "";
        if (webservice_URL == "" || webservice_URL == null) {
//            Main.mySpinnerDialog.hide();//todo uncomment
            Toast.makeText(ctx, "پیکربندی انجام نشده", Toast.LENGTH_LONG).show();
            Log.e("webservice_URL", "پیکربندی انجام نشده");
            return;
        }
        Url = webservice_URL + mposVerifyTransRequest;

        try {
            restClient.request(Url, params, onSuccess, onError);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


}

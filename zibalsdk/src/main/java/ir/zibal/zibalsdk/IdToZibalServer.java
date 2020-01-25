package ir.zibal.zibalsdk;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.HashMap;

import ir.zibal.zibalsdk.datatypes.ZibalInitialResponse;

public class IdToZibalServer extends AsyncTask<String, Void, ZibalInitialResponse> {

    private Context context;
    private Activity activity;
    private String zibalId;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//            pb.setVisibility(View.VISIBLE);
    }

    public IdToZibalServer(Context context, Activity activity,String zibalId) {
        this.context = context;
        this.activity = activity;
        this.zibalId = zibalId;
    }

    @Override
    protected  ZibalInitialResponse doInBackground(String... strings) {

        ZibalServer zibalServer = new ZibalServer();

        HashMap<String, String> response = zibalServer.getOrder(zibalId, getterminalID());

        ZibalInitialResponse initialResponse = new ZibalInitialResponse();
        if (response == null){
            initialResponse.setInnerStatus(ZibalInitialResponse.DISCONNECT);
        }
        else if (response.get("9F00").equals("01")){//ready to pay
            initialResponse.setStatus(response.get("9F00"));
            initialResponse.setPrice(response.get("9F10"));
            initialResponse.setFactorNo(hexStringToByteArray(response.get("9F11")));
            initialResponse.setSellerName(hexStringToByteArray(response.get("9F12")));
            initialResponse.setInnerStatus(ZibalInitialResponse.READY_TO_PAY);
        }
        else{//invalid zinal id
            initialResponse.setStatus(response.get("9F00"));
            initialResponse.setErrorCode(response.get("9F14"));
            if (initialResponse.getErrorCode().equals("00"))
                initialResponse.setInnerStatus(ZibalInitialResponse.PAID);
            else if (initialResponse.getErrorCode().equals("01") || initialResponse.getErrorCode().equals("02"))
                initialResponse.setInnerStatus(ZibalInitialResponse.INVALID);

        }
        return initialResponse;
    }

    @Override
    protected void onPostExecute(ZibalInitialResponse zibalInitialResponse) {
        super.onPostExecute(zibalInitialResponse);

        ZibalAPI zibalAPI = ZibalAPI.getInstance(context);
//            isKeyboardLocked = false;
//            pb.setVisibility(View.INVISIBLE);
        switch (zibalInitialResponse.getInnerStatus()){
            case ZibalInitialResponse.DISCONNECT:
                Toast.makeText(activity, "عدم دسترسی به سرور زیبال",Toast.LENGTH_SHORT).show();
                break;
            case ZibalInitialResponse.INVALID:
                Toast.makeText(activity, "شناسه زیبال نامعتبر است.",Toast.LENGTH_SHORT).show();
//                    idBox.setText("");
                break;
            case ZibalInitialResponse.PAID:
//                    idBox.setText("");
                Toast.makeText(activity, "شناسه قبلا پرداخت شده.",Toast.LENGTH_SHORT).show();
                break;
            case ZibalInitialResponse.READY_TO_PAY:
                zibalAPI.startPayment(""+zibalInitialResponse.getPrice(),zibalId);
        }
    }

    private String hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return new String(data);
    }

    String getterminalID() {
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = context.openFileInput("terminalINF.DAT");
            ObjectInputStream stream = new ObjectInputStream(fileInputStream);
            TerminalINF terminalINF = (TerminalINF) stream.readObject();
            stream.close();
            fileInputStream.close();
            String terminalID = (terminalINF.terminalID);
            return terminalID;
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

        return "";
    }

}

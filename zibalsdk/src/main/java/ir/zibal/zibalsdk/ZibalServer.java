package ir.zibal.zibalsdk;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

import ir.zibal.zibalsdk.Encryption.AES;
import ir.zibal.zibalsdk.Encryption.TLV;


public class ZibalServer {

    String serverIp;
    int port;
    Socket smtpSocket = null;
    DataOutputStream os = null;
    DataInputStream is = null;

    public ZibalServer() {
        serverIp = "https://api.zibal.ir";
        port = 2500;
    }

    public ZibalServer(String serverIp, int port) {
        this.serverIp = serverIp;
        this.port = port;
    }

    public void init() {
        try {
            smtpSocket = new Socket(serverIp, port);
            os = new DataOutputStream(smtpSocket.getOutputStream());
            is = new DataInputStream(smtpSocket.getInputStream());

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: hostname");
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: hostname");
        }
    }

    void close() {
        try {
            os.close();
            is.close();
            smtpSocket.close();
        } catch (IOException e) {
            System.err.println("IOException:  " + e);
        }
    }

    public HashMap<String, String> getOrder(String zibalId, String terminalId) {
        String strMessage = null;
        String serialized = null;
        TLV box = new TLV();

        box.add("9F01", padLeft(zibalId, 12, '0'));
        box.add("9F02", "01");
        box.add("9F03", padLeft(terminalId, 12, '0'));

        serialized = box.serialize();

        strMessage = send(serialized);
        return new TLV(strMessage).getAll();
    }

    public HashMap<String, String> taiidPardakht(String zibalId, String refNumber, String cardNumber, String payNumber, String SerialNumber, String paidAt) {
        String strMessage = null;
        String serialized = tlvBoxInitializer(zibalId, refNumber, cardNumber, payNumber, SerialNumber, paidAt);

        strMessage = send(serialized);
        if (strMessage != null)
            return new TLV(strMessage).getAll();
        else{
            HashMap<String, String> rep = new HashMap<>();
            return rep;
        }
    }
    String tlvBoxInitializer(String zibalId, String refNumber, String cardNumber, String payNumber, String SerialNumber, String paidAt){
        String serialized = null;
        TLV box = new TLV();

        box.add("9F01", padLeft(zibalId, 12, '0'));
        box.add("9F02", "02");
        box.add("9F30", padLeft(refNumber, 12, '0'));
        box.add("9F31", padLeft(cardNumber, 16, '0'));
        box.add("9F32", padLeft(payNumber, 6, '0'));
        box.add("9F33", padLeft(SerialNumber, 12, '0'));
        box.add("9F34", padLeft(paidAt, 12, '0'));

        serialized = box.serialize();
        return serialized;
    }

    String send(String serialized) {
//        init();

        byte[] encrypted = preSendEncryption(serialized);
        return sendRestEncryptGetReposnse(encrypted);
    }/*
    public static boolean isConnected (Context context){

        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        if (cm != null) {
            netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected())
                return true;
        }
        return false;
    }*/

    public String sendRestEncryptGetReposnse(byte[] encrypted)
    {
        HttpURLConnection urlConn;
        try {
            URL mUrl = new URL(serverIp+"/cod/app/handle");
            urlConn = (HttpURLConnection) mUrl.openConnection();
            urlConn.setRequestMethod("POST");
//            urlConn.setRequestProperty("Content-Length", Integer.toString(encrypted.length));
            urlConn.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(urlConn.getOutputStream());

            System.out.println(new String(encrypted).length());
            writer.write(new String(Base64.getEncoder().encode(encrypted)));
//            writer.write(new String(encrypted));
//            writer.write(URLEncoder.encode(new String(encrypted),"UTF-8"));
            writer.flush();
            writer.close();

            StringBuffer response = new StringBuffer();
            int responseCode = urlConn.getResponseCode();
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            String decrypted = new String(AES.decrypt(Base64.getDecoder().decode(response.toString())));
            return decrypted;
//            urlConn.getOutputStream().write(encrypted);
        }catch (Exception e){
            System.out.println("error");
            e.printStackTrace();
        }

        return "";


    }

    public String sendEncryptedGetResponse(byte[] encrypted){
        byte[] decrypted = null;
        if (smtpSocket != null && os != null && is != null) {
            try {

                os.write(encrypted);
                os.flush();

                ArrayList<Byte> response = new ArrayList<Byte>();

                int byteRead = is.read();
                while (byteRead != -1) {
                    response.add((byte) byteRead);
                    byteRead = is.read();
                }

                byte[] arrResponse = new byte[response.size()];
                for (int i = 0; i < response.size(); i++) {
                    arrResponse[i] = response.get(i);
                }

//                System.out.println("Server(" + response.size() + "): " + response);
                decrypted = AES.decrypt(arrResponse);
//                System.out.println("Server Decrypt: " + strMessage);
                close();
                return new String(decrypted);
            } catch (UnknownHostException e) {
                System.err.println("Trying to connect to unknown host: " + e);
            } catch (IOException e) {
                System.err.println("IOException:  " + e);
            }
        }
        return new String("null");
    }

    byte[] preSendEncryption(String serialized){
        int lengthofmessage = (16 - serialized.length() % 16) + serialized.length();
        String padded = String.format("%-" + Integer.toString(lengthofmessage) + "s", serialized);
        byte[] encrypted = AES.encrypt(padded);
        return  encrypted;
    }

    public static String padRight(String s, int n, char fill) {
        return String.format("%1$-" + n + "s", s).replace(' ', fill);
    }

    public static String padLeft(String s, int n, char fill) {
        return String.format("%1$" + n + "s", s).replace(' ', fill);
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public byte[] getCodedPaymentVerification(String zibalId, String refNumber, String cardNumber, String payNumber, String SerialNumber, String paidAt) {

        String serialized = tlvBoxInitializer(zibalId, refNumber, cardNumber, payNumber, SerialNumber, paidAt);

        byte[] encrypted = preSendEncryption(serialized);
        return encrypted;
    }

    public byte[] getCodedZibalId(String zibalId, String terminalId) {

        String serialized = null;
        TLV box = new TLV();
        box.add("9F01", padLeft(zibalId, 12, '0'));
        box.add("9F02", "01");
        box.add("9F03", padLeft(terminalId, 12, '0'));

        serialized = box.serialize();
        byte[] encrypted = preSendEncryption(serialized);
        return encrypted;
    }
}
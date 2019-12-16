package ir.zibal.zibalsdk.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.*;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import ir.zibal.zibalsdk.ZibalAPI;


public class RestClient {


    private Context ctx;

    public RestClient(Context context) {

        ctx = context;
    }

    /**/


    public class MySSLSocketFactory extends SSLSocketFactory {

        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");

        public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            sslContext.init(null, new TrustManager[]{tm}, null);

        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {

            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);


        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }





    }


    public HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();

            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();

            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }


    public String POST(String url, JSONObject jsonObject) {
        InputStream inputStream = null;
        String result = "";
        try {


            HttpClient httpClient = getNewHttpClient();
            HttpPost httpPost = new HttpPost(url);
           Log.e("GO->", "*************************************************");
          Log.e("url", url);
            String json = jsonObject.toString();
           Log.e("json", json);
            StringEntity se = new StringEntity(json);

            httpPost.setEntity(se);

            // httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-Type", "application/json");

            HttpResponse httpResponse = httpClient.execute(httpPost);


            inputStream = httpResponse.getEntity().getContent();

            if (inputStream != null) {
                result = convertInputStreamToString(inputStream);


            } else
                result = "erFailure";

        } catch (ClientProtocolException e) {
            result = "erFailure";
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            result = "erFailure";
            e.printStackTrace();
        } catch (IOException e) {
            result = "erFailure";
            e.printStackTrace();
        }

        return result;
    }


    public void request(String baseURL, JSONObject param, final Handler.Callback onSuccess, final Handler.Callback onError) {

        if (isNetworkAvailable(ctx)) {


            PostRequestParams params = new PostRequestParams(baseURL, param,
                    new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            String resp = (String) msg.obj.toString();

                            try {
                                JSONObject jResp = new JSONObject(resp);
                                Log.e("result->", "*************************************************");
                                Log.e("result",jResp.toString());
                                String Result = jResp.getString("Result");
                                Log.e("Result", Result);

                                if (Result.equals("erSucceed"))
                                    onSuccess.handleMessage(getMsg(jResp));
                                else {

                                    onError.handleMessage(getMsg(Result));
                                  /*  Main.mySpinnerDialog.hide();
                                    Main.notify(ctx, "خطا در انجام عملیات!");*/
                                }
                            } catch (JSONException e) {

                                onError.handleMessage(getMsg("erFailure"));
                                e.printStackTrace();
                            }
                            return false;
                        }
                    },
                    new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {

                            onError.handleMessage(getMsg("erFailure"));
                            return false;
                        }
                    }
            );

            new HttpAsyncTask().execute(params);
        } else {
//            Main.mySpinnerDialog.hide(); //todo uncomment
            ZibalAPI.notify(ctx, "اتصال اینترنت برقرار نمی باشد!");
        }

    }

    private static class PostRequestParams {
        static String url;
        static JSONObject params;
        Handler.Callback onSuccess;
        Handler.Callback onError;

        PostRequestParams(String url, JSONObject params, Handler.Callback onsuccess, Handler.Callback onerror) {
            this.url = url;
            this.params = params;
            this.onSuccess = onsuccess;
            this.onError = onerror;
        }
    }

    private class HttpAsyncTask extends AsyncTask<PostRequestParams, Void, String> {
        @Override
        protected String doInBackground(PostRequestParams... Params) {

            String res = "";
            res = POST(Params[0].url, Params[0].params);
            if (res.equals("erFailure")) {
                Handler.Callback cb = Params[0].onError;
                cb.handleMessage(getMsg(res));
            } else {
                Handler.Callback cb = Params[0].onSuccess;
                cb.handleMessage(getMsg(res));
            }


            return res;

        }
    }

    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }


    public Message getMsg(JSONObject res) {
        Message msg = new Message();
        msg.obj = res;
        return msg;
    }

    public Message getMsg(String res) {
        Message msg = new Message();
        msg.obj = res;
        return msg;
    }


}

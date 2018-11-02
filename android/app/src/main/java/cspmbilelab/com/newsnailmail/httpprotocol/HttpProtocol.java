package cspmbilelab.com.newsnailmail.httpprotocol;

/**
 * Created by 찬식 on 2017-12-06.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class HttpProtocol {

    private final static String TAG = "** NewSnailMail HP **";
    private Context mContext;
    private File root;
    private String mData = "";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;


    public HttpProtocol(Context con) {
        mContext = con;

        root = Environment.getExternalStorageDirectory().getAbsoluteFile();
        pref = mContext.getSharedPreferences("pref", mContext.MODE_PRIVATE);
        editor = pref.edit();
    }

    /*============================================================================
     * 서버에 파일데이터를 보내는 함수
     *============================================================================
     */
    public void httpPostFileSend(String url, String param) {
        Log.d(TAG, "httpPostFileSend()::");
        new HttpProtocolPostFileSend().execute(url, param, null);
    }

    /*============================================================================
     * 서버에 데이터를 보내는 함수
     *============================================================================
     */
    public void httpPostDataSend(String url, String param, String msg) {
        Log.d(TAG, "httpPostDataSend()::");
        new HttpProtocolPostDataSend().execute(url, param, null);

    }

    /*============================================================================
     * 서버로 부터  파일데이터 다운받는 함수
     *============================================================================
     */
    public void httpGetDownLoadFile(String url, String filename) {
        Log.d(TAG, "HttpGetDownLoadFile()::");
        new HttpGetDownLoadFile().execute(url, filename, null);
    }

    /*============================================================================
     * 서버로 부터  데이터를 다운받는 함수
     *============================================================================
     */
    public String httpGetData(String url) {
        Log.d(TAG, "httpGetData::");
        new HttpGetData().execute(url, null, null);

        return mData;
    }

    /*============================================================================
 * 서버로 부터 이용제한 시간값 데이터 갖고 오는 함수
 *============================================================================
 */
    public String httpTimeSetValueGetData(String url) {
        Log.d(TAG, "HttpTimeSetValueGetData()::");
        new HttpTimeSetValueGetData().execute(url, null, null);

        return mData;
    }

    /*============================================================================
     * 서버에 이용제한 시간값 데이터를 보내는 함수
     *============================================================================
     */
    public void httpTimeSetPostDataSend(String url, String param, String msg) {
        Log.d(TAG, "HttpProtocolTimeSetPostDataSend::");
        new HttpProtocolTimeSetPostDataSend().execute(url, param, msg);
    }

    /**
     * 서버에 파일 보내는 HttpProtocol 클래스 모듈
     * @author csp
     *
     */
    class HttpProtocolPostFileSend extends AsyncTask<String, String, Void> {
        @Override
        protected Void doInBackground(String... params) {

            File file = new File(root + params[0]);

            try {

                HttpClient client = new DefaultHttpClient();

                String postURL = "" + params[0];
                HttpPost post = new HttpPost(postURL);

                FileBody bin = new FileBody(file);

                MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                reqEntity.addPart(params[1], bin);

                post.setEntity(reqEntity);

                HttpResponse response = client.execute(post);
                HttpEntity resEntity = response.getEntity();

                if (resEntity != null)
                    Log.d(TAG, "resEntity ::" + resEntity);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * 서버에 데이터 보내는 HttpProtocol 클래스 모듈
     * @author csp
     *
     */
    class HttpProtocolPostDataSend extends AsyncTask<String, String, Void> {
        protected Void doInBackground(String... params) {

            try {
                HttpClient client = new DefaultHttpClient();

                String postURL = "https://www.snailmail.co.kr/index.php?p="+getPhoneNumber()+"&lat=" + params[0] + "&lon=" + params[1];

                HttpPost post = new HttpPost(postURL);

                List<NameValuePair> param = new ArrayList<NameValuePair>();

                //param.add(new BasicNameValuePair(params[0],params[1]));

                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(param, HTTP.UTF_8);
                post.setEntity(ent);

                HttpResponse responsePOST = client.execute(post);
                HttpEntity resEntity = responsePOST.getEntity();

                if (resEntity != null) {
                    Log.d(TAG, EntityUtils.toString(resEntity));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }


    /**
     *  서버로 부터 파일 다운로드 HttpProtocol 클래스 모듈
     * @author csp
     *
     */
    class HttpGetDownLoadFile extends AsyncTask<String, String, Void> {
        @Override
        protected Void doInBackground(String... params) {

            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setConnectTimeout(10000);
                conn.setUseCaches(false);

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    File dir = new File(root + "/GameKiller");
                    dir.mkdir();

                    File inputFile = new File(root + "/GameKiller/", params[1]);
                    FileOutputStream fileOutput = new FileOutputStream(inputFile);
                    InputStream inputStream = conn.getInputStream();

                    int nDownloadedSize = 0;
                    int nBufferLength = 0;

                    byte[] buffer = new byte[2048];

                    while ((nBufferLength = inputStream.read(buffer)) > 0) {
                        fileOutput.write(buffer, 0, nBufferLength);
                        nDownloadedSize += nBufferLength;
                    }
                    fileOutput.close();

                }
                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    /**
     * 서버로 부터 데이터 갖고오는 HttpProtocol 클래스 모듈
     * @author csp
     *
     */
    class HttpGetData extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {

            String url = params[0];

            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(url);
            HttpResponse response = null;

            try {
                response = client.execute(get);

            } catch (ClientProtocolException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }

            HttpEntity resEntity = response.getEntity();
            String strRes = "";

            if (resEntity != null) {
                try {
                    mData = EntityUtils.toString(resEntity);

                    Log.d(TAG, "mData ::" + mData);

                    //GAME BLOCKED-PARENTS 부모어플의 GCM REG ID를 저장한다
                    editor.putString("GCM_REG_ID_P", mData);
                    editor.commit();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    /**
     * 서버에 이용제한 설정 시간값 보내는 HttpProtocol 클래스 모듈
     * @author csp
     *
     */
    class HttpProtocolTimeSetPostDataSend extends AsyncTask<String, String, Void> {
        protected Void doInBackground(String... params) {
            try {
                HttpClient client = new DefaultHttpClient();

                Log.d(TAG, "PhoneNumber::" + params[1]);

                String postURL = "" + params[1] + "&TYPE=1&TIME_SET_VALUE=" + URLEncoder.encode(params[2], "UTF-8");

                HttpPost post = new HttpPost(postURL);

                List<NameValuePair> param = new ArrayList<NameValuePair>();

                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(param, HTTP.UTF_8);
                post.setEntity(ent);

                HttpResponse responsePOST = client.execute(post);
                HttpEntity resEntity = responsePOST.getEntity();

                if (resEntity != null) {
                    Log.d(TAG, EntityUtils.toString(resEntity));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    /**
     * 서버로 부터 이용제한 시간 설정값 갖고 오는 HttpProtocol 클래스모듈
     * @author csp
     *
     */
    class HttpTimeSetValueGetData extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {

            HttpClient client = new DefaultHttpClient();
            String url = params[0];
            HttpGet get = new HttpGet(url);

            HttpResponse response = null;

            try {
                response = client.execute(get);

            } catch (ClientProtocolException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }

            HttpEntity resEntity = response.getEntity();
            String strRes = "";

            if (resEntity != null) {
                try {
                    mData = EntityUtils.toString(resEntity);
                    Log.d(TAG, "TimeSetValue mData ::" + mData);

                    editor.putString("TIME_SET_VALUE", mData);
                    editor.commit();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    @SuppressLint("MissingPermission")
    public String getPhoneNumber() {
        TelephonyManager mgr = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNum;
        phoneNum = mgr.getLine1Number().replace("+82", "0");
        Log.d(TAG,phoneNum);
        return phoneNum;
    }

}

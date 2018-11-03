package cspmbilelab.com.newsnailmail;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.net.HttpCookie;

import cspmbilelab.com.newsnailmail.httpprotocol.HttpProtocol;
import cspmbilelab.com.newsnailmail.httpprotocol.PersistentCookieStore;
import cspmbilelab.com.newsnailmail.httpprotocol.SessionControl;
import cspmbilelab.com.newsnailmail.splash.NewSnailMailSplashActivity;

//import java.net.CookieManager;

//import android.webkit.CookieManager;



public class NewSnailMailMainActivity extends Activity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static String TAG = "** NewSnailMail**";

    private HttpProtocol mSnailMailHttpProtocol;

    private final static int MY_PERMISSION_ACCESS_FINE_LOCATION = 33;
    private final static int MY_PERMISSION_READ_PHONE_STATE = 11;

    private WebView mMainWebView;
    private WebSettings mWebSettings;

    private int mPermissionCheck;
    private int mPermissionCheckReadPhone;
    private Location mLocation;
    private LocationManager lm = null;

    private String mProvider = null;
    private String url;
    private CookieManager cookieManager;
    private String mCookie;

    private SessionControl mSesstionControl;
    private PersistentCookieStore mPersistentCookieStore;
    private HttpCookie mHttpCookie;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private long m_endTime;
    private long m_startTime;
    private boolean m_isPressedBackButton = false;

    private ConnectivityManager cManager;
    private NetworkInfo mobile;
    private NetworkInfo wifi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        cManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        mobile = cManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        wifi = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if(mobile==null)
        {
            if(wifi.isConnected())
            {

            }
            else
            {
                Toast.makeText(this, "인터넷 연결이 원활하지 않습니다\n 확인 후 다시 시도 하여 주십시오.", Toast.LENGTH_SHORT).show();
                finish();

            }
        }
        else if(mobile !=null && wifi !=null)
        {
            if(mobile.isConnected() || wifi.isConnected())
            {

            }
            else
            {
                Toast.makeText(this, "인터넷 연결이 원활하지 않습니다\n 확인 후 다시 시도 하여 주십시오.", Toast.LENGTH_SHORT).show();
                finish();


            }
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().setStatusBarColor(Color.BLACK);
        }


        pref = getSharedPreferences("pref",MODE_PRIVATE);
        editor = pref.edit();

        String strAgreement = "";
        String strAgeement2="";

        strAgreement =pref.getString("Accept","");
        strAgeement2=pref.getString("Accept2","");

        Log.d(TAG,"strAgreement:"+strAgreement);


        if(strAgreement.equals("") || strAgreement.equals("아니요")) {
            startActivity(new Intent(this, AgreeinfoActivity.class));
        }
        else
        {
            startActivity(new Intent(this, NewSnailMailSplashActivity.class)); //스플래쉬 액티비티 호출
        }

/*
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("휴대폰번호 수집 동의서");

        alertDialogBuilder
                .setMessage("달팽이편지 로그인을 위해 사용자의 \n휴대폰번호를 서버로 전송후 저장합니다\n이에 동의하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("동의",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editor.putString("Accept2", "예");
                                editor.commit();
                                LoadUrl();
                            }
                        })
                .setNegativeButton("동의 안함",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();


        if(strAgeement2.equals("") || strAgeement2.equals("아니요"))
        {
            alertDialog.show();
        }
*/

        setContentView(R.layout.activity_new_snail_mail_main);

        mMainWebView = (WebView) findViewById(R.id.snail_mail_webview);
        mMainWebView.setWebViewClient(new WebViewClient());

        mWebSettings = mMainWebView.getSettings();
        mWebSettings.setSaveFormData(true); // 폼 입력 값 저장 여부
        mWebSettings.setJavaScriptEnabled(true); // 자바스크립트 사용 여부
        mMainWebView.addJavascriptInterface(new WebAppInterface(NewSnailMailMainActivity.this), "Android");
        mWebSettings.setGeolocationEnabled(true); // 웹뷰내의 위치 정보 사용 여부

        if(strAgeement2.equals("예") )
        {
            url = new String("https://www.snailmail.co.kr/app_index.php?p="+  getPhoneNumber());
            mMainWebView.loadUrl(url);
        }





        // if (BuildSDK_INT.VERSION. > Build.VERSION_CODES.LOLLIPOP) {

        mPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        mPermissionCheckReadPhone= ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (mPermissionCheck == PackageManager.PERMISSION_DENIED || mPermissionCheckReadPhone== PackageManager.PERMISSION_DENIED) {
            Log.d(TAG, "권한없음");
           checkPermissionReadPhone();

        } else {
            Log.d(TAG, "권한있음");
            //getLocationInfo();
        }
        // }


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);





        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //(toolbar);

         /*  // 메일 아이콘 비활성화
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*
        MobileAds.initialize(this,"cca-app-pub-2194597750824626/3175553835");
        AdView adView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
                /*
                .addTestDevice("077FCF257EB9A5D52D9437B748D1AC7A").addTestDevice("0BC32AAA78149A3488066604087EFFF3").addTestDevice("CF045DC7881C3ADD5D773B1611594A44")
                .addTestDevice("02B62AECB74612C9E4D34084C6E12766").addTestDevice("D482D5F97E8A1EDF5BB078BE993BA2BC").addTestDevice("7366F6978DEEC615119ABE06AF0F0117")
                .build();

        adView.loadAd(adRequest);
        */
       //addTestDevice(AdRequest.DEVICE_ID_EMULATOR)



    }

    public class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_snail_mail_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return false;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        String url="";

        if (id == R.id.nav_home) {
             url = "https://www.snailmail.co.kr/app_index.php";
        } else if (id == R.id.nav_letter) {
            url = "https://www.snailmail.co.kr/html/send.php?bo_table=letter";
        } else if (id == R.id.nav_card) {
           /*
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("달팽이편지");
            alertDialogBuilder
                    .setMessage("\"WHITE DAY\" 이벤트가 종료 되었습니다")
                    .setCancelable(false)
                    .setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            url = "https://www.snailmail.co.kr/app_index.php";
            */
            url = "https://www.snailmail.co.kr/bbs/board.php?bo_table=card_list";
        } else if (id == R.id.nav_card2) {
                url = "https://www.snailmail.co.kr/bbs/board.php?bo_table=con_list";
        } else if (id == R.id.nav_postcard) {
            url = "https://www.snailmail.co.kr/html/send.php?bo_table=postcard";
        }else if (id == R.id.nav_postoffice) {
            url ="https:/www.snailmail.co.kr/html/postbox.php";
        }else if(id== R.id.nav_add_change)
        {
            url ="https://www.snailmail.co.kr/bbs/register_p4.php?type=m";
        }else if(id== R.id.nav_contact_us)
        {
            url = "https://www.snailmail.co.kr/html/center.php";
        }
        else if(id== R.id.nav_bank_info)
        {
            url = "https://www.snailmail.co.kr/html/bank_info.php";
        }
        else if(id==R.id.nav_military)
        {
            url = "https://www.snailmail.co.kr//bbs/board.php?bo_table=soldier_list";
            /*
            Builder alertDialogBuilder = new Builder(this);
            alertDialogBuilder.setTitle("달팽이편지");
            alertDialogBuilder
                    .setMessage("서비스 준비중 입니다")
                    .setCancelable(false)
                    .setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            url = "https://www.snailmail.co.kr/app_index.php";
            */
        }

        mMainWebView.loadUrl(url);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {


      //  if ((keyCode == KeyEvent.KEYCODE_BACK) && mMainWebView.canGoBack()) {


          // Log.d(TAG,mMainWebView.getUrl());
            String swap = "";
            swap = mMainWebView.getUrl();


           if("https://www.snailmail.co.kr/app_index.php".equals(swap.substring(0,41)))
           {
               Log.d(TAG,"main");

               if(keyCode==KeyEvent.KEYCODE_BACK )
               {
                   Log.d(TAG,"KEYCODE_BACK");

                   m_endTime = System.currentTimeMillis();

                   if (m_endTime - m_startTime > 2000)
                       m_isPressedBackButton = false;

                   if (m_isPressedBackButton == false) {
                       m_isPressedBackButton = true;

                       m_startTime = System.currentTimeMillis();
                       Toast.makeText(this, "뒤로가기 버튼을 한번 더 누르시면\n종료됩니다.", Toast.LENGTH_SHORT).show();
                   } else {
                       finish();  
                       System.exit(0);
                       android.os.Process.killProcess(android.os.Process.myPid());
                   }
               }
           }
           else if (swap.length() >= 52)
           {
               if ("https://www.snailmail.co.kr/html/card_addr.php?wr_id".equals(swap.substring(0,52))) {
                   Log.d(TAG, "goBack NONo");
                   return true;
               }
               else
                   mMainWebView.goBack();
           }
           else if("https://www.snailmail.co.kr/html/postbox.php".equals(swap.substring(0,44)))
          {
              Log.d(TAG,"post_box");
              LoadUrl();
          }
           else {
               Log.d(TAG,"goBack()");
               mMainWebView.goBack();
           }
            return true;
       // }

        //return super.onKeyDown(keyCode, event);
        /*
        if(keyCode==KeyEvent.KEYCODE_BACK  )
        {

            m_endTime = System.currentTimeMillis();

            if (m_endTime - m_startTime > 2000)
                m_isPressedBackButton = false;

            if (m_isPressedBackButton == false) {
                m_isPressedBackButton = true;

                m_startTime = System.currentTimeMillis();
                Toast.makeText(this, "뒤로가기 버튼을 한번 더 누르시면\n종료됩니다.", Toast.LENGTH_SHORT).show();
            }
            else {
                finish();
                System.exit(0);
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }

        return false;
        */
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onResume() {
        //Activity LifrCycle 관련 메서드는 무조건 상위 메서드 호출 필요
        super.onResume();
    }

    /** 다른 화면으로 넘어갈 때, 일시정지 처리*/
    @Override
    public void onPause() {
        //Activity LifrCycle 관련 메서드는 무조건 상위 메서드 호출 필요
        super.onPause();


    }

    protected void onDestroy() {
        super.onDestroy();
 
    }




    public static boolean isPermissionGranted(String permission, Context c) {
        //int res = ContextCompat.checkSelfPermission(context, permission);
        return (ContextCompat.checkSelfPermission(c, permission) == PackageManager.PERMISSION_GRANTED);
    }

    private void checkPermission() {

        // Activity에서 실행하는경우
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED) {

            // 이 권한을 필요한 이유를 설명해야하는가?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_CONTACTS)) {

                // 다이어로그같은것을 띄워서 사용자에게 해당 권한이 필요한 이유에 대해 설명합니다
                // 해당 설명이 끝난뒤 requestPermissions()함수를 호출하여 권한허가를 요청해야 합니다

            } else {


                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSION_ACCESS_FINE_LOCATION);

                // 필요한 권한과 요청 코드를 넣어서 권한허가요청에 대한 결과를 받아야 합니다

            }
        }

    }
    private void checkPermissionReadPhone()
    {
        // Activity에서 실행하는경우
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED) {

            // 이 권한을 필요한 이유를 설명해야하는가?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_CONTACTS)) {

                // 다이어로그같은것을 띄워서 사용자에게 해당 권한이 필요한 이유에 대해 설명합니다
                // 해당 설명이 끝난뒤 requestPermissions()함수를 호출하여 권한허가를 요청해야 합니다

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSION_READ_PHONE_STATE);

                // 필요한 권한과 요청 코드를 넣어서 권한허가요청에 대한 결과를 받아야 합니다

            }

        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_ACCESS_FINE_LOCATION:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // 권한 허가
                    // 해당 권한을 사용해서 작업을 진행할 수 있습니다
                    Log.d(TAG, "권한 허용");

                }
                else
                {
                    Log.d(TAG, "권한 거부");
                    // 사용자가 해당권한을 거부했을때 해주어야 할 동작을 수행합니다
                    Toast.makeText(this, " 앱을 종료합니다", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            }
            case MY_PERMISSION_READ_PHONE_STATE:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 권한 허가
                    // 해당 권한을 사용해서 작업을 진행할 수 있습니다
                    Log.d(TAG,"폰번호 권한 허용");
                    getPhoneNumber();

                    /*
                    if(getPhoneNumber()=="")
                    {
                         Toast.makeText(this, " 통신사에 등록된  단말기가 아닙니다 \n 앱을 종료합니다", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    */


                    LoadUrl();
                    checkPermission();

                } else {
                    Log.d(TAG,"권한 거부");
                    // 사용자가 해당권한을 거부했을때 해주어야 할 동작을 수행합니다
                    Toast.makeText(this, " 앱을 종료합니다", Toast.LENGTH_SHORT).show();
                    finish();
                }

                break;
            }

        }
    }

    @SuppressLint("MissingPermission")

    public String getPhoneNumber() {
        TelephonyManager mgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNum ="";

        try {
            if (mgr.getLine1Number() != null) {
                phoneNum = mgr.getLine1Number();
                phoneNum = mgr.getLine1Number().replace("+82", "0");
            }
            else {
                if (mgr.getSimSerialNumber() != null) {
                    phoneNum = mgr.getSimSerialNumber();
                    phoneNum = mgr.getLine1Number().replace("+82", "0");
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }


        Log.d(TAG,phoneNum);
        return phoneNum;
    }

    public void LoadUrl()
    {
        Log.d(TAG,"LoadUrl()");
        String url = new String("https://www.snailmail.co.kr/app_index.php?p="+ getPhoneNumber());

        pref = getSharedPreferences("pref",MODE_PRIVATE);
        editor = pref.edit();

        String strAgeement2="";

        strAgeement2=pref.getString("Accept2","");

        if(strAgeement2.equals("예") )
        {
            mMainWebView.loadUrl(url);
            mMainWebView.refreshDrawableState();
        }

    }


    private void connectWebView() {
        // 이제 쿠키에서 해당 url의 세션정보를 갖고 있습니다.
        // 웹뷰를 로딩하면 됩니다.

        url = new String("https://www.snailmail.co.kr/app_index.php?p="+  getPhoneNumber());

        mMainWebView.loadUrl(url);

    }


}


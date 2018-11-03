package cspmbilelab.com.newsnailmail;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class AgreeinfoActivity extends Activity implements View.OnClickListener {

    private final static String TAG = "** Agreeinfo **";
    private Intent intent;

    private ImageButton mAcceptBtn;
    private ImageButton mDenyBtn;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        pref = getSharedPreferences("pref", MODE_PRIVATE);
        editor = pref.edit();

        String strAgreement = "";
        String strAgeement2="";

        strAgreement =pref.getString("Accept","");
        strAgeement2=pref.getString("Accept2","");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("사용자정보 수집 동의서");

        alertDialogBuilder
                .setMessage("본 서비스 사용을  위해 이용자의 \n휴대폰번호 및 주소정보를 서버로\n전송후 저장합니다\n이에 동의하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("동의",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editor.putString("Accept2", "예");
                                editor.commit();

                            }
                        })
                .setNegativeButton("동의 안함",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                finish();
                               // System.exit(0);
                                android.os.Process.killProcess(android.os.Process.myPid());
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();


        if(strAgeement2.equals("") || strAgeement2.equals("아니요"))
        {
            alertDialog.show();
        }
        setContentView(R.layout.activity_agreeinfo);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mAcceptBtn = (ImageButton)findViewById(R.id.imageButton3);
        mDenyBtn = (ImageButton)findViewById(R.id.imageButton4);

        mAcceptBtn.setOnClickListener(this);
        mDenyBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.imageButton3:
                //intent.putExtra("Accept", "예");
                editor.putString("Accept", "예");
                editor.commit();
                //startActivity(new Intent(AgreementGps.this, MainActivity.class));
                Log.d(TAG,"동의함");
                break;

            case R.id.imageButton4:
                editor.putString("Accept", "아니요");
                editor.commit();

                Log.d(TAG,"동의않함");
                break;
        }

        //setResult(RESULT_OK, intent);
       // startActivity(new Intent(AgreeinfoActivity.this, NewSnailMailMainActivity.class));
        finish();
    }




}

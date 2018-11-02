package cspmbilelab.com.newsnailmail;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;

public class ContactUsActivity extends Activity implements View.OnClickListener {

    private ImageButton mConfirmBtn;


    private WebView mMainWebView;
    private WebSettings mWebSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_contact_us);



        mConfirmBtn = (ImageButton)findViewById(R.id.confirm_btn);
        mConfirmBtn.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm_btn:
                finish();
        }

    }

}

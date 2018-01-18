package android.rn.com.barcode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


public class SplashActivity extends BaseActivity {

    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        activity = SplashActivity.this;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (activity == null) {
                    return;
                }
                Intent myProfileIntent = new Intent(activity, HomeActivity.class);
                startActivity(myProfileIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();

            }
        }, 2000);

    }
}

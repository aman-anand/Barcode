package android.rn.com.barcode.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.rn.com.barcode.R;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;

/**
 * Created by DELL on 1/10/2018.
 */

public class App extends Application implements Application.ActivityLifecycleCallbacks, AdCallback {
//    Handler handler = new Handler();
    boolean flag = false;
    Context context;
    private InterstitialAd mInterstitialAd;
    Timer timer;
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        context = this;
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        log(activity.getClass().getSimpleName() + "created");
        MobileAds.initialize(this,
                activity.getResources().getString(R.string.admob_app_id));

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(activity.getResources().getString(R.string.adsUnitID));

        final Handler handler = new Handler();
        timer= new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            flag = true;
                            mInterstitialAd.loadAd(new AdRequest.Builder().build());
                            final AdCallback finalAdCallback = (AdCallback) context;
                            mInterstitialAd.setAdListener(new AdListener() {
                                @Override
                                public void onAdClosed() {
                                    super.onAdClosed();
                                    flag=true;
                                }

                                @Override
                                public void onAdFailedToLoad(int i) {
                                    super.onAdFailedToLoad(i);
//                                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
                                }

                                @Override
                                public void onAdLeftApplication() {
                                    super.onAdLeftApplication();
                                }

                                @Override
                                public void onAdOpened() {
                                    super.onAdOpened();

                                }

                                @Override
                                public void onAdLoaded() {
                                    super.onAdLoaded();

                                    finalAdCallback.loaded();


                                }

                                @Override
                                public void onAdClicked() {
                                    super.onAdClicked();
                                }

                                @Override
                                public void onAdImpression() {
                                    super.onAdImpression();
                                }
                            });

                            //your method here
                        } catch (Exception e) {
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 90000);

    }

    private void log(String s) {
        Log.e("app", s);
//        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityStarted(Activity activity) {
        log(activity.getClass().getSimpleName() + "started");
    }

    @Override
    public void onActivityResumed(Activity activity) {
        log(activity.getClass().getSimpleName() + "resumed");
        if (activity.getClass().getSimpleName().equals("AdActivity")){
//            mInterstitialAd.
            activity.finish();
        }

    }

    @Override
    public void onActivityPaused(Activity activity) {
        log(activity.getClass().getSimpleName() + "paused");
    }

    @Override
    public void onActivityStopped(Activity activity) {
        log(activity.getClass().getSimpleName() + "stopped");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        log(activity.getClass().getSimpleName() + "destroyed");
        timer.cancel();

    }

    @Override
    public void loaded() {
        if (flag) {
            mInterstitialAd.show();
        }
        flag = false;
    }

}

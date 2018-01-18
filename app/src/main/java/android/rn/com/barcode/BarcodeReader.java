package android.rn.com.barcode;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.rn.com.barcode.barcodeutils.ZXingScannerView;
import android.rn.com.barcode.base.AppConstants;
import android.rn.com.barcode.base.bean.BarcodeBean;
import android.rn.com.barcode.base.bean.Bimage;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.Result;
import com.google.zxing.WriterException;

import io.realm.Realm;
import io.realm.RealmResults;
import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.core.ViewFinderView;

/**
 * Created by DELL on 11/2/2016.
 */


public class BarcodeReader extends BaseActivity implements ZXingScannerView.ResultHandler {
    private static final String FLASH_STATE = "FLASH_STATE";
    @SuppressLint("InlinedApi")
    public String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
    public int PERMISSION_ALL = 1;
    public TextView takePermissionText;
    public RelativeLayout permissionLayout;
    private Activity activity;
    private ZXingScannerView mScannerView;
    private Button flashButton;
    private boolean mFlash;
    Realm realm ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_reader);
        realm=Realm.getDefaultInstance();
        activity = BarcodeReader.this;
        getSupportActionBar().setTitle("Barcode Reader");
        flashButton = (Button) findViewById(R.id.flashButton);
        boolean hasFlash = this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (!hasFlash) {
            flashButton.setVisibility(View.GONE);
        }
        flashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFlash = !mFlash;
                mScannerView.setFlash(mFlash);
            }
        });

        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(activity) {
            @Override
            protected IViewFinder createViewFinderView(Context context) {
                return new CustomViewFinderView(context);
            }
        };
        contentFrame.addView(mScannerView);
//        contentFrame.addView(overlapView);

        permissionLayout = (RelativeLayout) findViewById(R.id.permissionLayout);
        takePermissionText = (TextView) findViewById(R.id.takePermissionText);
        takePermissionText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allowPermission();
            }
        });
        allowPermission();
    }

    private void allowPermission() {
        launchFragment();
        if (!launchFragment()) {
            ActivityCompat.requestPermissions(activity, permissions, PERMISSION_ALL);
        }
    }

    public boolean launchFragment() {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void handleResult(final Result rawResult) {
        makeText( "Contents = " + rawResult.getText() +
                ", Format = " + rawResult.getBarcodeFormat().toString());
        BarcodeBean bean = new BarcodeBean();
        bean.setId(Utils.getNextId(BarcodeBean.class));
        bean.setType(rawResult.getBarcodeFormat().name());
        bean.setData(rawResult.getText());

        Bimage bimage;
        try {
            Bitmap bitmap=Utils.encodeAsBitmap(rawResult.getText(),rawResult.getBarcodeFormat(),900,900);
            if (bitmap!=null) {
                bimage = Utils.saveImage(bitmap, activity, Utils.getTime());
                bean.setImageName(bimage.getName());
                bean.setImagePath(bimage.getPath());
            }else{
                bean.setImageName("");
                bean.setImagePath("");
            }
        } catch (WriterException e) {
            e.printStackTrace();
            bean.setImageName("");
            bean.setImagePath("");
        }

        bean.setScanned(true);

//        realm.beginTransaction();
//        realm.copyToRealm(bean);
//        realm.commitTransaction();
        Intent data = new Intent(activity, ResultHandler.class);
        data.putExtra(AppConstants.TYPE, AppConstants.BARCODE);
        data.putExtra(AppConstants.BARCODE,bean);

        startActivity(data);

//        realm.executeTransactionAsync(new Realm.Transaction() {
//            @Override
//            public void execute(@NonNull Realm realm) {
//                BarcodeBean bean = realm.createObject(BarcodeBean.class);
//                bean.setId(Utils.getNextId(BarcodeBean.class));
//                bean.setType(rawResult.getBarcodeFormat().name());
//                bean.setData(rawResult.getText());
//            }
//        }
//        , new Realm.Transaction.OnError() {
//            @Override
//            public void onError(@NonNull Throwable error) {
//                makeText(error.getMessage());
//            }
//        }
//        );
        RealmResults<BarcodeBean> results=realm.where(BarcodeBean.class).findAll();
//        if (results.size()>0){
//            for (int i = 0; i < results.size(); i++) {
//                Log.e("Result", String.valueOf(results.last().getId()));
//                Log.e("Result2", results.last().getData());
//                Log.e("Result3", results.last().getType());
//                Log.e("Result4", results.last().getImage().getName());
//            }
//        }

//
//        // Note:
//        // * Wait 2 seconds to resume the preview.
//        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
//        // * I don't know why this is the case but I don't have the time to figure out.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(BarcodeReader.this);
            }
        }, 2000);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != PERMISSION_ALL) {
            Log.d("OLA", "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            finish();
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("OLA", "Camera permission granted - initialize the camera source");
            // We have permission, so create the camerasource

            return;
        }


        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                finish();
            }
        };
        DialogInterface.OnClickListener listener1 = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error ")
                .setMessage(R.string.no_camera_permission)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, listener)
                .setNeutralButton(getString(R.string.cancle),listener1)
                .show();
    }


    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mScannerView.stopCamera();
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, mFlash);
    }

    /**
     * Class for Barcode Library class
     */
    private static class CustomViewFinderView extends ViewFinderView {
        public static final String TRADE_MARK_TEXT = "";
        public static final int TRADE_MARK_TEXT_SIZE_SP = 40;
        public final Paint PAINT = new Paint();

        public CustomViewFinderView(Context context) {
            super(context);
            init();
        }

        public CustomViewFinderView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        private void init() {
            PAINT.setColor(Color.WHITE);
            PAINT.setAntiAlias(true);
            float textPixelSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                    TRADE_MARK_TEXT_SIZE_SP, getResources().getDisplayMetrics());
            PAINT.setTextSize(textPixelSize);
            setSquareViewFinder(false);
        }

        @Override
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            drawTradeMark(canvas);
        }

        private void drawTradeMark(Canvas canvas) {
            Rect framingRect = getFramingRect();
            float tradeMarkTop;
            float tradeMarkLeft;
            if (framingRect != null) {
                tradeMarkTop = framingRect.bottom + PAINT.getTextSize() + 10;
                tradeMarkLeft = framingRect.left;
            } else {
                tradeMarkTop = 10;
                tradeMarkLeft = canvas.getHeight() - PAINT.getTextSize() - 10;
            }
            canvas.drawText(TRADE_MARK_TEXT, tradeMarkLeft, tradeMarkTop, PAINT);
        }
    }
}

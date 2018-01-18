package android.rn.com.barcode;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.rn.com.barcode.ocr.OcrCaptureActivity;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;

public class HomeActivity extends BaseActivity {
    public String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
    public int PERMISSION_ALL = 1;

    private Activity activity;
    private RelativeLayout readerLayout, generatorLayout, nfcLayout, ocrLayout;
    private static final int RC_OCR_CAPTURE = 9003;
    private FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        activity = HomeActivity.this;
        getSupportActionBar().setTitle(getString(R.string.app_name));

        init();
        allowPermission();
        readerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, BarcodeReader.class);
                startActivity(intent);
            }
        });

        generatorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, SelectCodeType.class);
                startActivity(intent);
            }
        });

        nfcLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm = activity.getPackageManager();
                if (pm.hasSystemFeature(PackageManager.FEATURE_NFC)) {
                    Intent intent = new Intent(activity, NfcActivity.class);
                    startActivity(intent);
                } else {
                    Dialog errorDialog = Utils.dialogMessage(getString(R.string.msgText), activity, getString(R.string.nfcNotSupportedText));
                    errorDialog.show();
                }

            }
        });

        ocrLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, OcrCaptureActivity.class);
                intent.putExtra(OcrCaptureActivity.AutoFocus, true);
                intent.putExtra(OcrCaptureActivity.UseFlash, false);
//                startActivityForResult(intent, RC_OCR_CAPTURE);
                startActivity(intent);
            }
        });

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
    private void init() {
        fab=findViewById(R.id.fab);
        readerLayout = (RelativeLayout) findViewById(R.id.readerLayout);
        generatorLayout = (RelativeLayout) findViewById(R.id.generatorLayout);
        nfcLayout = (RelativeLayout) findViewById(R.id.nfcLayout);
        ocrLayout = (RelativeLayout) findViewById(R.id.ocrLayout);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity,ListActivity.class));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RC_OCR_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    String text = data.getStringExtra(OcrCaptureActivity.TextBlockObject);
                    Toast.makeText(activity,""+text,Toast.LENGTH_SHORT).show();
//                    statusMessage.setText(R.string.ocr_success);
//                    textValue.setText(text);
//                    Log.d(TAG, "Text read: " + text);
                } else {
                    Toast.makeText(activity,"ocr_failure",Toast.LENGTH_SHORT).show();
//                    statusMessage.setText(R.string.ocr_failure);
//                    Log.d(TAG, "No Text captured, intent data is null");
                }
            } else {
                Toast.makeText(activity,"ocr_error",Toast.LENGTH_SHORT).show();
//                statusMessage.setText(String.format(getString(R.string.ocr_error),
//                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}

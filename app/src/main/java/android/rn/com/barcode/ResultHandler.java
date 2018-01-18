package android.rn.com.barcode;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.rn.com.barcode.base.AppConstants;
import android.rn.com.barcode.base.bean.BarcodeBean;
import android.rn.com.barcode.base.bean.Bimage;
import android.rn.com.barcode.base.bean.NfcBean;
import android.rn.com.barcode.base.bean.OcrBean;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import io.realm.Realm;
import io.realm.RealmResults;

public class ResultHandler extends BaseActivity {
    private AlertDialog.Builder builder;
    private Realm realm;
    private LinearLayout dataLay, typeLay, decLay;
    private TextView d_data, d_type,headerTitle;
    private boolean calledFromGenerator;
    private EditText ocrDataET, descET;
    private ImageView imageView, saveImg, backImg, delImg, shareImg;
    private Context context;
    private BarcodeBean barcodeBean;
    private OcrBean ocrBean;
    private NfcBean nfcBean;
    private String choiceOption = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_layout);
        realm = Realm.getDefaultInstance();
        init();


    }

    public void setDataToViews(String option) {
        switch (option) {
            case AppConstants.BARCODE:
                if (barcodeBean != null) {
                    d_data.setText(barcodeBean.getData());
                    d_type.setText(barcodeBean.getType());
                    if (!TextUtils.isEmpty(barcodeBean.getDesc())) {
                        descET.setText(barcodeBean.getDesc());
                    }
                    if (barcodeBean.getImage() != null && !TextUtils.isEmpty(barcodeBean.getImageName())) {
                        imageView.setImageBitmap(loadImageFromStorage(barcodeBean.getImage()));
                    } else {
                        hide(imageView);
                    }
                }
                break;
            case AppConstants.OCR:
                if (ocrBean != null) {
                    ocrDataET.setText(ocrBean.getData());
                }
                assert ocrBean != null;
                if (!TextUtils.isEmpty(ocrBean.getDesc())) {
                    descET.setText(ocrBean.getDesc());
                }
                break;
            case AppConstants.NFC:
                if (nfcBean != null) {
                    ocrDataET.setText(nfcBean.getData());
                    ocrDataET.setEnabled(false);
                }
                if (!TextUtils.isEmpty(nfcBean.getDesc())) {
                    descET.setText(nfcBean.getDesc());
                }
                break;
        }
    }

    private void init() {
        context = ResultHandler.this;
        dataLay = findViewById(R.id.dataLayout);
        typeLay = findViewById(R.id.typeL);
        decLay = findViewById(R.id.descL);
        d_data = findViewById(R.id.dataET);
        d_type = findViewById(R.id.serial);
        imageView = findViewById(R.id.image);
        ocrDataET = findViewById(R.id.ocrData);
        descET = findViewById(R.id.desc);
        headerTitle = findViewById(R.id.headerTitle);
        saveImg = findViewById(R.id.saveImg);
        backImg = findViewById(R.id.backImg);
        delImg = findViewById(R.id.deleteImg);
        shareImg = findViewById(R.id.shareImg);
        builder = new AlertDialog.Builder(context);
        if (getIntent().hasExtra(AppConstants.TYPE)) {
            String type = getIntent().getStringExtra(AppConstants.TYPE);
            calledFromGenerator = true;
            assert type != null;
            switch (type) {
                case AppConstants.BARCODE:
                    changeUI(AppConstants.BARCODE);
                    choiceOption = AppConstants.BARCODE;
                    barcodeBean = getIntent().getParcelableExtra(AppConstants.BARCODE);
                    setDataToViews(AppConstants.BARCODE);
                    break;
                case AppConstants.OCR:
                    changeUI(AppConstants.OCR);
                    choiceOption = AppConstants.OCR;
                    ocrBean = getIntent().getParcelableExtra(AppConstants.OCR);
                    setDataToViews(AppConstants.OCR);
                    break;
                case AppConstants.NFC:
                    changeUI(AppConstants.NFC);
                    choiceOption = AppConstants.NFC;
                    nfcBean = getIntent().getParcelableExtra(AppConstants.NFC);
                    setDataToViews(AppConstants.NFC);
                    break;
            }
        } else if (getIntent().hasExtra(AppConstants.UPDATE)) {
            String type = getIntent().getStringExtra(AppConstants.UPDATE);
            long id = getIntent().getLongExtra(AppConstants.ID, 1);
            calledFromGenerator = false;
            assert type != null;
            switch (type) {
                case AppConstants.BARCODE:
                    changeUI(AppConstants.BARCODE);
                    choiceOption = AppConstants.BARCODE;
                    barcodeBean = getBarcodeResults(id);
                    setDataToViews(AppConstants.BARCODE);
                    break;
                case AppConstants.OCR:
                    changeUI(AppConstants.OCR);
                    choiceOption = AppConstants.OCR;
                    ocrBean = getOcrResults(id);
                    setDataToViews(AppConstants.OCR);
                    break;
                case AppConstants.NFC:
                    changeUI(AppConstants.NFC);
                    choiceOption = AppConstants.NFC;
                    nfcBean = getNfcResults(id);
                    setDataToViews(AppConstants.NFC);
                    break;
            }
        } else {
            makeText("ERROR");
            finish();
        }
        if (calledFromGenerator) {
            shareImg.setAlpha(0.3f);
            delImg.setAlpha(0.3f);
        } else {
            shareImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    switch (choiceOption) {
                        case AppConstants.BARCODE:
                            if (barcodeBean.getImage() != null && !TextUtils.isEmpty(barcodeBean.getImageName())) {
                                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                    Bitmap bitmap = loadImageFromStorage(barcodeBean.getImage());
                                    shareImg(bitmap, barcodeBean.getImageName());
                                } else {
                                    builder=new AlertDialog.Builder(context);
                                    builder.setTitle(R.string.message)
                                            .setMessage(getResources().getString(R.string.err_storage))
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                                    intent.setData(uri);
                                                    startActivity(intent);
                                                }
                                            })
                                            .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }).show();
                                }
                            } else {
                                shareText("Barcode", barcodeBean.toString());
                            }
                            break;
                        case AppConstants.OCR:
                            shareText(AppConstants.OCR, ocrBean.toString());
                            break;
                        case AppConstants.NFC:
                            shareText(AppConstants.NFC, nfcBean.toString());
                            break;
                    }
                }
            });
            delImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.hideKeyboard(ResultHandler.this, descET);
                    final RealmResults[] results = new RealmResults[1];
                    builder=new AlertDialog.Builder(context);
                    builder.setTitle(R.string.message)
                            .setMessage(getResources().getString(R.string.mesage_confirmation_delete_item))
                            .setCancelable(false)
                            .setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    realm.beginTransaction();
                                    switch (choiceOption) {
                                        case AppConstants.BARCODE:
                                            results[0] = realm.where(BarcodeBean.class).equalTo(BarcodeBean.ID, barcodeBean.getId()).findAll();
                                            if (results[0].deleteAllFromRealm()) finish();
                                            break;
                                        case AppConstants.OCR:
                                            results[0] = realm.where(OcrBean.class).equalTo(BarcodeBean.ID, ocrBean.getId()).findAll();
                                            if (results[0].deleteAllFromRealm()) finish();
                                            break;
                                        case AppConstants.NFC:
                                            results[0] = realm.where(NfcBean.class).equalTo(BarcodeBean.ID, nfcBean.getId()).findAll();
                                            if (results[0].deleteAllFromRealm()) finish();
                                            break;
                                    }
                                    realm.commitTransaction();
                                    finish();
                                }
                            }).show();

                }
            });
        }
        saveImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideKeyboard(ResultHandler.this, descET);
                realm.beginTransaction();

                getData(choiceOption);
                switch (choiceOption) {
                    case AppConstants.BARCODE:
                        realm.copyToRealmOrUpdate(barcodeBean);
                        break;
                    case AppConstants.OCR:
                        realm.copyToRealmOrUpdate(ocrBean);
                        break;
                    case AppConstants.NFC:
                        realm.copyToRealmOrUpdate(nfcBean);
                        break;
                }
                realm.commitTransaction();
                builder=new AlertDialog.Builder(context);
                builder.setTitle(R.string.message)
                        .setMessage(getResources().getString(R.string.message_data_saved))
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();

            }
        });

        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.hideKeyboard(ResultHandler.this, descET);
                finish();
            }
        });

        d_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp=d_data.getText().toString().trim();
                if (Patterns.PHONE.matcher(temp).matches()){
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+temp));
                    startActivity(intent);
                }else if (Patterns.WEB_URL.matcher(temp).matches()){
                    try {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        if (temp.startsWith("http://")||temp.startsWith("https://")){
                            i.setData(Uri.parse(temp));
                        }else{
                            i.setData(Uri.parse("https://"+temp));
                        }
                        startActivity(i);
                    }catch (Exception e){

                        return;
                    }
                }else if(Patterns.EMAIL_ADDRESS.matcher(temp).matches()){
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:"));
                    intent.putExtra(Intent.EXTRA_EMAIL  , new String[] { temp });
                    startActivity(Intent.createChooser(intent, "Email via..."));
                }
            }
        });

    }

    private void getData(String type_c) {
        switch (type_c) {
            case AppConstants.BARCODE:
                barcodeBean.setDesc(descET.getText().toString().trim());
                return;
            case AppConstants.OCR:
                ocrBean.setDesc(descET.getText().toString().trim());
//                ocrBean.setName(nameET.getText().toString().trim());
                ocrBean.setData(ocrDataET.getText().toString().trim());
                return;
            case AppConstants.NFC:
                nfcBean.setDesc(descET.getText().toString().trim());
                nfcBean.setData(ocrDataET.getText().toString().trim());
                return;
        }

    }

    private String checkData() {
        String str = descET.getText().toString().trim();
        if (!TextUtils.isEmpty(str)) {
            return str;
        } else {
            return "";
        }
    }

    private void changeUI(String option) {
        switch (option) {
            case AppConstants.BARCODE:
                headerTitle.setText(getResources().getString(R.string.barcode));
                show(dataLay);
                show(typeLay);
                show(descET);
                show(imageView);
                hide(ocrDataET);
                break;
            case AppConstants.OCR:
                headerTitle.setText(getResources().getString(R.string.ocrText));
                hide(dataLay);
                show(descET);
                hide(typeLay);
                hide(imageView);
                show(ocrDataET);
                break;
            case AppConstants.NFC:
                headerTitle.setText(getResources().getString(R.string.nfcText));
                hide(dataLay);
                show(descET);
                hide(typeLay);
                hide(imageView);
                show(ocrDataET);
                break;
        }
    }


    private BarcodeBean getBarcodeResults(long id) {
        RealmResults<BarcodeBean> results = realm.where(BarcodeBean.class).equalTo("id", id).findAll();
        if (results.size() > 0) {
            return results.last();
        } else {
            return null;
        }
    }

    private NfcBean getNfcResults(long id) {
        RealmResults<NfcBean> results = realm.where(NfcBean.class).equalTo("id", id).findAll();
        if (results.size() > 0) {
            return results.last();
        } else {
            return null;
        }
//        return realm.where(NfcBean.class).findAll().last();
    }

    private OcrBean getOcrResults(long id) {
        RealmResults<OcrBean> results = realm.where(OcrBean.class).equalTo("id", id).findAll();
        if (results.size() > 0) {
            return results.last();
        } else {
            return null;
        }

    }

    private Bitmap loadImageFromStorage(Bimage bimage) {
        Bitmap b = null;
        try {
            File f = new File(bimage.getPath(), bimage.getName());
            b = BitmapFactory.decodeStream(new FileInputStream(f));
//            ImageView img=(ImageView)findViewById(R.id.imgPicker);
//            img.setImageBitmap(b);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return b;
    }

    public void shareImg(Bitmap bitmap, String name) {
        String pathofBmp = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, name, null);
        Uri bmpUri = Uri.parse(pathofBmp);
        final Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        shareIntent.setType("image/png");
        startActivity(Intent.createChooser(shareIntent, "Share"));
        ContentResolver contentResolver = getContentResolver();
        contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                MediaStore.Images.ImageColumns.DATA + "=?", new String[]{pathofBmp});
    }

    @SuppressLint("SetWorldReadable")
    private void shareBitmap(Bitmap bitmap, String fileName) {
        try {
            File file = new File(context.getCacheDir(), fileName + ".png");
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();

            boolean b = file.setReadable(true, false);
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("image/png");
            startActivity(Intent.createChooser(intent, "Share"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void shareText(String subject, String body) {
        Intent txtIntent = new Intent(android.content.Intent.ACTION_SEND);
        txtIntent.setType("text/plain");
        txtIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        txtIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(txtIntent, "Share"));
    }
}

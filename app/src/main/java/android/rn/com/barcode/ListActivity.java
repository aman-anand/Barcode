package android.rn.com.barcode;

import android.content.Context;
import android.os.Bundle;
import android.rn.com.barcode.adapter.BarcodeAdapter;
import android.rn.com.barcode.adapter.NfcAdapter;
import android.rn.com.barcode.adapter.OcrAdapter;
import android.rn.com.barcode.base.AppConstants;
import android.rn.com.barcode.base.bean.BarcodeBean;
import android.rn.com.barcode.base.bean.NfcBean;
import android.rn.com.barcode.base.bean.OcrBean;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmResults;

public class ListActivity extends BaseActivity {

    //
    private ImageView backBtn;
    private View indi_scanned, indi_generated, indi_nfc, indi_ocr;
    private TextView empty;
    private int currentTab = 0;
    private ProgressBar progressBar;
    private LinearLayout scanned_lay, generated_lay, nfc_lay, ocr_lay;
    private boolean empty_scanned = false, empty_generated = false, empty_nfc = false, empty_ocr = false;

    private RelativeLayout rootLay;
    private Realm realm;
    private RecyclerView recyclerView;
    private Menu menu;
    private BarcodeAdapter adapter, adapterGenerated;
    private OcrAdapter ocrAdapter;
    private NfcAdapter nfcAdapter;

    //    private TabHost tabHost;
//    private TabHost.TabSpec[] tabSpec = new TabHost.TabSpec[4];
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        realm = Realm.getDefaultInstance();
        setupData();
        init();
        scanned_lay.performClick();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (adapter != null && currentTab == 1 && adapter.getItemCount() == 0) {
            show(empty);
            hide(recyclerView);
        } else if (ocrAdapter != null && currentTab == 2 && ocrAdapter.getItemCount() == 0) {
            show(empty);
            hide(recyclerView);
        } else if (nfcAdapter != null && currentTab == 3 && nfcAdapter.getItemCount() == 0) {
            show(empty);
            hide(recyclerView);
        } else if (adapterGenerated != null && currentTab == 4 && adapterGenerated.getItemCount() == 0) {
            show(empty);
            hide(recyclerView);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recyclerView.setAdapter(null);
        realm.close();
    }

    private void setupData() {
        RealmResults realmResults = realm.where(BarcodeBean.class).equalTo("isScanned", true).findAll();
        if (realmResults.size() > 0) {
//            adapter = new BarcodeAdapter(realm.where(BarcodeBean.class).isEmpty("image.name").findAll());
            adapter = new BarcodeAdapter(realm.where(BarcodeBean.class).equalTo("isScanned", true).findAll());
            empty_scanned = false;
        } else {
            empty_scanned = true;
        }
        realmResults = realm.where(BarcodeBean.class).equalTo("isScanned", false).findAll();
        if (realmResults.size() > 0) {
            adapterGenerated = new BarcodeAdapter(realm.where(BarcodeBean.class).equalTo("isScanned", false).findAll());
            empty_generated = false;
        } else {
            empty_generated = true;
        }
        realmResults = realm.where(OcrBean.class).findAll();
        if (realmResults.size() > 0) {
            ocrAdapter = new OcrAdapter(realm.where(OcrBean.class).findAll());
            empty_ocr = false;
        } else {
            empty_ocr = true;
        }
        realmResults = realm.where(NfcBean.class).findAll();
        if (realmResults.size() > 0) {
            nfcAdapter = new NfcAdapter(realm.where(NfcBean.class).findAll());
            empty_nfc = false;
//            empty.setVisibility(View.GONE);
//            recyclerView.setVisibility(View.VISIBLE);
        } else {
//            empty.setVisibility(View.VISIBLE);
//            recyclerView.setVisibility(View.GONE);
            empty_nfc = true;
        }
//        adapterGenerated = new BarcodeAdapter(realm.where(BarcodeBean.class).isNotEmpty("image.name").findAll());
//        ocrAdapter = new OcrAdapter(realm.where(OcrBean.class).findAll());
//        nfcAdapter = new NfcAdapter(realm.where(NfcBean.class).findAll());

    }

    private void init() {
        context = this;
        scanned_lay = findViewById(R.id.scannedMainLayout);
        generated_lay = findViewById(R.id.generatedMainLayout);
        nfc_lay = findViewById(R.id.nfcMainLayout);
        ocr_lay = findViewById(R.id.ocrMainLayout);

        indi_scanned = findViewById(R.id.scannedView);
        indi_generated = findViewById(R.id.generatedView);
        indi_nfc = findViewById(R.id.nfcView);
        indi_ocr = findViewById(R.id.ocrView);
        backBtn = findViewById(R.id.backImg);

        rootLay = findViewById(R.id.root);
        empty = findViewById(R.id.emptyText);
        recyclerView = findViewById(R.id.recyclerView);
        //setup Adapters
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        scanned_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUi(AppConstants.BARCODE);
                if (empty_scanned) {
                    hide(recyclerView);
                    show(empty);
                } else {
                    hide(empty);
                    show(recyclerView);
                    recyclerView.setAdapter(adapter);
                }

            }
        });
        generated_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUi(AppConstants.GENERATED);
                if (empty_generated) {
                    hide(recyclerView);
                    show(empty);
                } else {
                    hide(empty);
                    show(recyclerView);
                    recyclerView.setAdapter(adapterGenerated);
                }
            }
        });
        nfc_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUi(AppConstants.NFC);
                if (empty_nfc) {
                    hide(recyclerView);
                    show(empty);
                } else {
                    hide(empty);
                    show(recyclerView);
                    recyclerView.setAdapter(nfcAdapter);
                }
            }
        });
        ocr_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUi(AppConstants.OCR);
                if (empty_ocr) {
                    hide(recyclerView);
                    show(empty);
                } else {
                    hide(empty);
                    show(recyclerView);
                    recyclerView.setAdapter(ocrAdapter);
                }
            }
        });

        backBtn = findViewById(R.id.backImg);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        progressBar = findViewById(R.id.progressBar);
    }

    public void changeUi(String option) {
        switch (option) {
            case AppConstants.BARCODE:
                currentTab = 1;
                indi_generated.setBackgroundResource(R.color.colorPrimary);
                indi_nfc.setBackgroundResource(R.color.colorPrimary);
                indi_ocr.setBackgroundResource(R.color.colorPrimary);
                indi_scanned.setBackgroundResource(R.color.colorWhite);
                break;
            case AppConstants.OCR:
                currentTab = 2;
                indi_scanned.setBackgroundResource(R.color.colorPrimary);
                indi_generated.setBackgroundResource(R.color.colorPrimary);
                indi_nfc.setBackgroundResource(R.color.colorPrimary);
                indi_ocr.setBackgroundResource(R.color.colorWhite);

                break;
            case AppConstants.NFC:
                currentTab = 3;
                indi_scanned.setBackgroundResource(R.color.colorPrimary);
                indi_generated.setBackgroundResource(R.color.colorPrimary);
                indi_ocr.setBackgroundResource(R.color.colorPrimary);
                indi_nfc.setBackgroundResource(R.color.colorWhite);

                break;
            case AppConstants.GENERATED:
                currentTab = 4;
                indi_scanned.setBackgroundResource(R.color.colorPrimary);
                indi_nfc.setBackgroundResource(R.color.colorPrimary);
                indi_ocr.setBackgroundResource(R.color.colorPrimary);
                indi_generated.setBackgroundResource(R.color.colorWhite);

                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

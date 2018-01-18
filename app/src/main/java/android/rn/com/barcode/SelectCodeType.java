package android.rn.com.barcode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.rn.com.barcode.adapter.SelectCodeTypeAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by DELL on 11/17/2016.
 */

public class SelectCodeType extends BaseActivity {

    private Activity activity;
    private ListView listView;
    private SelectCodeTypeAdapter selectCodeTypeAdapter;
    private String[] typeName = {"Code128","Code39","Codabar","UPC-A","Aztec Code","QR Code","DataMatrix","EAN-8","EAN-13","ITF","PDF 417"};
    private String[] typeDetail = {"Up to 80 ASCII characters from 0x20 to 0x7E (example: Abc 123)","Up to 43 characters containing only A-Z,1-9,-,.,space,$,/,+,%,* (example: ABC 1)","Up to 1K characters containing only 1234567890-$:/.+. Should starts and finishes with one of A,B,C or D (example: A123$B)",
            "11 or 12 digits(example: 123456789012)","Up to 1K UTF-8 or ISO 8859-1 characters (example: Abc 123)","Up to 1K UTF-8 characters (example: Abc 123)","Up to 1K ASCII characters (example: Abc 123)",
            "8 digits (example: 12345678)","13 digits (example: 123456789012)","Up to 40digits pairs (example: 1234)","Up to 1K ASCII characters (example: Abc123)"};

//    private String[] typeDetail = {getString(R.string.Code128),getString(R.string.Code39),getString(R.string.Codabar),
//            getString(R.string.UPC_A),getString(R.string.AztecCode),getString(R.string.QRCode),getString(R.string.DataMatrix),
//            getString(R.string.EAN_8),getString(R.string.EAN_13),getString(R.string.ITF),getString(R.string.PDF417)};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_code_type);
        activity = SelectCodeType.this;
        getSupportActionBar().setTitle("Select Code Type");
        listView = (ListView) findViewById(R.id.listView);

        selectCodeTypeAdapter = new SelectCodeTypeAdapter(activity, typeName, typeDetail);
        listView.setAdapter(selectCodeTypeAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(activity, BarcodeGenerator.class);
                Bundle bundle = new Bundle();
                bundle.putString("selectCodeTypeName",typeName[position]);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


    }

}

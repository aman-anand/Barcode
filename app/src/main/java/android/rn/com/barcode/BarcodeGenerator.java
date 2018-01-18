package android.rn.com.barcode;

/**
 * Created by DELL on 11/17/2016.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.rn.com.barcode.base.AppConstants;
import android.rn.com.barcode.base.bean.BarcodeBean;
import android.rn.com.barcode.base.bean.Bimage;
import android.support.design.widget.TextInputLayout;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Pattern;

public class BarcodeGenerator extends BaseActivity {

    /**************************************************************
     * getting from com.google.zxing.client.android.encode.QRCodeEncoder
     * <p>
     * See the sites below
     * http://code.google.com/p/zxing/
     * http://code.google.com/p/zxing/source/browse/trunk/android/src/com/google/zxing/client/android/encode/EncodeActivity.java
     * http://code.google.com/p/zxing/source/browse/trunk/android/src/com/google/zxing/client/android/encode/QRCodeEncoder.java
     */


    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;
    public Bundle bundle = null;
    public TextInputLayout editTextLayout;
    String type = "";
    Bitmap bitmap = null;
    private Activity activity;
    private EditText editText;
    private ImageView barcodeImage;
    private TextView generateBarcodeButton;
    private String selectCodeTypeName;

    /**
     * int BLACK = 0xFF000000;
     * int WHITE = 0xFFFFFFFF;
     * <p>
     * // change the values to your needs
     * int requestedWidth = 300;
     * int requestedHeight = 300;
     * <p>
     * int width = bitMatrix.getWidth();
     * int height = bitMatrix.getHeight();
     * <p>
     * // calculating the scaling factor
     * int pixelsize = requestedWidth/width;
     * if (pixelsize > requestedHeight/height)
     * {
     * pixelsize = requestedHeight/height;
     * }
     * <p>
     * int[] pixels = new int[requestedWidth * requestedHeight];
     * // All are 0, or black, by default
     * for (int y = 0; y < height; y++) {
     * int offset = y * requestedWidth * pixelsize;
     * <p>
     * // scaling pixel height
     * for (int pixelsizeHeight = 0; pixelsizeHeight < pixelsize; pixelsizeHeight++, offset+=requestedWidth) {
     * for (int x = 0; x < width; x++) {
     * int color = bitMatrix.get(x, y) ? BLACK : WHITE;
     * <p>
     * // scaling pixel width
     * for (int pixelsizeWidth = 0; pixelsizeWidth < pixelsize; pixelsizeWidth++) {
     * pixels[offset + x * pixelsize + pixelsizeWidth] = color;
     * }
     * }
     * }
     * }
     * Bitmap bitmap = Bitmap.createBitmap(requestedWidth, requestedHeight, Bitmap.Config.ARGB_8888);
     * bitmap.setPixels(pixels, 0, requestedWidth, 0, 0, requestedWidth, requestedHeight);
     *
     * @param contents
     * @return
     */

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_generator);
        activity = BarcodeGenerator.this;
        bundle = this.getIntent().getExtras();
        selectCodeTypeName = bundle.getString("selectCodeTypeName");
        getSupportActionBar().setTitle("Barcode Generator (" + selectCodeTypeName + ")");

        editText = (EditText) findViewById(R.id.editText);
//        editText.setHint("Enter "+selectCodeTypeName);
        setHint();
        editTextLayout = (TextInputLayout) findViewById(R.id.editTextLayout);
        barcodeImage = (ImageView) findViewById(R.id.barcodeImage);
        generateBarcodeButton = (TextView) findViewById(R.id.generateBarcodeButton);

        generateBarcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editTextData = editText.getText().toString();
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                if (editTextData.isEmpty()) {
                    editTextLayout.setError("Field is required");
                    barcodeImage.setImageBitmap(null);
                } else if (selectCodeTypeName.equals("Code39") && !Pattern.matches("[A-Z 1-9 -.$/+%*]+", editTextData)) {
                    editTextLayout.setError(getString(R.string.Code39));
                    barcodeImage.setImageBitmap(null);
                } else if (selectCodeTypeName.equals("Codabar") && !Pattern.matches("[0-9 -.$:/+]+", editTextData)) {
                    editTextLayout.setError(getString(R.string.Codabar));
                    barcodeImage.setImageBitmap(null);
                } else if (selectCodeTypeName.equals("UPC-A") && editTextData.length() < 11) {
                    editTextLayout.setError(getString(R.string.UPC_A));
                    barcodeImage.setImageBitmap(null);
                } else if (selectCodeTypeName.equals("EAN-8") && editTextData.length() < 8) {
                    editTextLayout.setError(getString(R.string.EAN_8));
                    barcodeImage.setImageBitmap(null);
                } else if (selectCodeTypeName.equals("EAN-13") && editTextData.length() < 13) {
                    editTextLayout.setError(getString(R.string.EAN_13));
                    barcodeImage.setImageBitmap(null);
                } else if (selectCodeTypeName.equals("ITF") && editTextData.length() < 13) {
                    editTextLayout.setError(getString(R.string.ITF));
                    barcodeImage.setImageBitmap(null);
                } else {
                    editTextLayout.setError(null);
                    final String barcode_data = editText.getText().toString().trim();


                    try {
                        if (selectCodeTypeName.equals("Code128")) {
                            bitmap = encodeAsBitmap(barcode_data, BarcodeFormat.CODE_128, 600, 300);
                            type = BarcodeFormat.CODE_128.toString();
                        } else if (selectCodeTypeName.equals("Code39")) {
                            bitmap = encodeAsBitmap(barcode_data, BarcodeFormat.CODE_39, 600, 300);
                            type = BarcodeFormat.CODE_39.toString();
                        } else if (selectCodeTypeName.equals("Codabar")) {
                            type = BarcodeFormat.CODABAR.toString();
                            bitmap = encodeAsBitmap(barcode_data, BarcodeFormat.CODABAR, 600, 300);
                        } else if (selectCodeTypeName.equals("UPC-A")) {
                            type = BarcodeFormat.UPC_A.toString();
                            bitmap = encodeAsBitmap(barcode_data, BarcodeFormat.UPC_A, 600, 300);
                        } else if (selectCodeTypeName.equals("Aztec Code")) {
                            type = BarcodeFormat.AZTEC.toString();
                            bitmap = encodeAsBitmap(barcode_data, BarcodeFormat.AZTEC, 800, 800);
                        } else if (selectCodeTypeName.equals("QR Code")) {
                            type = BarcodeFormat.QR_CODE.toString();
                            bitmap = encodeAsBitmap(barcode_data, BarcodeFormat.QR_CODE, 900, 900);
                        } else if (selectCodeTypeName.equals("DataMatrix")) {
                            type = BarcodeFormat.DATA_MATRIX.toString();
                            bitmap = encodeAsBitmap(barcode_data, BarcodeFormat.DATA_MATRIX, 200, 200);
                        } else if (selectCodeTypeName.equals("EAN-8")) {
                            type = BarcodeFormat.EAN_8.toString();
                            bitmap = encodeAsBitmap(barcode_data, BarcodeFormat.EAN_8, 600, 300);
                        } else if (selectCodeTypeName.equals("EAN-13")) {
                            type = BarcodeFormat.EAN_13.toString();
                            bitmap = encodeAsBitmap(barcode_data, BarcodeFormat.EAN_13, 600, 300);
                        } else if (selectCodeTypeName.equals("ITF")) {
                            type = BarcodeFormat.ITF.toString();
                            bitmap = encodeAsBitmap(barcode_data, BarcodeFormat.ITF, 600, 300);
                        } else if (selectCodeTypeName.equals("PDF 417")) {
                            type = BarcodeFormat.PDF_417.toString();
                            bitmap = encodeAsBitmap(barcode_data, BarcodeFormat.PDF_417, 600, 600);
                        }
                        if (bitmap == null) {
                            editTextLayout.setError(getString(R.string.barcodeNotGenerated));
                            barcodeImage.setImageBitmap(null);
                        } else {

//                            barcodeImage.setImageBitmap(bitmap);
//                            Realm realm = Realm.getDefaultInstance();
//                            String image=
                            Bimage finalImage = Utils.saveImage(bitmap, activity, Utils.getTime());
                            BarcodeBean bean = new BarcodeBean();
                            bean.setId(Utils.getNextId(BarcodeBean.class));
                            bean.setType(type);
                            bean.setData(barcode_data);
                            bean.setImage(finalImage);
                            bean.setScanned(false);
//                            realm.beginTransaction();
//                            realm.copyToRealm(bean);
//                            realm.commitTransaction();

                            Intent data = new Intent(activity, ResultHandler.class);
                            data.putExtra(AppConstants.TYPE, AppConstants.BARCODE);
                            data.putExtra(AppConstants.BARCODE, bean);
                            startActivity(data);
                            finish();
                        }

                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private void setHint() {
        if (selectCodeTypeName.equals("Code128")) {
            editText.setHint("Enter Code128");
            setEditTextLength(80);
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
        } else if (selectCodeTypeName.equals("Code39")) {
            setEditTextLength(43);
            editText.setHint("Enter Code39");
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
        } else if (selectCodeTypeName.equals("Codabar")) {
            setEditTextLength(1000);
            editText.setHint("Enter Codabar");
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
        } else if (selectCodeTypeName.equals("UPC-A")) {
            setEditTextLength(12);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setHint("Enter UPC-A");
        } else if (selectCodeTypeName.equals("Aztec Code")) {
            setEditTextLength(1000);
            editText.setHint("Enter Aztec Code");
        } else if (selectCodeTypeName.equals("QR Code")) {
            setEditTextLength(1000);
            editText.setHint("Enter QR Code");
        } else if (selectCodeTypeName.equals("DataMatrix")) {
            setEditTextLength(1000);
            editText.setHint("Enter DataMatrix");
        } else if (selectCodeTypeName.equals("EAN-8")) {
            editText.setHint("Enter EAN-8");
            setEditTextLength(8);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else if (selectCodeTypeName.equals("EAN-13")) {
            setEditTextLength(13);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setHint("Enter EAN-13");
        } else if (selectCodeTypeName.equals("ITF")) {
            setEditTextLength(40);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setHint("Enter ITF");
        } else if (selectCodeTypeName.equals("PDF 417")) {
            setEditTextLength(1000);
            editText.setHint("Enter PDF 417");
//            barcodeImage.setScaleType(ImageView.ScaleType.FIT_XY);
        }
    }

    private void setEditTextLength(int i) {
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(i);
        editText.setFilters(FilterArray);
    }

    public Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {
        String contentsToEncode = contents;
        if (contentsToEncode == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        if (format.equals(BarcodeFormat.DATA_MATRIX)) {
            int requestedWidth = 600;
            int requestedHeight = 600;



            // calculating the scaling factor
            int pixelsize = requestedWidth / width;
            if (pixelsize > requestedHeight / height) {
                pixelsize = requestedHeight / height;
            }

            int[] pixels = new int[requestedWidth * requestedHeight];
            // All are 0, or black, by default
            for (int y = 0; y < height; y++) {
                int offset = y * requestedWidth * pixelsize;

                // scaling pixel height
                for (int pixelsizeHeight = 0; pixelsizeHeight < pixelsize; pixelsizeHeight++, offset += requestedWidth) {
                    for (int x = 0; x < width; x++) {
                        int color = result.get(x, y) ? BLACK : WHITE;

                        // scaling pixel width
                        for (int pixelsizeWidth = 0; pixelsizeWidth < pixelsize; pixelsizeWidth++) {
                            pixels[offset + x * pixelsize + pixelsizeWidth] = color;
                        }
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(requestedWidth, requestedHeight, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, requestedWidth, 0, 0, requestedWidth, requestedHeight);
            return bitmap;
        } else {

            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        }
    }

}
package android.rn.com.barcode;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.rn.com.barcode.base.bean.Bimage;
import android.text.format.Time;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

import io.realm.Realm;

/**
 * Created by Adil on 9/6/2017.
 */

public class Utils {
    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;
    private static Dialog dialog;

    public static boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }

    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(cs.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    public static Dialog dialogMessage(String title, Activity context, final String msg, final View editTxt) {
        AlertDialog.Builder builder;

        builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (editTxt != null && editTxt instanceof EditText) {
                            editTxt.requestFocus();
                        }
                    }
                })
                .setCancelable(false);

        return builder.create();
    }

    public static Dialog dialogMessage(String title, Context context, String msg) {
        AlertDialog.Builder builder;
        if (title.equalsIgnoreCase("") || title.equalsIgnoreCase(" ")) {
            builder = new AlertDialog.Builder(context)
                    .setMessage(msg)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setCancelable(false);
        } else {
            builder = new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(msg)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setCancelable(false);
        }
        return builder.create();
    }

    public static long getNextId(Class clazz) {
        Realm realm = Realm.getDefaultInstance();
        Number currentIdNum = realm.where(clazz).max("id");
        long nextId;
        if (currentIdNum == null) {
            nextId = 1;
        } else {
            nextId = currentIdNum.intValue() + 1;
        }
        return nextId;
    }

    public static Bimage saveImage(Bitmap finalBitmap, String name) {
        Bimage bimage = new Bimage();
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File myDir = new File(root + "/BarcodeImage");
//        myDir.mkdirs();
        if (!myDir.exists()) myDir.mkdirs();

        String fname = "BC-" + name + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        bimage.setName(fname);
        bimage.setPath(myDir.getAbsolutePath());
        return bimage;
    }

    public static String getTime() {
//        return Calendar.getInstance().getTime().toString();
        Time now = new Time();
        now.setToNow();
        return now.format("%Y_%m_%d_%M_%S");
    }

    public static Bimage saveImage(Bitmap bitmapImage, Context context, String name) {
        Bimage bimage = new Bimage();
        ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        String fname = "BC-" + name + ".png";
        File mypath = new File(directory, fname);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert fos != null;
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        bimage.setName(fname);
        bimage.setPath(directory.getAbsolutePath());
        return bimage;
    }
    public static Bitmap loadImageFromStorage(Bimage bimage) {
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

    public static void hideKeyboard(Activity activity,EditText editText){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }
    public static void hideKeyboardTextView(Activity activity,TextView textView){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
        }
    }

    public static void showKeyboard(Activity activity,EditText editText){
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            editText.requestFocus();
        }
    }
    public static Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {
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
    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }


}

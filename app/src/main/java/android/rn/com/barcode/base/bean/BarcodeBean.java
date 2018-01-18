package android.rn.com.barcode.base.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by DELL on 1/10/2018.
 */

public class BarcodeBean extends RealmObject implements Parcelable {
    @PrimaryKey
    private long id = 0;
    private String type = "";
    private String data = "";
    private String desc = "";
    private Boolean isScanned;

    public Boolean isScanned() {
        return isScanned;
    }

    public void setScanned(Boolean scanned) {
        isScanned = scanned;
    }

    //    private String image="";
    private Bimage image = new Bimage();
    public static final String ID="id";
    public static final String TYPE="type";
    public static final String DATA="data";
    public static final String DESC="desc";
    public static final String IMAGE="image";
    public static final String IMAGE_NAME="image.name";
    public static final String IMAGE_PATH ="image.path";

    public BarcodeBean(long id, String type, String code, String data) {
        this.id = id;
        this.type = type;
        this.data = data;

    }

    @Override
    public String toString() {
        if (!TextUtils.isEmpty(desc)) {
            return "{" +
                    "Type='" + type + '\'' +
                    ", Data='" + data + '\'' +
                    ", Description='" + desc + '\'' +
                    '}';
        }else{
            return "{" +
                    "Type='" + type + '\'' +
                    ", Data='" + data + '\'' +
                    '}';
        }
    }

    public BarcodeBean() {
    }

    public Bimage getImage() {
        return image;
    }

    public void setImage(Bimage image) {
        this.image = image;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImageName() {
        return image.getName();
    }

    public void setImageName(String imageName) {
        this.image.setName(imageName);
    }

    public String getImagePath() {
        return image.getPath();
    }

    public void setImagePath(String path) {
        this.image.setPath(path);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }



    protected BarcodeBean(Parcel in) {
        id = in.readLong();
        type = in.readString();
        data = in.readString();
        desc = in.readString();
        byte isScannedVal = in.readByte();
        isScanned = isScannedVal == 0x02 ? null : isScannedVal != 0x00;
        image = (Bimage) in.readValue(Bimage.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(type);
        dest.writeString(data);
        dest.writeString(desc);
        if (isScanned == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (isScanned ? 0x01 : 0x00));
        }
        dest.writeValue(image);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<BarcodeBean> CREATOR = new Parcelable.Creator<BarcodeBean>() {
        @Override
        public BarcodeBean createFromParcel(Parcel in) {
            return new BarcodeBean(in);
        }

        @Override
        public BarcodeBean[] newArray(int size) {
            return new BarcodeBean[size];
        }
    };
}
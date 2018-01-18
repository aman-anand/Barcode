package android.rn.com.barcode.base.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by DELL on 1/10/2018.
 */

public class OcrBean extends RealmObject implements Parcelable {
    @PrimaryKey
    private long id=0;
    private String name= "";
    private String data= "";
    private  String desc="";

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public OcrBean() {
    }

    public OcrBean(long id, String name, String data) {
        this.id = id;
        this.name = name;
        this.data = data;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        if (!TextUtils.isEmpty(name)) {
            if (!TextUtils.isEmpty(desc)) {
                return
                        "Name='" + name + '\'' +
                        ", Data='" + data + '\'' +
                        ", Desc='" + desc + '\'' ;
            } else {
                return
                        "Name='" + name + '\'' +
                        ", Data='" + data + '\'';
            }
        }else{
            if (!TextUtils.isEmpty(desc)) {
                return
                        ", Data='" + data + '\'' +
                        ", Desc='" + desc + '\''
                        ;
            } else {
                return
                        "Data='" + data + '\'' ;
            }
        }
    }

    protected OcrBean(Parcel in) {
        id = in.readLong();
        name = in.readString();
        data = in.readString();
        desc = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(data);
        dest.writeString(desc);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<OcrBean> CREATOR = new Parcelable.Creator<OcrBean>() {
        @Override
        public OcrBean createFromParcel(Parcel in) {
            return new OcrBean(in);
        }

        @Override
        public OcrBean[] newArray(int size) {
            return new OcrBean[size];
        }
    };
}
package android.rn.com.barcode.base.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by DELL on 1/10/2018.
 */

public class NfcBean extends RealmObject implements Parcelable {
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<NfcBean> CREATOR = new Parcelable.Creator<NfcBean>() {
        @Override
        public NfcBean createFromParcel(Parcel in) {
            return new NfcBean(in);
        }

        @Override
        public NfcBean[] newArray(int size) {
            return new NfcBean[size];
        }
    };
    @PrimaryKey
    private long id = 0;
    private String data = "";
    private String desc = "";

    public NfcBean() {
    }

    protected NfcBean(Parcel in) {
        id = in.readLong();
        data = in.readString();
        desc = in.readString();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        if (!TextUtils.isEmpty(desc)) {
            return
                    "Data= '" + data + '\'' + ", Desc= '" + desc ;
        } else {
            return "Data= '" + data;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(data);
        dest.writeString(desc);
    }
}
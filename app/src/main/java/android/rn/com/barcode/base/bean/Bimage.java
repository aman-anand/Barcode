package android.rn.com.barcode.base.bean;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;

/**
 * Created by DELL on 1/11/2018.
 */
public class Bimage extends RealmObject implements Parcelable {
    String path="";
    String name="";


    public Bimage() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected Bimage(Parcel in) {
        path = in.readString();
        name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(name);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Bimage> CREATOR = new Parcelable.Creator<Bimage>() {
        @Override
        public Bimage createFromParcel(Parcel in) {
            return new Bimage(in);
        }

        @Override
        public Bimage[] newArray(int size) {
            return new Bimage[size];
        }
    };
}
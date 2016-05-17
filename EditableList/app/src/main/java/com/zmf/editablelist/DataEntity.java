package com.zmf.editablelist;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zimengfang on 16/5/16.
 */
public class DataEntity implements Parcelable{

    private String secid;
    private String ticker;
    private String secshortname;

    public String getSecid() {
        return secid;
    }

    public void setSecid(String secid) {
        this.secid = secid;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getSecshortname() {
        return secshortname;
    }

    public void setSecshortname(String secshortname) {
        this.secshortname = secshortname;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.secid);
        dest.writeString(this.ticker);
        dest.writeString(this.secshortname);
    }

    public DataEntity() {
    }

    protected DataEntity(Parcel in) {
        this.secid = in.readString();
        this.ticker = in.readString();
        this.secshortname = in.readString();
    }

    public static final Creator<DataEntity> CREATOR = new Creator<DataEntity>() {
        public DataEntity createFromParcel(Parcel source) {
            return new DataEntity(source);
        }

        public DataEntity[] newArray(int size) {
            return new DataEntity[size];
        }
    };
}

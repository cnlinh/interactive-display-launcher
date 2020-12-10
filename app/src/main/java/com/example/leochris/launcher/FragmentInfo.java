package com.example.leochris.launcher;

import android.os.Parcel;
import android.os.Parcelable;

public class FragmentInfo implements Parcelable {
    private String name;
    private String data;
    private int type;
    private boolean toBeDeleted;
    /*type values
    * 1:Image Tab
    * 2:Video Tab
    * 3:Text Tab
    * 4:Apps Grid Fragment
    * 5:Weather Tab*/
    public FragmentInfo(String name,int type) {
        this.name = name;
        this.type = type;
        data = "";
        toBeDeleted = false;
    }

    public FragmentInfo(String name) {
        this.name = name;
        toBeDeleted = false;
    }

    public String getName() {
        return name;
    }

    public String getData() {
        return data;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Boolean isToBeDeleted() {
        return toBeDeleted;
    }

    public void setToBeDeleted(Boolean toBeDeleted) {
        this.toBeDeleted = toBeDeleted;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }



    protected FragmentInfo(Parcel in) {
        name = in.readString();
        data = in.readString();
        type = in.readInt();
        toBeDeleted = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(data);
        dest.writeInt(type);
        dest.writeByte((byte) (toBeDeleted ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<FragmentInfo> CREATOR = new Parcelable.Creator<FragmentInfo>() {
        @Override
        public FragmentInfo createFromParcel(Parcel in) {
            return new FragmentInfo(in);
        }

        @Override
        public FragmentInfo[] newArray(int size) {
            return new FragmentInfo[size];
        }
    };
}
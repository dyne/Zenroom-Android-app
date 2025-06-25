package com.example.zencode;

import android.os.Parcel;
import android.os.Parcelable;

public class ZencodeContract implements Parcelable {
    private String title;
    private String contract;
    private String keys;
    private String data;

    public ZencodeContract(String title, String contract, String keys, String data) {
        this.title = title;
        this.contract = contract;
        this.keys = keys;
        this.data = data;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getContract() {
        return contract;
    }

    public String getKeys() {
        return keys;
    }

    public String getData() {
        return data;
    }

    // --- Parcelable Implementation ---

    protected ZencodeContract(Parcel in) {
        title = in.readString();
        contract = in.readString();
        keys = in.readString();
        data = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(contract);
        dest.writeString(keys);
        dest.writeString(data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ZencodeContract> CREATOR = new Creator<ZencodeContract>() {
        @Override
        public ZencodeContract createFromParcel(Parcel in) {
            return new ZencodeContract(in);
        }

        @Override
        public ZencodeContract[] newArray(int size) {
            return new ZencodeContract[size];
        }
    };

}
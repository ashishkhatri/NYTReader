package com.ashish.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

public class NewsItem implements Parcelable {
    public String mImageUrl;
    public String mTitle;
    public String mAuthor;
    public String mUrl;
    public String mAbstract;
    public boolean mExpanded;

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public NewsItem createFromParcel(Parcel in) {
            return new NewsItem(in);
        }

        public NewsItem[] newArray(int size) {
            return new NewsItem[size];
        }
    };

    public NewsItem() {
        mImageUrl = "";
        mTitle = "";
        mAuthor = "";
        mUrl = "";
        mAbstract = "";
        mExpanded = false;
    }

    public NewsItem(Parcel in) {
        mImageUrl = in.readString();
        mTitle = in.readString();
        mAuthor = in.readString();
        mUrl = in.readString();
        mAbstract = in.readString();
        mExpanded = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mImageUrl);
        dest.writeString(mTitle);
        dest.writeString(mAuthor);
        dest.writeString(mUrl);
        dest.writeString(mAbstract);
        dest.writeByte((byte) (mExpanded ? 1 : 0));
    }
}

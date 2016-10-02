package com.fivetrue.fivetrueandroid.google;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.location.places.PlacePhotoMetadata;

/**
 * Created by kwonojin on 16. 9. 23..
 */
public final class PlaceImageData implements Parcelable {

    public final String placeId;
    public final int width;
    public final int height;
    public final String key;
    public final Bitmap image;

    public PlaceImageData(String placeId, PlacePhotoMetadata metadata, Bitmap image){
        this.placeId = placeId;
        this.width = metadata.getMaxWidth();
        this.height = metadata.getMaxHeight();
        this.key = metadata.getAttributions().toString();
        this.image = image;
    }

    protected PlaceImageData(Parcel in) {
        placeId = in.readString();
        width = in.readInt();
        height = in.readInt();
        key = in.readString();
        image = null;
//        image = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<PlaceImageData> CREATOR = new Creator<PlaceImageData>() {
        @Override
        public PlaceImageData createFromParcel(Parcel in) {
            return new PlaceImageData(in);
        }

        @Override
        public PlaceImageData[] newArray(int size) {
            return new PlaceImageData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(placeId);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeString(key);
//        dest.writeParcelable(image, flags);
    }
}

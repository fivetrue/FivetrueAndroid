package com.fivetrue.fivetrueandroid.google;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.android.volley.toolbox.ImageLoader;
import com.fivetrue.fivetrueandroid.image.ImageLoadManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kwonojin on 16. 9. 13..
 */
public class GoogleApiUtils {

    private static final String TAG = "GoogleApiUtils";

    public interface OnLoadPhotoMetadataBufferListener{
        void onLoadPhotoMetadataBuffer(PlacePhotoMetadataBuffer buffer);

    }

    public interface OnLoadPhotoListener{
        void onLoadImages(ArrayList<PlaceImageData> placeImageDatas);
    }


    public static void getStaticMapAsync(StaticMapData data, String apiKey, ImageLoader.ImageListener ll){
        ImageLoadManager.getInstance().loadImageUrl(getStaticMapUrl(data, apiKey), ll);
    }

    public static String getStaticMapUrl(StaticMapData data, String apiKey){
        return data.toMapImageUrl(apiKey);
    }

    public static void getPhotoMetaDatabBuffer(String placeId , final GoogleApiClient apiClient, final OnLoadPhotoMetadataBufferListener ll){
        if(placeId != null && apiClient != null){
            new AsyncTask<String, Void, PlacePhotoMetadataBuffer>(){

                @Override
                protected PlacePhotoMetadataBuffer doInBackground(String... params) {
                    ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
                    String place = params[0];
                    PlacePhotoMetadataBuffer photoMetadataBuffer = null;
                    PlacePhotoMetadataResult result = Places.GeoDataApi
                            .getPlacePhotos(apiClient, place).await();
                    if (result != null && result.getStatus().isSuccess()) {
                        photoMetadataBuffer = result.getPhotoMetadata();
                    }
                    return photoMetadataBuffer;
                }

                @Override
                protected void onPostExecute(PlacePhotoMetadataBuffer buffer) {
                    super.onPostExecute(buffer);
                    if(buffer != null){
                        ll.onLoadPhotoMetadataBuffer(buffer);
                    }

                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, placeId);
        }
    }

    public static void getPhotosByPlaceId(String placeId , final GoogleApiClient apiClient, final OnLoadPhotoListener ll){
        if(placeId != null && apiClient != null){
            new AsyncTask<String, Void, ArrayList<PlaceImageData>>(){

                @Override
                protected ArrayList<PlaceImageData> doInBackground(String... params) {
                    ArrayList<PlaceImageData> placeImageDatas = new ArrayList<PlaceImageData>();
                    String place = params[0];
                    if(place != null){
                        PlacePhotoMetadataBuffer photoMetadataBuffer = null;
                        PlacePhotoMetadataResult result = Places.GeoDataApi
                                .getPlacePhotos(apiClient, place).await();
                        if (result != null && result.getStatus().isSuccess()) {
                            photoMetadataBuffer = result.getPhotoMetadata();
                            for(PlacePhotoMetadata data : photoMetadataBuffer) {
                                Bitmap bm = ImageLoadManager.getInstance().getBitmapFromCache(data.getAttributions().toString());
                                if (bm != null && !bm.isRecycled()) {
                                    PlaceImageData imageData = new PlaceImageData(place, data, bm);
                                    placeImageDatas.add(imageData);
                                } else {
                                    bm = data.getPhoto(apiClient).await().getBitmap();
                                    if (bm != null && !bm.isRecycled()) {
                                        ImageLoadManager.getInstance().putBitmapToCache(data.getAttributions().toString(), bm);
                                        PlaceImageData imageData = new PlaceImageData(place, data, bm);
                                        placeImageDatas.add(imageData);
                                    }
                                }
                            }

                        }
                    }
                    return placeImageDatas;
                }

                @Override
                protected void onPostExecute(ArrayList<PlaceImageData> placeImageDatas) {
                    super.onPostExecute(placeImageDatas);
                    if(placeImageDatas != null){
                        ll.onLoadImages(placeImageDatas);
                    }

                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, placeId);
        }
    }
}

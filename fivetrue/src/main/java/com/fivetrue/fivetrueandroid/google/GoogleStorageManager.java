package com.fivetrue.fivetrueandroid.google;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;

/**
 * Created by kwonojin on 16. 9. 20..
 */
public class GoogleStorageManager {

    private static final String TAG = "GoogleStorageManager";

    public interface OnUploadResultListener{
        void onUploadSuccess(Uri url);
        void onUploadFailed(Exception e);
    }

    private static final float DEFAULT_ASPECT_X = 4f;
    private static final float DEFAULT_ASPECT_Y = 3f;
    private static final float DEFAULT_ASPECT_RATIO = DEFAULT_ASPECT_X / DEFAULT_ASPECT_Y;
    private static final float DEFAULT_CROP_IMAGE_WIDTH =  1080f;
    private static final float DEFAULT_CROP_IMAGE_HEIGHT = DEFAULT_CROP_IMAGE_WIDTH / DEFAULT_ASPECT_RATIO;

    private static final int DEFAULT_PROFILE_IMAGE_SIZE = 200;

    private Context mContext = null;

    private FirebaseStorage mFirebaseStorage = null;
    private StorageReference mStorageRef = null;

    private float mAspectX = DEFAULT_ASPECT_X;
    private float mAspectY = DEFAULT_ASPECT_Y;
    private float mAspectRatio = mAspectX / mAspectY;
    private float mImageWidth = DEFAULT_CROP_IMAGE_WIDTH;
    private float mImageHeight = DEFAULT_CROP_IMAGE_HEIGHT;

    public GoogleStorageManager(Context context, String firebaseUrl){
        this(context, firebaseUrl, 0, 0, 0, 0);
    }

    public GoogleStorageManager(Context context, String firebaseUrl, float aspectX, float aspectY, float imageSize){
        this(context, firebaseUrl, aspectX, aspectY, imageSize, 0);
    }

    public GoogleStorageManager(Context context, String firebaseUrl, float aspectX, float aspectY
            , float imageWidth, float imageHeight){
        mContext = context;
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageRef = mFirebaseStorage.getReferenceFromUrl(firebaseUrl);
        mAspectX = aspectX > 0 ? aspectX : DEFAULT_ASPECT_X;
        mAspectY = aspectY > 0 ? aspectY : DEFAULT_ASPECT_Y;
        mAspectRatio = mAspectX / mAspectY;
        mImageWidth = imageWidth > 0 ? imageWidth : DEFAULT_CROP_IMAGE_WIDTH;
        mImageHeight = imageHeight > 0 ? imageHeight : imageWidth / mAspectRatio;
    }

    public void uploadBitmapImage(String path, String childName, Bitmap bitmap, final OnUploadResultListener ll){
        if(childName != null && bitmap != null && !bitmap.isRecycled() && ll != null){
            if(bitmap.getHeight() <= DEFAULT_CROP_IMAGE_WIDTH && bitmap.getWidth() <= DEFAULT_CROP_IMAGE_WIDTH){
                StorageReference profileImageRef = mStorageRef.child(path + childName);
                uploadImageToStorage(profileImageRef, bitmap, ll);
            }else{
                ll.onUploadFailed(new Exception("Bitmap size over " + DEFAULT_CROP_IMAGE_WIDTH));
            }
        }
    }

    private void uploadImageToStorage(StorageReference reference, Bitmap bitmap, final OnUploadResultListener ll){
        if(reference != null && bitmap != null && !bitmap.isRecycled()){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = reference.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception exception) {
                    ll.onUploadFailed(exception);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    ll.onUploadSuccess(taskSnapshot.getDownloadUrl());
                }
            });
        }
    }

    public static void chooseDeviceImage(Activity activity, int requestCode){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void cropChooseDeviceImage(Activity activity, Uri uri){
        cropChooseDeviceImage(activity, uri, (int) DEFAULT_ASPECT_X, (int) DEFAULT_ASPECT_Y
                , (int)DEFAULT_CROP_IMAGE_WIDTH, (int)DEFAULT_CROP_IMAGE_HEIGHT);
    }

    public static void cropChooseDeviceImage(Activity activity, Uri uri
            , int aspectX, int aspectY, int width, int height){
        CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(aspectX, aspectY)
                .setRequestedSize(width, height)
                .start(activity);
    }

}

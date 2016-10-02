package com.fivetrue.fivetrueandroid.google;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.fivetrue.hangoutbaby.Constants;
import com.fivetrue.hangoutbaby.vo.PlaceItem;
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
    private static final float DEFAULT_CROP_IMAGE_WIDTH = 720f;
    private static final float DEFAULT_CROP_IMAGE_HEIGHT = DEFAULT_CROP_IMAGE_WIDTH / DEFAULT_ASPECT_RATIO;

    private static final int DEFAULT_PROFILE_IMAGE_SIZE = 200;

    private static final String IMAGE_ROOT = "images/";
    private static final String IMAGE_PROFILE = IMAGE_ROOT + "profile/";
    private static final String IMAGE_COMMENT = IMAGE_ROOT + "comment/";

    private Context mContext = null;

    private static GoogleStorageManager sInstance = null;

    private FirebaseStorage mFirebaseStorage = null;
    private StorageReference mStorageRef = null;

    public static GoogleStorageManager getInstnace(Context context){
        if(sInstance == null){
            sInstance = new GoogleStorageManager(context);
        }
        return sInstance;
    }

    private GoogleStorageManager(Context context){
        mContext = context;
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageRef = mFirebaseStorage.getReferenceFromUrl(Constants.FIREBASE_STORAGE_URL);
    }

    public void uploadCommentImage(PlaceItem item, Bitmap bitmap, final OnUploadResultListener ll){
        if(item != null && bitmap != null && !bitmap.isRecycled() && ll != null){
            String childName = item.getPlaceId() + item.getPlaceAuthor();
            if(bitmap.getHeight() <= DEFAULT_CROP_IMAGE_WIDTH && bitmap.getWidth() <= DEFAULT_CROP_IMAGE_WIDTH){
                StorageReference profileImageRef = mStorageRef.child(IMAGE_COMMENT + childName);
                uploadImageToStorage(profileImageRef, bitmap, ll);
            }else{
                ll.onUploadFailed(new Exception("Bitmap size over " + DEFAULT_CROP_IMAGE_WIDTH));
            }
        }
    }

    public void uploadProfileImage(String childName, Bitmap bitmap, OnUploadResultListener ll){
        if(childName != null && bitmap != null && !bitmap.isRecycled() && ll != null){
            if(bitmap.getHeight() <= DEFAULT_PROFILE_IMAGE_SIZE && bitmap.getWidth() <= DEFAULT_PROFILE_IMAGE_SIZE){
                StorageReference profileImageRef = mStorageRef.child(IMAGE_PROFILE + childName);
                uploadImageToStorage(profileImageRef, bitmap, ll);
            }else{
                ll.onUploadFailed(new Exception("Bitmap size over 200px"));
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
        CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio((int) DEFAULT_ASPECT_X, (int) DEFAULT_ASPECT_Y)
                .setRequestedSize((int)DEFAULT_CROP_IMAGE_WIDTH, (int)DEFAULT_CROP_IMAGE_HEIGHT)
                .start(activity);
    }

}

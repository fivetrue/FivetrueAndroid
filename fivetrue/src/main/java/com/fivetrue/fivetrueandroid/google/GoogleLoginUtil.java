package com.fivetrue.fivetrueandroid.google;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.VolleyError;
import com.fivetrue.fivetrueandroid.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Arrays;

/**
 * Created by kwonojin on 16. 9. 19..
 */
public class GoogleLoginUtil implements FirebaseAuth.AuthStateListener{

    private static final String TAG = "GoogleLoginUtil";

    public interface OnAccountManagerListener{
        void onUserAddSuccess(FirebaseUser user);
        void onUserAddError(Exception message);
        void onUpdateUserInfo(FirebaseUser user);
    }

    private static final int REQUEST_GOOGLE_ACCOUNT_LOGIN = 0x44;

    /**
     * Google
     */
    private GoogleApiClient mGoogleApiClient = null;

    private FragmentActivity mActivity = null;

    private OnAccountManagerListener mOnAccountManagerListener = null;

    private FirebaseAuth mAuth = null;

    private String mClientId = null;

    public GoogleLoginUtil(FragmentActivity activity, String firebaseClientId){
        mActivity = activity;
        mClientId = firebaseClientId;
        if(mActivity instanceof OnAccountManagerListener){
            mOnAccountManagerListener = (OnAccountManagerListener) mActivity;
        }
        initFirebase();
        initGoogle();

    }
    private void initFirebase(){
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(this);
    }

    private void initGoogle(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(mClientId)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .enableAutoManage(mActivity, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Log.d(TAG, "onConnectionFailed() called with: " + "connectionResult = [" + connectionResult + "]");
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public void onStart(){
        mGoogleApiClient.connect();
    }

    public void onStop(){
        mGoogleApiClient.disconnect();
    }

    public boolean isSignIn(){
        return mAuth.getCurrentUser() != null;
    }

    public FirebaseUser getUser(){
        return mAuth.getCurrentUser();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d(TAG, "onActivityResult() called with: " + "requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        if(requestCode == REQUEST_GOOGLE_ACCOUNT_LOGIN){
            if(resultCode == Activity.RESULT_OK){
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleGoogleLogin(result);
            }else{
                onUserAddFail(new Exception("Cancel login"));
            }
        }
    }

    private void handleGoogleLogin(GoogleSignInResult result){
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String idToken = acct.getIdToken();
            String serverAuthCode = acct.getServerAuthCode();
            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, serverAuthCode);
            requestAuthCredential(credential);
        } else {
            onUserAddFail(new Exception(result.getStatus().getStatusMessage()));
        }
    }

    private void requestAuthCredential(AuthCredential credential){
        mAuth.signInWithCredential(credential).addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = task.getResult().getUser();
                    onUserAddFinish(user);
                }else {
                    onUserAddFail(task.getException());
                }
            }
        });
    }

    public void loginGoogleAccount(){
        Log.d(TAG, "loginGoogleAccount() called with: " + "");
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        mActivity.startActivityForResult(signInIntent, REQUEST_GOOGLE_ACCOUNT_LOGIN);
    }

    public void logout(){
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        mAuth.signOut();
    }

    public void updateUser(String name, String imageUrl){
        if((!TextUtils.isEmpty(name) || !TextUtils.isEmpty(imageUrl)) && getUser() != null){
            UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
            if(!TextUtils.isEmpty(name)){
                builder.setDisplayName(name);
            }
            if(!TextUtils.isEmpty(imageUrl)){
                builder.setPhotoUri(Uri.parse(imageUrl));
            }
            UserProfileChangeRequest profileUpdates = builder.build();
            final FirebaseUser user = getUser();
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()) {
                                mOnAccountManagerListener.onUpdateUserInfo(getUser());
                            }
                        }
                    });
        }

    }

    private void onUserAddFinish(FirebaseUser user) {
        Log.d(TAG, "onUserAddFinish() called with: " + "user = [" + user + "]");
        mOnAccountManagerListener.onUserAddSuccess(user);
    }

    private void onUserAddFail(Exception e){
        Log.d(TAG, "onUserAddFail() called with: " + "message = [" + e + "]");
        if(mOnAccountManagerListener != null){
            mOnAccountManagerListener.onUserAddError(e);
        }
    }

    @Override
    public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
        if(firebaseAuth != null){
            Log.d(TAG, "onAuthStateChanged() called with: " + "firebaseAuth = [" + firebaseAuth + "]");
        }
    }
}

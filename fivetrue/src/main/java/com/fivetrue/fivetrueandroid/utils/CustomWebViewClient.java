package com.fivetrue.fivetrueandroid.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.fivetrue.fivetrueandroid.ui.diaglog.LoadingDialog;

/**
 * Created by kwonojin on 2016. 10. 10..
 */

public class CustomWebViewClient extends WebViewClient {

    private static final String TAG = "CustomWebViewClient";

    private static final int REQUEST_CODE_GET_FILE = 0x77;
    private static final int REQUEST_CODE_GET_FILE_OVER_L = 0x66;

    private Activity mActivity;
    private WebView mWebView;
    private LoadingDialog mLoadingDialog;

    private ValueCallback<Uri> mFileCallback;
    private ValueCallback<Uri[]> mFilePathCallbacks = null;
    private String mUrl = null;

    private JSInterface mJsInterface;

    public CustomWebViewClient(Activity activity, WebView webView){
        this(activity, webView, null);
    }

    public CustomWebViewClient(Activity activity, WebView webView, JSInterface jsInterface){

        this.mActivity = activity;
        this.mWebView = webView;
        this.mJsInterface = jsInterface;
        this.mLoadingDialog = new LoadingDialog(activity);

        mWebView.setWebViewClient(this);
        mWebView.setWebChromeClient(webChromeClient);
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        mWebView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        mWebView.getSettings().setSupportZoom(true);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        if(jsInterface != null){
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.addJavascriptInterface(mJsInterface.getInterface(), mJsInterface.getInterfaceName());
        }
    }

    public void onStart(){
        mWebView.onResume();
    }

    public void onStop(){
        mWebView.onPause();
    }

    public void onDestroy(){
        mWebView.destroy();
    }

    public String getCurrentUrl(){
        return mUrl;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        switch (requestCode){
            case REQUEST_CODE_GET_FILE:
                onGetFile(resultCode, intent);
                break;
            case REQUEST_CODE_GET_FILE_OVER_L:
                onGetFileOverL(resultCode, intent);
                break;
        }
    }


    protected boolean onShouldOverrideUrlLoading(WebView view, String url){
        boolean b = false;
        return b;
    }

    protected void onWebPageFinished(WebView view, String url){
        mLoadingDialog.dismiss();
        mUrl = url;
        if(mJsInterface != null){
            mJsInterface.onLoadedPage(view, url);
        }
    }

    protected void onWebPageCommitVisible(WebView view, String url){

    }

    protected void onPageProgressChanged(WebView view, int newProgress) {
    }

    protected void onWebPageStarted(WebView view, String url, Bitmap favicon){
        mLoadingDialog.show();
        if(mJsInterface != null){
            mJsInterface.onStartPage(view, url);
        }
    }

    protected void onPagePermissionRequest(PermissionRequest request){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            request.grant(request.getResources());
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected boolean onShowFileChooserFromWeb(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams){
        mFilePathCallbacks = filePathCallback;
        try {
            Intent intent = fileChooserParams.createIntent();
            mActivity.startActivityForResult(intent, REQUEST_CODE_GET_FILE_OVER_L);
        } catch (Exception e) {
            // TODO: when open file chooser failed
        }
        return true;
    }

    protected void onOpenFileChooserFromWeb(ValueCallback<Uri> uploadMsg, String acceptType, String caputre){
        mFileCallback = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        if(acceptType != null){
            i.setType(acceptType);
        }else{
            i.setType("*/*");
        }
        mActivity.startActivityForResult(Intent.createChooser(i, "Select File"), REQUEST_CODE_GET_FILE);
    }

    protected void onGetFile(int resultCode, Intent intent){
        if(intent != null && mFileCallback != null){
            if(resultCode == Activity.RESULT_OK){
                mFileCallback.onReceiveValue(intent.getData());
            }else{
                mFileCallback.onReceiveValue(null);
            }
            mFileCallback = null;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void onGetFileOverL(int resultCode, Intent intent){
        if(intent != null && mFilePathCallbacks != null){
            mFilePathCallbacks.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
            mFilePathCallbacks = null;
        }
    }



    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return onShouldOverrideUrlLoading(view, url);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        onWebPageFinished(view, url);
    }

    @Override
    public void onPageCommitVisible(WebView view, String url) {
        super.onPageCommitVisible(view, url);
        onWebPageCommitVisible(view, url);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        onWebPageStarted(view, url, favicon);
    }


    private WebChromeClient webChromeClient = new WebChromeClient(){
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            onPageProgressChanged(view, newProgress);
        }

        @Override
        public void onPermissionRequest(PermissionRequest request) {
            super.onPermissionRequest(request);
            onPagePermissionRequest(request);
        }

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            return onShowFileChooserFromWeb(webView, filePathCallback, fileChooserParams);
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            onOpenFileChooserFromWeb(uploadMsg, null, null);
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            onOpenFileChooserFromWeb(uploadMsg, acceptType, null);
        }

        //For Android 4.1
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            onOpenFileChooserFromWeb(uploadMsg, acceptType, capture);
        }
    };

    public interface JSInterface{
        String getInterfaceName();
        Object getInterface();

        void onLoadedPage(WebView webView, String url);
        void onStartPage(WebView webView, String url);
    }
}

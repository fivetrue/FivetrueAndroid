package com.fivetrue.fivetrueandroid.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fivetrue.fivetrueandroid.R;

/**
 * Created by kwonojin on 16. 7. 20..
 */
public abstract class BaseDialogFragment extends DialogFragment {

    private static final String TAG = "BaseDialogFragment";

    private static final String KEY_TITLE = TAG + "title";
    private static final String KEY_BUTTON_OK = TAG + "ok";
    private static final String KEY_BUTTON_CANCEL = TAG + "cancel";

    public interface OnClickDialogFragmentListener <T>{
        void onClickOKButton(BaseDialogFragment f, T data);
        void onClickCancelButton(BaseDialogFragment f, T data);
    }

    private TextView mTitle = null;
    private RelativeLayout mLayoutContent = null;
    private Button mOk = null;
    private Button mCancel = null;

    private OnClickDialogFragmentListener mOnClickDialogFragmentListener = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(mOnClickDialogFragmentListener == null){
            if(getParentFragment() != null && getParentFragment() instanceof OnClickDialogFragmentListener){
                mOnClickDialogFragmentListener = (OnClickDialogFragmentListener) getParentFragment();
            }else if(getActivity() != null && getActivity() instanceof OnClickDialogFragmentListener){
                mOnClickDialogFragmentListener = (OnClickDialogFragmentListener) getActivity();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = initView(inflater);

        return view;
    }

    private View initView(LayoutInflater inflater){
        View view = inflater.inflate(R.layout.base_dialog_fragment, null);
        mTitle = (TextView) view.findViewById(R.id.tv_base_dialog_fragment_title);
        mLayoutContent = (RelativeLayout) view.findViewById(R.id.layout_base_dialog_fragment);
        mOk = (Button) view.findViewById(R.id.btn_base_dialog_fragemnt_ok);
        mCancel = (Button) view.findViewById(R.id.btn_base_dialog_fragemnt_cancel);
        mOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickOkButton();
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickCancelButton();
            }
        });

        View childView = onCreateChildView(inflater);
        if(childView != null){
            mLayoutContent.addView(childView);
        }

        String title = getArguments().getString(KEY_TITLE);
        if(!TextUtils.isEmpty(title)){
            setTitle(title);
        }else{
            mTitle.setVisibility(View.GONE);
        }

        String ok = getArguments().getString(KEY_BUTTON_OK);
        if(!TextUtils.isEmpty(ok)){
            mOk.setText(ok);
        }else{
            mOk.setVisibility(View.GONE);
        }

        String cancel = getArguments().getString(KEY_BUTTON_CANCEL);
        if(!TextUtils.isEmpty(cancel)){
            mCancel.setText(cancel);
        }else{
            mCancel.setVisibility(View.GONE);
        }

        return view;
    }

    protected abstract View onCreateChildView(LayoutInflater inflater);

    public void setTitle(int stringRes){
        mTitle.setText(stringRes);
    }

    public void setTitle(String title){
        mTitle.setText(title);
    }


    public void show(FragmentManager fm,
                     Bundle b, OnClickDialogFragmentListener ll){
        show(fm, null, null, null, b, ll);
    }

    public void show(FragmentManager fm, String title,
                     Bundle b, OnClickDialogFragmentListener ll){
        show(fm, title, null, null, b, ll);
    }
    public void show(FragmentManager fm, String title, String buttonOk, String buttonCancel,
                     Bundle b, OnClickDialogFragmentListener ll){
        if(b == null){
            b = new Bundle();
        }
        b.putString(KEY_TITLE, title);
        b.putString(KEY_BUTTON_OK, buttonOk);
        b.putString(KEY_BUTTON_CANCEL, buttonCancel);
        setStyle(STYLE_NO_TITLE, 0);
        setArguments(b);
        if(ll != null){
            mOnClickDialogFragmentListener = ll;
        }
        super.show(fm, getClass().getSimpleName());
    }

    protected OnClickDialogFragmentListener getOnClickDialogFragmentListener() {
        return mOnClickDialogFragmentListener;
    }

    protected void onClickOkButton(){
        if(mOnClickDialogFragmentListener != null){
            mOnClickDialogFragmentListener.onClickOKButton(this, null);
        }
    }

    protected void onClickCancelButton(){
        if(mOnClickDialogFragmentListener != null){
            mOnClickDialogFragmentListener.onClickCancelButton(this, null);
        }
    }

}

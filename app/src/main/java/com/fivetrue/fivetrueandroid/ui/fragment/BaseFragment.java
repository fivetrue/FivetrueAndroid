package com.fivetrue.fivetrueandroid.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

/**
 * Created by kwonojin on 16. 3. 18..
 */
public class BaseFragment extends Fragment implements BaseFragmentImp{

    protected void showToast(String msg) {
        if (getActivity() != null) {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    public String getFragmentTag(){
        return getClass().getSimpleName();
    }

    public String getFragmentTitle(){
        return getClass().getSimpleName();
    }

    @Override
    public String getFragmentBackStackName() {
        return getClass().getName();
    }

    public void addChildFragment(Class<? extends BaseFragment> fClss, Bundle bundle){
        addChildFragment(fClss, bundle, true);
    }

    public void addChildFragment(Class<? extends BaseFragment> fClss, Bundle bundle, boolean addBackStack) {
        BaseFragment f = null;
        try {
            f = fClss.newInstance();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if(f != null){
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            if(bundle != null){
                f.setArguments(bundle);
            }
            if(addBackStack){
                ft.addToBackStack(f.getFragmentBackStackName());
            }
            ft.setBreadCrumbTitle(f.getFragmentTitle())
                    .replace(getChildFragmentAnchorId(), f, f.getFragmentTag())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public int getChildFragmentAnchorId(){
        return getView().getId();
    }

    @Override
    public int getFragmentNameResource(){
        return 0;
    }

}
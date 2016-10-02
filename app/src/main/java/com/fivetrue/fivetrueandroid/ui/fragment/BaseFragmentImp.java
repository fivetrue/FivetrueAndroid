package com.fivetrue.fivetrueandroid.ui.fragment;

/**
 * Created by kwonojin on 16. 9. 21..
 */
public interface BaseFragmentImp {

    String getFragmentTag();

    String getFragmentTitle();

    String getFragmentBackStackName();

    int getChildFragmentAnchorId();

    int getFragmentNameResource();

}

package com.fivetrue.fivetrueandroid.ui.adapter;

import android.support.v4.view.PagerAdapter;

/**
 * Created by ojin.kwon on 2016-04-03.
 */
abstract public class BasePagerAdapter extends PagerAdapter {

    protected static final int INVALID_VALUE = -1;

    public abstract Object getItem(int position);
}

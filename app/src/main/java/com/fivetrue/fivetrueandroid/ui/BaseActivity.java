package com.fivetrue.fivetrueandroid.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.fivetrue.fivetrueandroid.ui.diaglog.LoadingDialog;
import com.fivetrue.fivetrueandroid.ui.fragment.BaseFragmentImp;


/**
 * Created by kwonojin on 16. 7. 11..
 */
abstract public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    private LoadingDialog mLoadingDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    protected boolean popFragment(FragmentManager fm){
        boolean b = fm.popBackStackImmediate();
        return b;
    }

    public Fragment addFragment(Class< ? extends BaseFragmentImp> cls, Bundle arguments, int anchorLayout, boolean addBackstack){
        return addFragment(cls, arguments, anchorLayout, 0, 0, addBackstack);
    }

    public Fragment addFragment(Class< ? extends BaseFragmentImp> cls, Bundle arguments, boolean addBackstack){
        return addFragment(cls, arguments, getFragmentAnchorLayoutID(), 0, 0, addBackstack);
    }

    public Fragment addFragment(Class< ? extends BaseFragmentImp> cls, Bundle arguments, int anchorLayout, int enterAnim, int exitAnim, boolean addBackstack){
        Fragment f = null;
        try {
            f = (Fragment) cls.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if(f != null){
            if(arguments != null){
                f.setArguments(arguments);
            }
            FragmentTransaction ft = getCurrentFragmentManager().beginTransaction();
            ft.setCustomAnimations(enterAnim, exitAnim, enterAnim, exitAnim)
                    .replace(anchorLayout, f, ((BaseFragmentImp) f).getFragmentTag());
            if(addBackstack){
                ft.addToBackStack(((BaseFragmentImp) f).getFragmentTag());
                ft.setBreadCrumbTitle(((BaseFragmentImp) f).getFragmentNameResource());
            }
            ft.commitAllowingStateLoss();
        }
        return f;
    }

    @Override
    public void onBackPressed() {
        if(popFragment(getCurrentFragmentManager())){
            return;
        }
        super.onBackPressed();
    }

    protected FragmentManager getCurrentFragmentManager(){
        return getSupportFragmentManager();
    }

    protected int getFragmentAnchorLayoutID(){
        return android.R.id.content;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        int res = getOptionsMenuResource();
        if(res > 0){
            getMenuInflater().inflate(res, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    protected int getOptionsMenuResource(){
        return 0;
    }

    protected boolean usingParentLayoutStyle(){
        return true;
    }

    protected void showLoadingDialog(){
        if(mLoadingDialog == null){
            mLoadingDialog = new LoadingDialog(this);
        }
        if(!mLoadingDialog.isShowing()){
            mLoadingDialog.show();
        }
    }

    protected void dismissLoadingDialog(){
        if(mLoadingDialog != null && mLoadingDialog.isShowing()){
            mLoadingDialog.dismiss();
        }
    }
}


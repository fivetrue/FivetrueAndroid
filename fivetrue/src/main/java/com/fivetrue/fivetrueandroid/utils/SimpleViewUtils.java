package com.fivetrue.fivetrueandroid.utils;

import android.animation.Animator;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

/**
 * Created by kwonojin on 16. 8. 5..
 */
public class SimpleViewUtils {

    private static final String TAG = "SimpleViewUtils";

    public interface SimpleAnimationStatusListener{
        void onStart();
        void onEnd();
    }

    public static void showView(View view, int visibility){
        showView(view, visibility, null);
    }

    public static void showView(View view, final int visibility, final SimpleAnimationStatusListener ll){
        if(view != null && view.getParent() != null){
            if(!view.isShown()){
                try{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Animator animator = ViewAnimationUtils.createCircularReveal(view,
                                view.getWidth() / 2,
                                view.getWidth() / 2,
                                0,
                                view.getWidth());
                        animator.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                if(ll != null){
                                    ll.onStart();
                                }
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if(ll != null){
                                    ll.onEnd();
                                }
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                        animator.start();
                        view.setVisibility(visibility);
                    } else {
                        AlphaAnimation anim = new AlphaAnimation(0, 1);
                        anim.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                if(ll != null){
                                    ll.onStart();
                                }
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                if(ll != null){
                                    ll.onEnd();
                                }
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        anim.setDuration(300L);
                        view.setAnimation(anim);
                        view.setVisibility(visibility);
                    }
                }catch (Exception e){
                    Log.e(TAG, "showView() error : " + e);
                    view.setVisibility(visibility);
                }
            }
        }
    }

    public static void hideView(View view, int visibility){
        hideView(view, visibility, null);
    }

    public static void hideView(final View view, final int visibility, final SimpleAnimationStatusListener ll){
        hideView(view, visibility, view.getWidth() / 2, view.getHeight() / 2, ll);
    }

    public static void hideView(final View view, final int visibility, int centerX, int centerY, final SimpleAnimationStatusListener ll){
        if(view != null){
            if(view.isShown()){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    Animator anim = ViewAnimationUtils.createCircularReveal(view,
                            centerX,
                            centerY,
                            view.getWidth(),
                            0);
                    anim.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            if(ll != null){
                                ll.onStart();
                            }
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            view.setVisibility(visibility);
                            if(ll != null){
                                ll.onEnd();
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    anim.start();
                }else{
                    AlphaAnimation anim = new AlphaAnimation(1, 0);
                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            if(ll != null){
                                ll.onStart();
                            }
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if(ll != null){
                                ll.onEnd();
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    anim.setDuration(300L);
                    view.setAnimation(anim);
                    view.setVisibility(visibility);
                }
            }else{
                view.setVisibility(visibility);
            }
        }
    }

}

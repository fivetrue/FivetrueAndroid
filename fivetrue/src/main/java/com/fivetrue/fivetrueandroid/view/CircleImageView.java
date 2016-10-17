package com.fivetrue.fivetrueandroid.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.fivetrue.fivetrueandroid.image.ImageLoadManager;
import com.fivetrue.fivetrueandroid.utils.SimpleViewUtils;

/**
 * Created by kwonojin on 16. 6. 14..
 */
public class CircleImageView extends ImageView {

    private static final String TAG = "CircleImageView";

    private boolean mShowingAnimation = true;
    public CircleImageView(Context context) {
        super(context);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setImageUrl(final String url){
        Bitmap bm = ImageLoadManager.getInstance().getBitmapFromCache(url + ":circle");
        if(bm != null && !bm.isRecycled()){
            setImageBitmap(bm);
        }else{
            ImageLoadManager.getInstance().loadImageUrl(url, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if(response != null && response.getBitmap() != null && !response.getBitmap().isRecycled()){
                        Bitmap temp = Bitmap.createBitmap(response.getBitmap().getWidth(),
                                response.getBitmap().getHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(temp);

                        Path oval = new Path();
                        oval.addCircle(temp.getWidth() / 2, temp.getHeight() / 2, temp.getWidth() / 2,  Path.Direction.CCW);
                        canvas.clipPath(oval);

                        canvas.drawBitmap(response.getBitmap(), 0, 0, null);

                        final Bitmap output = Bitmap.createBitmap(temp.getWidth(),
                                temp.getHeight(), Bitmap.Config.ARGB_8888);

                        Canvas dest = new Canvas(output);
                        dest.drawBitmap(temp, 0, 0, null);
                        temp.recycle();

                        Paint paint = new Paint();
                        paint.setStrokeWidth(0.5f);
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setColor(Color.BLACK);
                        paint.setAntiAlias(true);
                        dest.drawPath(oval, paint);
                        ImageLoadManager.getInstance().putBitmapToCache(url + ":circle", output);
                        if(mShowingAnimation){
                            SimpleViewUtils.showView(CircleImageView.this, VISIBLE, new SimpleViewUtils.SimpleAnimationStatusListener() {
                                @Override
                                public void onStart() {
                                    setImageBitmap(output);
                                }

                                @Override
                                public void onEnd() {

                                }
                            });
                        }
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "network error : ", error);
                }
            });
        }
    }

    public void setShowingAnimation(boolean b){
        mShowingAnimation = b;
    }

}

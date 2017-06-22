package com.testframe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by lzp on 2017/6/22.
 */

public class GifImageView extends ImageView {
    private ArrayList<Integer> resIds = new ArrayList<>();
    private volatile boolean loop = true;
    private long fps = 500;
    private Bitmap mBitmap;
    private volatile boolean stop = true;
    private Thread wThread = null;

    public GifImageView(Context context) {
        super(context);
    }

    public GifImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GifImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setGifs(int... resourceId) {
        recycle();
        resIds.clear();
        int len = resourceId.length;
        for (int i = 0; i < len; i++) {
            resIds.add(resourceId[i]);
        }
        if (wThread == null) {
            wThread = new Thread(writeRunnable);
        }
        stop = false;
        wThread.start();
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public void setFps(long fps) {
        this.fps = fps;
    }

    public void stop() {
        stop = true;
    }

    public boolean isStop() {
        return stop;
    }

    public void recycle() {
        stop = true;
        if (mBitmap != null && !mBitmap.isRecycled()) {
            setImageBitmap(null);
            mBitmap.recycle();
            mBitmap = null;
        }
    }

    public void start() {
        if (resIds != null && !resIds.isEmpty()) {
            if (wThread == null) {
                wThread = new Thread(writeRunnable);
            }
            stop = false;
            wThread.start();
        }
    }

    private Runnable writeRunnable = new Runnable() {
        @Override
        public void run() {
            while (!stop && loop) {
                try {
                    int len = resIds.size();
                    for (int i = 0; i < len; i++) {
                        if (!stop && loop) {
                            int resId = resIds.get(i);
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inPreferredConfig = Bitmap.Config.RGB_565;
                            options.inMutable = true;
                            if (mBitmap != null) {
                                options.inBitmap = mBitmap;
                            }
                            mBitmap = BitmapFactory.decodeResource(getResources(), resId, options);
                            Thread.sleep(fps);
                            GifImageView.this.post(new Runnable() {
                                @Override
                                public void run() {
                                    GifImageView.this.setImageBitmap(mBitmap);
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };
}

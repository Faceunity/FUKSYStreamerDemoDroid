package com.ksyun.media.streamer.demo;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.faceunity.nama.FURenderer;
import com.ksyun.media.streamer.filter.imgtex.ImgFilterBase;
import com.ksyun.media.streamer.framework.ImgTexFormat;
import com.ksyun.media.streamer.framework.ImgTexFrame;
import com.ksyun.media.streamer.framework.SinkPin;
import com.ksyun.media.streamer.framework.SrcPin;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 自定义美颜，使用 Faceunity 渲染
 */
public class FaceunityFilter extends ImgFilterBase {
    private static final String TAG = "FaceunityFilter";
    private SrcPin<ImgTexFrame> mSrcPin;
    private SinkPin<ImgTexFrame> mSinkPin;
    private boolean mIsSurfaceCreated;

    public FaceunityFilter(final FURenderer fuRenderer) {
        mSrcPin = new SrcPin<>();
        mSinkPin = new SinkPin<ImgTexFrame>() {
            private Handler mHandler;
            private int mSkippedFrames;

            @Override
            public void onFormatChanged(Object format) {
                mSrcPin.onFormatChanged(format);
                ImgTexFormat imgTexFormat = ((ImgTexFormat) format);
                Log.d(TAG, "onFormatChanged() imgTexFormat colorFormat:" + imgTexFormat.colorFormat
                        + ", width:" + imgTexFormat.width + ", height:" + imgTexFormat.height + ", looper:" + Looper.myLooper());
                if (!mIsSurfaceCreated) {
                    mHandler = new Handler(Looper.myLooper());
                    mIsSurfaceCreated = true;
                    fuRenderer.onSurfaceCreated();
                }
                mSkippedFrames = 3;
            }

            @Override
            public void onFrameAvailable(final ImgTexFrame frame) {
                if (mSrcPin.isConnected()) {
                    int texture = fuRenderer.onDrawFrameSingleInput(frame.textureId, frame.format.width, frame.format.height);
                    if (mSkippedFrames > 0) {
                        mSkippedFrames--;
                    } else {
                        mSrcPin.onFrameAvailable(new ImgTexFrame(frame.format, texture, frame.texMatrix, frame.pts));
                    }
                }
            }

            @Override
            public void onDisconnect(boolean recursive) {
                Log.d(TAG, "onDisconnect() called with: recursive = [" + recursive + "]");
                final CountDownLatch countDownLatch = new CountDownLatch(1);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        fuRenderer.onSurfaceDestroyed();
                        countDownLatch.countDown();
                    }
                });
                try {
                    countDownLatch.await(1000, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mIsSurfaceCreated = false;
                mSrcPin.disconnect(recursive);
            }
        };
    }

    @Override
    public int getSinkPinNum() {
        return 1;
    }

    @Override
    public SinkPin<ImgTexFrame> getSinkPin(int i) {
        return mSinkPin;
    }

    @Override
    public SrcPin<ImgTexFrame> getSrcPin() {
        return mSrcPin;
    }
}

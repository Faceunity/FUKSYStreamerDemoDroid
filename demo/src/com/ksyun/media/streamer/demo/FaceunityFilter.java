package com.ksyun.media.streamer.demo;

import android.util.Log;

import com.faceunity.nama.FURenderer;
import com.ksyun.media.streamer.filter.imgtex.ImgFilterBase;
import com.ksyun.media.streamer.framework.ImgTexFormat;
import com.ksyun.media.streamer.framework.ImgTexFrame;
import com.ksyun.media.streamer.framework.SinkPin;
import com.ksyun.media.streamer.framework.SrcPin;

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
            private int mSkippedFrames;

            @Override
            public void onFormatChanged(Object format) {
                mSrcPin.onFormatChanged(format);
                ImgTexFormat imgTexFormat = ((ImgTexFormat) format);
                Log.d(TAG, "onFormatChanged() imgTexFormat colorFormat:" + imgTexFormat.colorFormat
                        + ", width:" + imgTexFormat.width + ", height:" + imgTexFormat.height);
                if (!mIsSurfaceCreated) {
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
                fuRenderer.onSurfaceDestroyed();
                mSrcPin.disconnect(recursive);
                mIsSurfaceCreated = false;
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

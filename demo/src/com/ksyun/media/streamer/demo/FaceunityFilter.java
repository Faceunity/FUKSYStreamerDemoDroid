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
    private SrcPin<ImgTexFrame> srcPin;
    private SinkPin<ImgTexFrame> sinkPin;
    private boolean mIsSurfaceCreated;

    public FaceunityFilter(final FURenderer fuRenderer) {
        srcPin = new SrcPin<>();

        sinkPin = new SinkPin<ImgTexFrame>() {
            @Override
            public void onFormatChanged(Object format) {
                ImgTexFormat imgTexFormat = ((ImgTexFormat) format);
                Log.d(TAG, "onFormatChanged() imgTexFormat colorFormat:" + imgTexFormat.colorFormat
                        + ", width:" + imgTexFormat.width + ", height:" + imgTexFormat.height);
                if (!mIsSurfaceCreated) {
                    fuRenderer.onSurfaceCreated();
                    mIsSurfaceCreated = true;
                }
                srcPin.onFormatChanged(format);
            }

            @Override
            public void onFrameAvailable(ImgTexFrame frame) {
                if (srcPin.isConnected()) {
                    int texture = fuRenderer.onDrawFrameSingleInput(frame.textureId, frame.format.width, frame.format.height);
                    srcPin.onFrameAvailable(new ImgTexFrame(frame.format, texture, frame.texMatrix, frame.pts));
                }
            }

            @Override
            public void onDisconnect(boolean recursive) {
                Log.d(TAG, "onDisconnect() called with: recursive = [" + recursive + "]");
                if (recursive) {
                    fuRenderer.onSurfaceDestroyed();
                    mIsSurfaceCreated = false;
                }
            }
        };
    }

    @Override
    public int getSinkPinNum() {
        return 1;
    }

    @Override
    public SinkPin<ImgTexFrame> getSinkPin(int i) {
        return sinkPin;
    }

    @Override
    public SrcPin<ImgTexFrame> getSrcPin() {
        return srcPin;
    }
}

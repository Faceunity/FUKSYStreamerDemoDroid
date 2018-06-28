package com.ksyun.media.streamer.demo;

import android.hardware.Camera;

import com.faceunity.beautycontrolview.FURenderer;
import com.ksyun.media.streamer.filter.imgtex.ImgFilterBase;
import com.ksyun.media.streamer.framework.ImgTexFrame;
import com.ksyun.media.streamer.framework.SinkPin;
import com.ksyun.media.streamer.framework.SrcPin;

public class FaceunityFilter extends ImgFilterBase {

    private SrcPin<ImgTexFrame> srcPin;
    private SinkPin<ImgTexFrame> sinkPin;

    private boolean init;

    private int cameraType = Camera.CameraInfo.CAMERA_FACING_FRONT;

    FaceunityFilter(final FURenderer fuRenderer) {
        srcPin = new SrcPin<>();

        sinkPin = new SinkPin<ImgTexFrame>() {
            @Override
            public void onFormatChanged(Object format) {
                if (!init) {
                    fuRenderer.loadItems();
                    init = true;
                } else {
                    fuRenderer.onCameraChange(cameraType = (cameraType == Camera.CameraInfo.CAMERA_FACING_FRONT ? Camera.CameraInfo.CAMERA_FACING_BACK : Camera.CameraInfo.CAMERA_FACING_FRONT), 0);
                }

                srcPin.onFormatChanged(format);
            }

            @Override
            public void onFrameAvailable(ImgTexFrame frame) {
                if (srcPin.isConnected()) {
                    int texture = fuRenderer.drawFrame(frame.textureId, frame.format.width, frame.format.height);

                    srcPin.onFrameAvailable(new ImgTexFrame(frame.format, texture, frame.texMatrix, frame.pts));
                }
            }

            @Override
            public void onDisconnect(boolean recursive) {
                if (recursive) {
                    fuRenderer.destroyItems();
                    init = false;
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

package com.ksyun.media.streamer.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.faceunity.nama.FURenderer;
import com.ksyun.media.streamer.demo.utils.PreferenceUtil;


public class NeedFaceUnityAcct extends AppCompatActivity {

    private boolean isOn = true;//是否使用FaceUnity

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_faceunity);

        final Button button = (Button) findViewById(R.id.btn_set);
        String isOpen = PreferenceUtil.getString(DemoApplication.getInstance(), PreferenceUtil.KEY_FACEUNITY_ISON);
        if (TextUtils.isEmpty(isOpen) || isOpen.equals("false")) {
            isOn = false;
        } else {
            isOn = true;
        }
        button.setText(isOn ? "On" : "Off");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOn = !isOn;
                button.setText(isOn ? "On" : "Off");
            }
        });

        Button btn_to_main = (Button) findViewById(R.id.btn_to_main);
        btn_to_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOn) {
                    FURenderer.setup(getApplicationContext());
                }
                Intent intent = new Intent(NeedFaceUnityAcct.this, DemoActivity.class);
                PreferenceUtil.persistString(DemoApplication.getInstance(), PreferenceUtil.KEY_FACEUNITY_ISON,
                        isOn + "");
                startActivity(intent);
                finish();
            }
        });

    }
}

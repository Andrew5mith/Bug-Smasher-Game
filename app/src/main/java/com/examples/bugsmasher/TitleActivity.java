package com.examples.bugsmasher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;


public class TitleActivity extends Activity implements View.OnClickListener {
    Bitmap bmp;

    private Button startButton;
    private Button scoresButton;
    private ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);
        bmp = null;

        imageView = (ImageView) findViewById(R.id.imageView);
        startButton = (Button) findViewById(R.id.startButton);
        scoresButton = (Button) findViewById(R.id.scoresButton);

        startButton.setOnClickListener(this);
        scoresButton.setOnClickListener(this);
    }


    @Override
    public void onClick (View v) {
        int viewId = v.getId();

        switch(viewId) {
            case R.id.startButton:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.scoresButton:
                Intent intent2 = new Intent(this, SettingsActivity.class);
                startActivity(intent2);
                break;

        }
    }
}

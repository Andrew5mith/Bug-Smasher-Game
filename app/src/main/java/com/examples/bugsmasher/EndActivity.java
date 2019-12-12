package com.examples.bugsmasher;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class EndActivity extends Activity implements View.OnClickListener {

    Bitmap bmp;

    private Button returnButton;
    private Button homeButton;
    private ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
        bmp = null;
        imageView = (ImageView) findViewById(R.id.imageView);
        returnButton = (Button) findViewById(R.id.returnButton);
        homeButton = (Button) findViewById(R.id.homeButton);
        returnButton.setOnClickListener(this);
        homeButton.setOnClickListener(this);

    }


    @Override
    public void onClick (View v) {
        int viewId = v.getId();

        switch(viewId) {
            case R.id.returnButton:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.homeButton:
                Intent intent2 = new Intent(this, TitleActivity.class);
                startActivity(intent2);
                break;

        }
    }
}

package com.examples.bugsmasher;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import static com.examples.bugsmasher.Assets.bug;
import static com.examples.bugsmasher.Assets.soundPool;
import static com.examples.bugsmasher.Assets.sound_gameaudio;


public class MainThread extends Thread {
    private SurfaceHolder holder;
    private Handler handler;		// required for running code in the UI thread
    private boolean isRunning = false;
    Context context;
    Paint paint;
    int touchx, touchy;	// x,y of touch event
    boolean touched;	// true if touch happened
    boolean loaded = false;
    boolean data_initialized;
    private static final Object lock = new Object();

    public MainThread (SurfaceHolder surfaceHolder, Context context) {
        holder = surfaceHolder;
        this.context = context;
        handler = new Handler();
        data_initialized = false;
        touched = false;
    }

    public void setRunning(boolean b) {
        isRunning = b;	// no need to synchronize this since this is the only line of code to writes this variable
    }

    // Set the touch event x,y location and flag indicating a touch has happened
    public void setXY (int x, int y) {
        synchronized (lock) {
            touchx = x;
            touchy = y;
            this.touched = true;
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            // Lock the canvas before drawing
            Canvas canvas = holder.lockCanvas();
            if (canvas != null) {
                // Perform drawing operations on the canvas
                render(canvas);
                // After drawing, unlock the canvas and display it
                holder.unlockCanvasAndPost (canvas);
            }
        }
    }

    // Loads graphics, etc. used in game
    private void loadData (Canvas canvas) {
        Bitmap bmp;
        int newWidth, newHeight;
        float scaleFactor;
        paint = new Paint();

        // Load roach1
        bmp = BitmapFactory.decodeResource (context.getResources(), R.drawable.roach1_250);
        // Compute size of bitmap needed (suppose want width = 20% of screen width)
        newWidth = (int)(canvas.getWidth() * 0.2f);
        // What was the scaling factor to get to this?
        scaleFactor = (float)newWidth / bmp.getWidth();
        // Compute the new height
        newHeight = (int)(bmp.getHeight() * scaleFactor);
        // Scale it to a new size
        Assets.roach1 = Bitmap.createScaledBitmap (bmp, newWidth, newHeight, false);
        // Delete the original
        bmp = null;

        // Load the other bitmaps similarly
        bmp = BitmapFactory.decodeResource (context.getResources(), R.drawable.roach2_250);
        Assets.roach2 = Bitmap.createScaledBitmap (bmp, newWidth, newHeight, false);
        // Delete the original
        bmp = null;

        // Load roach3 (dead bug)
        bmp = BitmapFactory.decodeResource (context.getResources(), R.drawable.roach3_250);
        // Compute size of bitmap needed (suppose want width = 20% of screen width)
        newWidth = (int)(canvas.getWidth() * 0.2f);
        // What was the scaling factor to get to this?
        scaleFactor = (float)newWidth / bmp.getWidth();
        // Compute the new height
        newHeight = (int)(bmp.getHeight() * scaleFactor);
        // Scale it to a new size
        Assets.roach3 = Bitmap.createScaledBitmap (bmp, newWidth, newHeight, false);
        // Delete the original
        bmp = null;

        bmp = BitmapFactory.decodeResource (context.getResources(), R.drawable.life_icon);
        newWidth = (int)(canvas.getWidth() * 0.05f);
        scaleFactor = (float)newWidth / bmp.getWidth();
        newHeight = (int)(bmp.getHeight() * scaleFactor);
        Assets.life_icon = Bitmap.createScaledBitmap(bmp, newWidth, newHeight,false);
        bmp = null;

        Assets.bugs = new ArrayList<Bug>();

        for(int i=0;i<5;i++) Assets.bugs.add(new Bug());
    }

    // Load specific background screen
    private void loadBackground (Canvas canvas, int resId) {
        // Load background
        Bitmap bmp = BitmapFactory.decodeResource (context.getResources(), resId);
        // Scale it to fill entire canvas
        Assets.background = Bitmap.createScaledBitmap (bmp, canvas.getWidth(), canvas.getHeight(), false);
        // Delete the original
        bmp = null;
    }

    private void render (Canvas canvas) {
        int i, x, y;

        if (! data_initialized) {
            loadData(canvas);
            data_initialized = true;
        }

        switch (Assets.state) {
            case GettingReady:
                loadBackground (canvas, R.drawable.table);
                // Draw the background screen
                canvas.drawBitmap (Assets.background, 0, 0, null);
                // Play a sound effect
                Assets.soundPool.play(Assets.sound_getready, 1, 1, 1, 0, 1);
                // Start a timer
                Assets.gameTimer = System.nanoTime() / 1000000000f;
                // Go to next state
                Assets.state = Assets.GameState.Starting;
                break;
            case Starting:
                // Draw the background screen
                canvas.drawBitmap (Assets.background, 0, 0, null);
                // Has 3 seconds elapsed?
                float currentTime = System.nanoTime() / 1000000000f;
                if (currentTime - Assets.gameTimer >= 3)
                    // Goto next state
                    Assets.state = Assets.GameState.Running;
                break;
            case Running:
                canvas.drawBitmap (Assets.background, 0, 0, null);
                paint.setColor(Color.BLACK);
                paint.setStyle(Paint.Style.FILL);
                paint.setTextSize(60);
                canvas.drawText(Assets.scoreString, 10, 80, paint);
                paint.setTextSize(30);
                canvas.drawText(Assets.pauseButton, 10, 120, paint);

                int radius = (int)(canvas.getWidth() * 0.05f);
                int spacing = 8; // spacing in between circles
                x = canvas.getWidth() - radius - spacing;	// coordinates for rightmost circle to draw
                y = radius + spacing;
                for (i=0; i<Assets.livesLeft; i++) {

                    canvas.drawBitmap(Assets.life_icon, x, y, null);
                    x -= (radius*2 + spacing);
                }

                for(int j=0;j<Assets.bugs.size();j++) {

                    if (touched) {
                        boolean bugKilled = Assets.bugs.get(j).touched(canvas, touchx, touchy);
                        if (bugKilled) {
                            Assets.currentScore++;
                            Assets.scoreString = "Score: " + Assets.currentScore;
                            Assets.soundPool.play(Assets.sound_squish1, 1, 1, 1, 0, 1);
                        } else
                            Assets.soundPool.play(Assets.sound_punch, 1, 1, 1, 0, 1);

                    }

                    Assets.bugs.get(j).drawDead(canvas);
                    // Move bugs on screen
                    Assets.bugs.get(j).move(canvas);
                    // Bring a dead bug to life?
                    Assets.bugs.get(j).birth(canvas);
                }
                touched = false;

                if (Assets.livesLeft == 0)
                    // Goto next state
                    Assets.state = Assets.GameState.GameEnding;
                break;
            case GameEnding:
                soundPool.stop(sound_gameaudio);
                if(Assets.currentScore > Assets.highScore)
                    Assets.highScore = Assets.currentScore;
                // Goto next state
                Assets.state = Assets.GameState.GameOver;
                break;
            case GameOver:
                // Fill the entire canvas' bitmap with 'black'
                Intent gameOverScreen = new Intent(context, EndActivity.class);
                context.startActivity(gameOverScreen);
                //canvas.drawColor(Color.BLACK);
                break;
        }
    }
}

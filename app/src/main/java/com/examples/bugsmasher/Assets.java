package com.examples.bugsmasher;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.SoundPool;


import java.util.ArrayList;
import java.util.List;

public class Assets {
    static Bitmap background;
    static Bitmap roach1;
    static Bitmap roach2;
    static Bitmap roach3;
    static Bitmap life_icon;


    // States of the Game Screen
    enum GameState {
        GettingReady,
        Starting,
        Running,
        GameEnding,
        GameOver,
    };
    static GameState state;
    static float gameTimer;
    static int livesLeft;

    static SoundPool soundPool;
    static MediaPlayer mediaPlayer;
    static int sound_getready;
    static int sound_squish1;
    static int sound_squish2;
    static int sound_squish3;
    static int sound_punch;
    static int sound_gameaudio;
    static int sound_ahcrap;
    static int currentScore;
    static String scoreString;
    static int highScore;
    static Bug bug;
    static ArrayList<Bug> bugs;
    static String pauseButton = "Pause";
}

package com.examples.bugsmasher;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {
    MainView v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Disable the title
        //requestWindowFeature (Window.FEATURE_NO_TITLE);  // use the styles.xml file to set no title bar
        // Make full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Start the view
        v = new MainView(this);
        setContentView(v);

        Assets.currentScore = 0;
        Assets.scoreString = "Score: 0";
    }

    @Override
    protected void onSaveInstanceState(Bundle outBundle) {
        super.onSaveInstanceState(outBundle);
    }

    @Override
    protected void onPause () {
        super.onPause();
        v.pause();
        if (isFinishing()) {
            if (Assets.mediaPlayer != null) {
                Assets.mediaPlayer.stop();
                Assets.mediaPlayer.release();
                Assets.mediaPlayer = null;
            }
        }

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("key_highScore", Assets.highScore);
        editor.commit();

        Assets.highScore = prefs.getInt("key_highScore", 0);
    }

    @Override
    protected void onResume () {
        super.onResume();
        v.resume();

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean b = prefs.getBoolean("key_music_enabled", true);

        Assets.highScore = prefs.getInt("key_highscore", 0);

        if (b == true) {
            if (Assets.mediaPlayer != null) {
                Assets.mediaPlayer.release();
                Assets.mediaPlayer = null;
            }
            Assets.mediaPlayer = MediaPlayer.create (this, R.raw.gameaudio);
            Assets.mediaPlayer.setLooping(true);
            Assets.mediaPlayer.start();
        }
    }
}

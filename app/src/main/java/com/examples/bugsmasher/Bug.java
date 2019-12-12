package com.examples.bugsmasher;

import android.graphics.Canvas;

public class Bug {

    // States of a Bug
    enum BugState {
        Dead,
        ComingBackToLife,
        Alive, 			    // in the game
        DrawDead,			// draw dead body on screen
    };

    BugState state;			// current state of bug
    int x,y;				// location on screen (in screen coordinates)
    double speed;			// speed of bug (in pixels per second)
    // All times are in seconds
    float timeToBirth;		// # seconds till birth
    float startBirthTimer;	// starting timestamp when decide to be born
    float deathTime;		// time of death
    float animateTimer;
           // used to move and animate the bug

    // Bug starts not alive
    public Bug  () {
        state = BugState.Dead;
    }

    // Bug birth processing
    public void birth (Canvas canvas) {
        // Bring a bug to life?
        if (state == BugState.Dead) {
            // Set it to coming alive
            state = BugState.ComingBackToLife;
            // Set a random number of seconds before it comes to life
            timeToBirth = (float)Math.random () * 4;
            // Note the current time
            startBirthTimer = System.nanoTime() / 1000000000f;
        }
        // Check if bug is alive yet
        else if (state == BugState.ComingBackToLife) {
            float curTime = System.nanoTime() / 1000000000f;
            // Has birth timer expired?
            if (curTime - startBirthTimer >= timeToBirth) {
                // If so, then bring bug to life
                state = BugState.Alive;
                // Set bug starting location at top of screen
                x = (int)(Math.random() * canvas.getWidth());
                // Keep entire bug on screen
                if (x < Assets.roach1.getWidth()/2)
                    x = Assets.roach1.getWidth()/2;
                else if (x > canvas.getWidth() - Assets.roach1.getWidth()/2)
                    x = canvas.getWidth() - Assets.roach1.getWidth()/2;
                y = 0;
                // Set speed of this bug
                speed = canvas.getHeight() / (4 + Math.random() * 8);
                animateTimer = curTime;
            }
        }
    }

    // Bug movement processing
    public void move (Canvas canvas) {
        // Make sure this bug is alive
        if (state == BugState.Alive) {
            // Get elapsed time since last call here
            float curTime = System.nanoTime() / 1000000000f;
            float elapsedTime = curTime - animateTimer;
            animateTimer = curTime;
            // Compute the amount of pixels to move (vertically down the screen)
            y += (speed * elapsedTime);
            // Draw bug on screen
            long divTime = System.currentTimeMillis() / 100 % 10;
            if (divTime % 2 == 0)
                canvas.drawBitmap(Assets.roach1, x-Assets.roach1.getWidth()/2,  y-Assets.roach1.getHeight()/2, null);
            else
                canvas.drawBitmap(Assets.roach2, x-Assets.roach2.getWidth()/2,  y-Assets.roach2.getHeight()/2, null);

            // Has it reached the bottom of the screen?
            if (y >= canvas.getHeight()) {
                // Kill the bug
                state = BugState.Dead;
                // Subtract 1 life
                Assets.soundPool.play(Assets.sound_ahcrap, 1, 1, 1, 0, 1);
                Assets.livesLeft--;
            }
        }
    }

    // Process touch to see if kills bug - return true if bug killed
    public boolean touched (Canvas canvas, int touchx, int touchy) {
        boolean touched = false;
        // Make sure this bug is alive
        if (state == BugState.Alive) {
            // Compute distance between touch and center of bug
            float dis = (float)(Math.sqrt ((touchx - x) * (touchx - x) + (touchy - y) * (touchy - y)));
            // Is this close enough for a kill?
            if (dis <= Assets.roach1.getWidth()*0.5f) {
                state = BugState.DrawDead;	// need to draw dead body on screen for a while
                touched = true;
                // Record time of death
                deathTime = System.nanoTime() / 1000000000f;

            }
        }
        return (touched);
    }

    // Draw dead bug body on screen, if needed
    public void drawDead (Canvas canvas) {
        if (state == BugState.DrawDead) {
            canvas.drawBitmap(Assets.roach3, x-Assets.roach1.getWidth()/2,  y-Assets.roach1.getHeight()/2, null);
            // Get time since death
            float curTime = System.nanoTime() / 1000000000f;
            float timeSinceDeath = curTime - deathTime;
            // Drawn dead body long enough (4 seconds) ?
            if (timeSinceDeath > 4)
                state = BugState.Dead;
        }
    }
}

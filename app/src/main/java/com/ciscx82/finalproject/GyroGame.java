package com.ciscx82.finalproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.appcompat.app.AppCompatActivity;

public class GyroGame extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private GameView gameView;
    private boolean surfaceReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the game view
        gameView = new GameView(this);
        setContentView(gameView);

        // Initialize the gyroscope sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (gyroscopeSensor != null) {
            sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            // Pass accelerometer values to the game view
            gameView.updateBallPosition(event.values[0], event.values[1]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No implementation needed
    }

    // Inner class for the game view
    private class GameView extends SurfaceView implements SurfaceHolder.Callback {

        private Ball ball;
        private Paint paint;
        private Maze maze;

        public GameView(Context context) {
            super(context);
            getHolder().addCallback(this);
            paint = new Paint();
            ball = new Ball(100, 100, 20); // Ball with initial position and radius
            maze = new Maze(); // Initialize the maze
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.d("GameView", "Surface Created");
            surfaceReady = true;
            drawGame(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.d("GameView", "Surface Destroyed");
            surfaceReady = false;
        }

        private void drawGame(SurfaceHolder holder) {
            Canvas canvas = holder.lockCanvas();
            if (canvas != null) {
                try {
                    Log.d("GameView", "Drawing Game");
                    canvas.drawColor(Color.WHITE);

                    // Draw maze
                    maze.draw(canvas, paint);

                    // Draw ball
                    paint.setColor(Color.RED);
                    canvas.drawCircle(ball.x, ball.y, ball.radius, paint);
                } finally {
                    holder.unlockCanvasAndPost(canvas);
                }
            } else {
                Log.w("GameView", "Canvas is null, skipping draw");
            }
        }


        public void updateBallPosition(float tiltX, float tiltY) {
            float previousX = ball.x;
            float previousY = ball.y;

            ball.update(tiltX, tiltY, getWidth(), getHeight(), maze);

            // Only redraw if the ball has moved significantly to prevent crashes
            if (Math.abs(ball.x - previousX) > 1 || Math.abs(ball.y - previousY) > 1) {
                drawGame(getHolder());
            }
        }

    }

    // Ball class
    private class Ball {
        float x, y, radius;

        public Ball(float x, float y, float radius) {
            this.x = x;
            this.y = y;
            this.radius = radius;
        }

        public void update(float tiltX, float tiltY, int screenWidth, int screenHeight, Maze maze) {
            // Update position based on tilt
            x -= tiltX * 5; // Adjust sensitivity by multiplying tilt values
            y += tiltY * 5;

            // Keep the ball within screen bounds
            x = Math.max(radius, Math.min(screenWidth - radius, x));
            y = Math.max(radius, Math.min(screenHeight - radius, y));

            // Check for collisions with maze walls (Not yet implemented)
            if (maze.collidesWithWall(x, y, radius)) {
                // Handle collision reaction
            }
        }
    }

    // Maze class
    private class Maze {
        public void draw(Canvas canvas, Paint paint) {
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(10);
            // Example walls
            canvas.drawLine(200, 200, 500, 200, paint);
            canvas.drawLine(500, 200, 500, 500, paint);
            canvas.drawLine(500, 500, 200, 500, paint);
            canvas.drawLine(200, 500, 200, 200, paint);
        }

        public boolean collidesWithWall(float x, float y, float radius) {
            // Wall positions
            float left = 200;
            float top = 200;
            float right = 500;
            float bottom = 500;

            // Check for collision with walls
            if (x - radius < left || x + radius > right || y - radius < top || y + radius > bottom) {
                Log.d("Collision", "Collision detected");
                return true; // Collision detected
            }
            Log.d("Collision", "No collision detected");
            return false; // No collision
        }
    }
}


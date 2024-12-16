package com.ciscx82.finalproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class GyroGame extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private GameView gameView;

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
        if (gameView != null && gameView.isSurfaceReady()) {
            float tiltX = event.values[0];
            float tiltY = event.values[1];

            // Pass values to gameView for processing
            gameView.post(() -> gameView.updateBallPosition(tiltX, tiltY));
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No implementation needed
    }

    public void endGame() {
        Log.d("GyroGame", "Game Over: Ball reached the goal!");
        runOnUiThread(() -> {
            // Show a toast message
            Toast.makeText(this, "You Win!", Toast.LENGTH_LONG).show();

            // Close game
            Intent i = new Intent(GyroGame.this, MainActivity.class);
            GyroGame.this.startActivity(i);
        });
    }


    // Inner class for the game view
    private class GameView extends SurfaceView implements SurfaceHolder.Callback {

        private Ball ball;
        private Paint paint;
        private Maze maze;
        private Goal goal;
        private boolean surfaceReady = false;

        public GameView(Context context) {
            super(context);
            getHolder().addCallback(this);
            paint = new Paint();
            ball = new Ball(100, 100, 20); // Ball with initial position and radius
            maze = new Maze(); // Initialize the maze
            goal = new Goal(800, 1900, 20); // Goal position
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
            surfaceReady = false;
        }

        public boolean isSurfaceReady() {
            return surfaceReady;
        }

        private void drawGame(SurfaceHolder holder) {
            Canvas canvas = holder.lockCanvas();
            if (canvas != null) {
                try {
                    //Log.d("GameView", "Drawing Game");
                    canvas.drawColor(Color.WHITE);

                    // Draw maze
                    maze.draw(canvas, paint);

                    // Draw goal
                    goal.draw(canvas, paint);

                    // Draw ball
                    ball.draw(canvas, paint);
                } finally {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
        }


        public void updateBallPosition(float tiltX, float tiltY) {
            float previousX = ball.x;
            float previousY = ball.y;

            ball.update(tiltX, tiltY, getWidth(), getHeight(), maze);

            // Check if ball reached the goal
            if (goal.isBallInside(ball.x, ball.y, ball.radius)) {
                Log.d("GameView", "Goal Reached!");
                // End the game
                ((GyroGame) getContext()).endGame();
                return;
            }

            // Only redraw if the ball has moved significantly to prevent crashes
            if (Math.abs(ball.x - previousX) > 0.5 || Math.abs(ball.y - previousY) > 0.5) {
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

        public void draw(Canvas canvas, Paint paint) {
            paint.setColor(Color.RED);
            canvas.drawCircle(x, y, radius, paint);
        }

        public void update(float tiltX, float tiltY, int screenWidth, int screenHeight, Maze maze) {
            float newX = x - tiltX * 5; // Calculate new X position
            float newY = y + tiltY * 5; // Calculate new Y position

            // Check for collisions with walls before updating position
            if (!maze.collidesWithWall(newX, y, radius)) {
                x = Math.max(radius, Math.min(screenWidth - radius, newX)); // Update X if no collision
            }

            if (!maze.collidesWithWall(x, newY, radius)) {
                y = Math.max(radius, Math.min(screenHeight - radius, newY)); // Update Y if no collision
            }
        }

    }

    // Maze class
    private class Maze {
        private List<RectF> walls;

        public Maze() {
            walls = new ArrayList<>();

            // Define the maze walls as rectangles
            walls.add(new RectF(0, 700, 700, 710)); // Horizontal wall
            walls.add(new RectF(490, 0, 500, 500)); // Vertical wall
            walls.add(new RectF(200, 490, 500, 500)); // Horizontal wall
            walls.add(new RectF(200, 0, 210, 500)); // Vertical wall
            walls.add(new RectF(700, 700, 710, 2000));
            walls.add(new RectF(900, 0, 910, 900));
            walls.add(new RectF(900, 900, 1100, 910));
            walls.add(new RectF(900, 1100, 1100, 1110));
            walls.add(new RectF(900, 1100, 910, 2000));
        }

        public void draw(Canvas canvas, Paint paint) {
            paint.setColor(Color.BLACK);
            for (RectF wall : walls) {
                canvas.drawRect(wall, paint); // Draw each wall as a rectangle
            }
        }

        public boolean collidesWithWall(float x, float y, float radius) {
            for (RectF wall : walls) {
                // Check if the ball intersects with any wall
                if ((x + radius > wall.left) && (x - radius < wall.right) && (y + radius > wall.top) && (y - radius < wall.bottom)) {
                    //Log.d("Collision", "Collision with wall: " + wall.toString());
                    return true; // Collision detected
                }
            }
            return false; // No collision
        }
    }


    private class Goal {
        float x, y, size;

        public Goal(float x, float y, float size) {
            this.x = x;
            this.y = y;
            this.size = size;
        }

        public void draw(Canvas canvas, Paint paint) {
            paint.setColor(Color.GREEN);
            canvas.drawRect(x, y, x + size, y + size, paint);
        }

        public boolean isBallInside(float ballX, float ballY, float ballRadius) {
            return ballX + ballRadius > x && ballX - ballRadius < x + size &&
                    ballY + ballRadius > y && ballY - ballRadius < y + size;
        }
    }
}


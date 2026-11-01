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
import android.util.Pair;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class GyroGame extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private GameView gameView;
    private String difficulty = "easy";

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

    public String getDifficulty() {
        return this.difficulty;
    }
    public void setDifficulty(String diff) {
        this.difficulty = diff;
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
                    maze.draw(canvas, paint, getWidth(), getHeight());

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
        float x, y, radius, xVel, yVel, xAccel, yAccel;
        float friction = 0.2f, maxVel = 12.0f;

        public Ball(float x, float y, float radius) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.xVel = 0;
            this.yVel = 0;
        }

        public void draw(Canvas canvas, Paint paint) {
            paint.setColor(Color.RED);
            canvas.drawCircle(x, y, radius, paint);
        }

        public void update(float tiltX, float tiltY, int screenWidth, int screenHeight, Maze maze) {
            //Log.d("gyro", "screenWidth: "+screenWidth+", screenHeight: "+screenHeight);
            xAccel = update_accel(-tiltX); // set X acceleration based on tilt
            yAccel = update_accel(tiltY); // set Y acceleration based on tilt
            xVel = update_vel(xVel, xAccel); // update X velocity based on X acceleration
            yVel = update_vel(yVel, yAccel); // update Y velocity based on Y acceleration
            float newX = x + xVel; // Calculate new X position
            float newY = y + yVel; // Calculate new Y position

            // Check for collisions with walls before updating position
            var xCollision = maze.collidesWithWall(newX, y, radius);
            var yCollision = maze.collidesWithWall(x, newY, radius);
            if (xCollision == null) {
                x = Math.max(radius, Math.min(screenWidth - radius, newX)); // Update X if no collision
            } else {
                if (xVel < 0) {
                    x = xCollision.right + radius;
                } else {
                    x = xCollision.left - radius;
                }
            }

            if (yCollision == null) {
                y = Math.max(radius, Math.min(screenHeight - radius, newY)); // Update Y if no collision
            } else {
                if (yVel < 0) {
                    y = yCollision.bottom + radius;
                } else {
                    y = yCollision.top - radius;
                }
            }
        }

        private float update_accel(float tilt) {
            return tilt * 0.25f; // arbitrary coefficient, can change to increase/decrease how much tilt affects acceleration
        }

        private float update_vel(float vel, float accel) {
            var newVel = clamp_vel(vel + accel); // keeps velocity between hard-coded max and min
            if (newVel < 0) { // if velocity is less than friction, returns 0, otherwise returns difference between velocity and friction
                return Math.min(newVel, -friction) + friction;
            } else return Math.max(newVel, friction) - friction;
        }
        private float clamp_vel(float vel) {
            return Math.max(-maxVel, Math.min(maxVel, vel)); // arbitrary maxVel, can be changed for self-explanatory results
        }

    }

    // Maze class
    private class Maze {
        private List<RectF> walls;
        private HashMap<Pair<Integer, Integer>, Cell> cells;
        private int rows, columns;

        public Maze() {
            walls = new ArrayList<>();
            cells = new HashMap<Pair<Integer, Integer>, Cell>();
            switch (difficulty) {
                case "medium":
                    rows = 8; columns = 6; break;
                case "hard":
                    rows = 10; columns = 8; break;
                default:
                    rows = 6; columns = 4; break;
            }
        }

        public void generateMaze(int rows, int columns, int mazeWidth, int mazeHeight) {
            //walls.add(new RectF(0, 700, 700, 710)); // Horizontal wall
            //use prim's simplified algo btw!! https://en.wikipedia.org/wiki/Maze_generation_algorithm#Iterative_randomized_Prim's_algorithm_(without_stack,_without_sets)
            try {
                for (int row = 1, col = 1; row <= rows; col++) { // generate cells
                    makeCell(row, col);
                    if (col >= columns) {
                        row++;
                        col = 0;
                    }
                }
//                Log.d("gyro", "cells: "+cells);
                for (int row = 1, col = 1; row <= rows; col++) { // generate vertical walls
                    makeWall(row, col, true, mazeWidth, mazeHeight);
                    if (col + 1 == columns) {
                        row++;
                        col = 0;
                    }
                }
                for (int row = 1, col = 1; col <= columns; row++) { // generate horizontal walls
                    makeWall(row, col, false, mazeWidth, mazeHeight);
                    if (row + 1 == rows) {
                        col++;
                        row = 0;
                    }
                }
            } finally {
                for (int row = 1, col = 1; col <= columns; row++) { // generate cells
                    Log.d("gyro", "cell "+Pair.create(row, col)+": "+Objects.requireNonNull(cells.get(Pair.create(row, col))).adjacentWalls);
                    if (row >= rows) {
                        col++;
                        row = 0;
                    }
                }
                makeBorders(mazeHeight, mazeWidth);
            }
        }

        public void makeCell(int x, int y) {
            Cell newCell = new Cell(x, y);
            cells.put(Pair.create(x,y), newCell);
        }

        public void makeWall(int x, int y, boolean orientation, int mazeWidth, int mazeHeight) {
            Wall newWall = new Wall(x, y, orientation, mazeWidth, mazeHeight);
            newWall.addWall(walls, rows, columns);
            newWall.addAdjacencies(cells);
        }

        public void makeBorders(int mazeHeight, int mazeWidth) {
            walls.add(new RectF(5, 5, 20, mazeHeight)); // left wall
            walls.add(new RectF(5, 5,  mazeWidth, 20)); // ceiling
            walls.add(new RectF(mazeWidth - 20, 5, mazeWidth, mazeHeight)); // right wall
            walls.add(new RectF(5, mazeHeight - 20,  mazeWidth, mazeHeight)); // floor
        }

        public void draw(Canvas canvas, Paint paint, int screenWidth, int screenHeight) {
            generateMaze(rows, columns, screenWidth - 5, screenHeight - 5);
            paint.setColor(Color.BLACK);
            for (RectF wall : walls) {
                canvas.drawRect(wall, paint); // Draw each wall as a rectangle
            }
        }

        public RectF collidesWithWall(float x, float y, float radius) {
            for (RectF wall : walls) {
                // Check if the ball intersects with any wall
                if ((x + radius > wall.left) && (x - radius < wall.right) && (y + radius > wall.top) && (y - radius < wall.bottom)) {
                    //Log.d("Collision", "Collision with wall: " + wall.toString());
                    return wall; // Collision detected
                }
            }
            return null; // No collision
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

    private class Cell {
        int row, column; //
        List<Wall> adjacentWalls;

        public Cell(int row, int column) {
            this.row = row;
            this.column = column;
            this.adjacentWalls = new ArrayList<Wall>();
        }

        public void addCellToMaze(List<Cell> maze) {
            maze.add(this);
        }
    }

    private class Wall {
        int row, column; // vertical can be row 1-6 and column 1-3, horizontal can be row 1-5 and column 1-4
        boolean orientation; // true for vertical, false for horizontal
        int width = 10;
        int mazeWidth, mazeHeight;
        Cell subCell, superCell;

        public Wall(int row, int column, boolean orientation, int mazeWidth, int mazeHeight) {
            this.row = row;
            this.column = column;
            this.orientation = orientation;
            this.mazeWidth = mazeWidth;
            this.mazeHeight = mazeHeight;
        }

        @NonNull
        @Override
        public String toString() {
            if (orientation) {
                return "Vertical Wall <"+row+", "+column+">";
            } else {
                return "Horizontal Wall <" + row + ", " + column + ">";
            }
        }

        public void addWall(List<RectF> walls, int rows, int columns) {
            var colDist = mazeWidth/columns;
            var rowDist = mazeHeight/rows;
            var halfWidth = width/2;
            if (this.orientation) {
                walls.add(new RectF(column*colDist - halfWidth, (row-1)*rowDist + 5, column*colDist + halfWidth, row*rowDist)); // Vertical wall
            } else {
                walls.add(new RectF((column-1)*colDist + 5, row*rowDist - halfWidth, column*colDist, row*rowDist + halfWidth)); // Horizontal wall
            }
        }

        public void addAdjacencies(HashMap<Pair<Integer,Integer>, Cell> cells) {
            subCell = cells.get(Pair.create(row, column));
            if (this.orientation) {
                superCell = cells.get(Pair.create(row+1, column));
            } else {
                superCell = cells.get(Pair.create(row, column+1));
            }
            if (subCell != null) {
                subCell.adjacentWalls.add(this);
            }
            if (superCell != null) {
                superCell.adjacentWalls.add(this);
            }

        }
    }
}


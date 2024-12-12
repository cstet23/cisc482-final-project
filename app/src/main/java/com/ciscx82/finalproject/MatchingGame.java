package com.ciscx82.finalproject;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class MatchingGame extends AppCompatActivity {
    private int currentScreen = 0;
    private int score = 0;

    private int[] screens = {
            // Different game screens representing progression through the game: 0/5 - 5/5
            R.layout.screen_0
            /*
            R.layout.screen_1,
            R.layout.screen_2,
            R.layout.screen_3,
            R.layout.screen_4,
            R.layout.screen_final
             */
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(screens[currentScreen]);
        placeShapesRandomly();
    }

    public void onShapeClick(View view) {
        ImageView selectedShape = (ImageView) view;
        String tag = (String) selectedShape.getTag();

        if (isCorrectSelection(tag)) {
            score++;
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Try Again!", Toast.LENGTH_SHORT).show();
        }

        currentScreen++;
        if (currentScreen < screens.length) {
            setContentView(screens[currentScreen]);
            placeShapesRandomly();
        } else {
            showFinalScreen();
        }
    }

    private boolean isCorrectSelection(String tag) {
        switch (currentScreen) {
            case 0:
                return "red_circle".equals(tag);
            case 1:
                return "blue_triangle".equals(tag);
            case 2:
                return "blue_circle".equals(tag);
            case 3:
                return "red_triangle".equals(tag);
            case 4:
                return "blue_circle".equals(tag);
            default:
                return false;
        }
    }

    // Shape randomization and game mechanics need further testing
    private void placeShapesRandomly() {
        RelativeLayout layout = findViewById(R.id.matchingLayout); // Replace with your layout ID
        Random random = new Random();
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof ImageView) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) child.getLayoutParams();
                params.leftMargin = random.nextInt(layout.getWidth() - child.getWidth());
                params.topMargin = random.nextInt(layout.getHeight() - child.getHeight());
                child.setLayoutParams(params);
            }
        }
    }

    private void showFinalScreen() {
        // Show the final screen with the final score setContentView(R.layout.screen_final);
        TextView scoreTextView = findViewById(R.id.scoreTextView);
        scoreTextView.setText("Score: " + score + "/5");
    }
}

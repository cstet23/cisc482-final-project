package com.ciscx82.finalproject;



import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;

public class AnalogClockView extends View {

    private Paint paintCircle;
    private Paint paintHour;
    private Paint paintMinute;
    private Paint paintSecond;
    private Paint paintCenter;

    private int width;
    private int height;
    private float radius;

    private Handler handler;
    private Runnable runnable;

    public AnalogClockView(Context context) {
        super(context);
        init();
    }

    public AnalogClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnalogClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Initialize Paint objects
        paintCircle = new Paint();
        paintCircle.setColor(Color.BLACK);
        paintCircle.setStyle(Paint.Style.STROKE);
        paintCircle.setStrokeWidth(5f);
        paintCircle.setAntiAlias(true);

        paintHour = new Paint();
        paintHour.setColor(Color.BLACK);
        paintHour.setStyle(Paint.Style.STROKE);
        paintHour.setStrokeWidth(8f);
        paintHour.setAntiAlias(true);

        paintMinute = new Paint();
        paintMinute.setColor(Color.BLACK);
        paintMinute.setStyle(Paint.Style.STROKE);
        paintMinute.setStrokeWidth(6f);
        paintMinute.setAntiAlias(true);

        paintSecond = new Paint();
        paintSecond.setColor(Color.RED);
        paintSecond.setStyle(Paint.Style.STROKE);
        paintSecond.setStrokeWidth(4f);
        paintSecond.setAntiAlias(true);

        paintCenter = new Paint();
        paintCenter.setColor(Color.BLACK);
        paintCenter.setStyle(Paint.Style.FILL);
        paintCenter.setAntiAlias(true);

        // Initialize Handler for updating the clock every second
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                invalidate(); // Redraw the view
                handler.postDelayed(this, 1000); // Schedule next update in 1 second
            }
        };
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        handler.post(runnable); // Start the clock updates
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacks(runnable); // Stop the clock updates
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Determine the center and radius
        width = getWidth();
        height = getHeight();
        radius = Math.min(width, height) / 2f - 20f; // Padding of 20px

        // Draw the outer circle
        canvas.drawCircle(width / 2f, height / 2f, radius, paintCircle);

        // Get current time
        Calendar calendar = Calendar.getInstance();
        float hour = calendar.get(Calendar.HOUR);
        float minute = calendar.get(Calendar.MINUTE);
        float second = calendar.get(Calendar.SECOND);

        // Calculate angles
        float secondAngle = second * 6f; // 360 / 60
        float minuteAngle = (minute + second / 60f) * 6f; // 360 / 60
        float hourAngle = (hour + minute / 60f) * 30f; // 360 / 12

        // Draw hour hand
        float hourLength = radius * 0.5f;
        float hourX = (float) (width / 2f + hourLength * Math.sin(Math.toRadians(hourAngle)));
        float hourY = (float) (height / 2f - hourLength * Math.cos(Math.toRadians(hourAngle)));
        canvas.drawLine(width / 2f, height / 2f, hourX, hourY, paintHour);

        // Draw minute hand
        float minuteLength = radius * 0.7f;
        float minuteX = (float) (width / 2f + minuteLength * Math.sin(Math.toRadians(minuteAngle)));
        float minuteY = (float) (height / 2f - minuteLength * Math.cos(Math.toRadians(minuteAngle)));
        canvas.drawLine(width / 2f, height / 2f, minuteX, minuteY, paintMinute);

        // Draw second hand
        float secondLength = radius * 0.9f;
        float secondX = (float) (width / 2f + secondLength * Math.sin(Math.toRadians(secondAngle)));
        float secondY = (float) (height / 2f - secondLength * Math.cos(Math.toRadians(secondAngle)));
        canvas.drawLine(width / 2f, height / 2f, secondX, secondY, paintSecond);

        // Draw center circle
        canvas.drawCircle(width / 2f, height / 2f, 10f, paintCenter);
    }
}

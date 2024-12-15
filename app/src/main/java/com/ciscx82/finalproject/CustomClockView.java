package com.ciscx82.finalproject;


// CustomClockView.java

import android.util.Log;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;
import java.util.List;

public class CustomClockView extends View {

    private Paint paintCircle;
    private Paint paintHour;
    private Paint paintMinute;
    private Paint paintSecond;
    private Paint paintCenter;
    private Paint paintArc;

    private float width;
    private float height;
    private float radius;

    private Handler handler;
    private Runnable runnable;

    private List<Alarm> alarmList; // List of alarms from MainActivity

    private long nextAlarmTimeMillis = -1; // Timestamp of the next alarm

    public CustomClockView(Context context) {
        super(context);
        init();
    }

    public CustomClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomClockView(Context context, AttributeSet attrs, int defStyleAttr) {
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

        paintArc = new Paint();
        paintArc.setColor(Color.BLUE);
        paintArc.setStyle(Paint.Style.STROKE);
        paintArc.setStrokeWidth(10f);
        paintArc.setAntiAlias(true);
        paintArc.setStrokeCap(Paint.Cap.ROUND);

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

        // Get current time with milliseconds for smooth second hand
        long currentTimeMillis = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTimeMillis);
        float hour = calendar.get(Calendar.HOUR);
        float minute = calendar.get(Calendar.MINUTE);
        float second = calendar.get(Calendar.SECOND) + calendar.get(Calendar.MILLISECOND) / 1000f;

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

        // Draw the arc indicator for the next alarm
        if (nextAlarmTimeMillis > System.currentTimeMillis()) {
            float sweepAngle = calculateSweepAngle(currentTimeMillis, nextAlarmTimeMillis);
            RectF oval = new RectF(width / 2f - radius, height / 2f - radius, width / 2f + radius, height / 2f + radius);
            canvas.drawArc(oval, -90, sweepAngle, false, paintArc);
        } else {
            // No upcoming alarms; you may choose to hide or reset the arc
        }
    }

    /**
     * Calculates the sweep angle for the arc based on the duration until the next alarm.
     * The arc represents the proportion of time passed from now until the alarm.
     *
     * @param currentTimeMillis Current system time in milliseconds.
     * @param alarmTimeMillis   Alarm time in milliseconds.
     * @return Sweep angle in degrees.
     */
    private float calculateSweepAngle(long currentTimeMillis, long alarmTimeMillis) {
        long durationMillis = alarmTimeMillis - currentTimeMillis;
        // Let's represent the duration up to 12 hours (720 minutes)
        long maxDuration = 12 * 60 * 60 * 1000L; // 12 hours in milliseconds
        if (durationMillis > maxDuration) {
            durationMillis = maxDuration;
        }
        if (durationMillis < 0) {
            durationMillis = 0;
        }
        float proportion = (float) durationMillis / maxDuration;
        return 180f * proportion; // Half-circle is 180 degrees
    }

    /**
     * Sets the list of alarms and calculates the next alarm time.
     *
     * @param alarms List of alarms.
     */
    public void setAlarms(List<Alarm> alarms) {
        this.alarmList = alarms;
        calculateNextAlarm();
        invalidate(); // Redraw to update the arc
    }

    /**
     * Calculates the timestamp of the next alarm.
     */
    private void calculateNextAlarm() {
        if (alarmList == null || alarmList.isEmpty()) {
            nextAlarmTimeMillis = -1;
            return;
        }

        long currentTime = System.currentTimeMillis();
        long minDuration = Long.MAX_VALUE;
        long nextAlarm = -1;

        for (Alarm alarm : alarmList) {
            long alarmTime = alarm.getAlarmTimeInMillis();
            if (alarmTime > currentTime) {
                long duration = alarmTime - currentTime;
                if (duration < minDuration) {
                    minDuration = duration;
                    nextAlarm = alarmTime;
                }
            }
        }

        nextAlarmTimeMillis = nextAlarm;
        // Logging for debugging
        Log.d("CustomClockView", "Next Alarm Time (ms): " + nextAlarmTimeMillis);
    }
}

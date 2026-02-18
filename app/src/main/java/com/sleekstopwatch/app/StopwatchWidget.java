package com.sleekstopwatch.app;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class StopwatchWidget extends AppWidgetProvider {

    public static final String ACTION_WIDGET_START_STOP = "com.sleekstopwatch.app.WIDGET_START_STOP";
    public static final String ACTION_WIDGET_LAP_RESET = "com.sleekstopwatch.app.WIDGET_LAP_RESET";
    public static final String ACTION_WIDGET_UPDATE = "com.sleekstopwatch.app.WIDGET_UPDATE";

    // Shared state (kept in memory - service is source of truth)
    static String currentTime = "00:00";
    static String currentMillis = ".00";
    static boolean isRunning = false;
    static boolean hasTime = false;
    static String lastLap = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (ACTION_WIDGET_START_STOP.equals(action)) {
            Intent serviceIntent = new Intent(context, StopwatchService.class);
            serviceIntent.setAction(isRunning ? "PAUSE" : "START");
            context.startService(serviceIntent);
            handleStartStop(context);
        } else if (ACTION_WIDGET_LAP_RESET.equals(action)) {
            Intent serviceIntent = new Intent(context, StopwatchService.class);
            serviceIntent.setAction(isRunning ? "LAP" : "RESET");
            context.startService(serviceIntent);
            if (!isRunning && hasTime) {
                currentTime = "00:00";
                currentMillis = ".00";
                hasTime = false;
                lastLap = "";
            }
        } else if (ACTION_WIDGET_UPDATE.equals(action)) {
            // Update from service tick
        }

        super.onReceive(context, intent);
        updateAllWidgets(context);
    }

    private void handleStartStop(Context context) {
        if (isRunning) {
            isRunning = false;
        } else {
            isRunning = true;
            hasTime = true;
            // Start the ticker in widget
            startWidgetUpdater(context);
        }
    }

    private void startWidgetUpdater(Context context) {
        Intent intent = new Intent(context, WidgetUpdateService.class);
        context.startService(intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        updateAllWidgets(context);
    }

    public static void updateAllWidgets(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName component = new ComponentName(context, StopwatchWidget.class);
        int[] ids = manager.getAppWidgetIds(component);
        for (int id : ids) {
            updateWidget(context, manager, id);
        }
    }

    public static void updateWidget(Context context, AppWidgetManager manager, int widgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_stopwatch);

        views.setTextViewText(R.id.widget_tv_time, currentTime);
        views.setTextViewText(R.id.widget_tv_millis, currentMillis);
        views.setTextViewText(R.id.widget_tv_lap, lastLap.isEmpty() ? "" : "Lap: " + lastLap);

        // Start/Stop button
        if (isRunning) {
            views.setTextViewText(R.id.widget_btn_start_stop, "⏸");
            views.setInt(R.id.widget_btn_start_stop, "setBackgroundResource", R.drawable.widget_btn_pause);
        } else if (hasTime) {
            views.setTextViewText(R.id.widget_btn_start_stop, "▶");
            views.setInt(R.id.widget_btn_start_stop, "setBackgroundResource", R.drawable.widget_btn_start);
        } else {
            views.setTextViewText(R.id.widget_btn_start_stop, "▶");
            views.setInt(R.id.widget_btn_start_stop, "setBackgroundResource", R.drawable.widget_btn_start);
        }

        // Lap/Reset button
        if (isRunning) {
            views.setTextViewText(R.id.widget_btn_lap_reset, "⊙");
        } else if (hasTime) {
            views.setTextViewText(R.id.widget_btn_lap_reset, "↺");
        } else {
            views.setTextViewText(R.id.widget_btn_lap_reset, "⊙");
        }

        // Pending intents for buttons
        Intent startStopIntent = new Intent(context, StopwatchWidget.class);
        startStopIntent.setAction(ACTION_WIDGET_START_STOP);
        PendingIntent startStopPending = PendingIntent.getBroadcast(context, 0, startStopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_btn_start_stop, startStopPending);

        Intent lapResetIntent = new Intent(context, StopwatchWidget.class);
        lapResetIntent.setAction(ACTION_WIDGET_LAP_RESET);
        PendingIntent lapResetPending = PendingIntent.getBroadcast(context, 1, lapResetIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_btn_lap_reset, lapResetPending);

        // Open app on time tap
        Intent openIntent = new Intent(context, MainActivity.class);
        PendingIntent openPending = PendingIntent.getActivity(context, 2, openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_tv_time, openPending);

        manager.updateAppWidget(widgetId, views);
    }
}

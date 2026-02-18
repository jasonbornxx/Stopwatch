package com.sleekstopwatch.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import androidx.core.app.NotificationCompat;
import java.util.ArrayList;
import java.util.List;

public class StopwatchService extends Service {

    public static final String ACTION_TICK = "com.sleekstopwatch.app.TICK";
    public static final String ACTION_WIDGET_UPDATE = "com.sleekstopwatch.app.WIDGET_UPDATE";
    private static final String CHANNEL_ID = "stopwatch_channel";
    private static final int NOTIFICATION_ID = 1;

    private final IBinder binder = new LocalBinder();

    private long startTime = 0;
    private long elapsedBeforePause = 0;
    private boolean running = false;
    private List<Long> laps = new ArrayList<>();

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable tickRunnable = new Runnable() {
        @Override
        public void run() {
            if (running) {
                broadcastTick();
                broadcastWidgetUpdate();
                handler.postDelayed(this, 50);
            }
        }
    };

    public class LocalBinder extends Binder {
        StopwatchService getService() {
            return StopwatchService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public void start() {
        if (!running) {
            startTime = System.currentTimeMillis();
            running = true;
            startForeground(NOTIFICATION_ID, buildNotification());
            handler.post(tickRunnable);
        }
    }

    public void pause() {
        if (running) {
            elapsedBeforePause += System.currentTimeMillis() - startTime;
            running = false;
            handler.removeCallbacks(tickRunnable);
            updateNotification();
        }
    }

    public void reset() {
        running = false;
        handler.removeCallbacks(tickRunnable);
        elapsedBeforePause = 0;
        startTime = 0;
        laps.clear();
        stopForeground(true);
    }

    public void lap() {
        laps.add(getElapsedTime());
    }

    public long getElapsedTime() {
        if (running) {
            return elapsedBeforePause + (System.currentTimeMillis() - startTime);
        }
        return elapsedBeforePause;
    }

    public boolean isRunning() {
        return running;
    }

    public List<Long> getLaps() {
        return new ArrayList<>(laps);
    }

    private void broadcastTick() {
        Intent intent = new Intent(ACTION_TICK);
        sendBroadcast(intent);
    }

    private void broadcastWidgetUpdate() {
        Intent intent = new Intent(ACTION_WIDGET_UPDATE);
        intent.setPackage(getPackageName());
        sendBroadcast(intent);
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID, "Stopwatch", NotificationManager.IMPORTANCE_LOW);
        channel.setDescription("Shows stopwatch status");
        channel.setShowBadge(false);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }

    private Notification buildNotification() {
        Intent openIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Sleek Stopwatch")
                .setContentText(StopwatchUtils.formatFull(getElapsedTime()) + " — Running")
                .setSmallIcon(R.drawable.ic_stopwatch_notif)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setSilent(true)
                .build();
    }

    private void updateNotification() {
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.notify(NOTIFICATION_ID, buildNotification());
    }
}

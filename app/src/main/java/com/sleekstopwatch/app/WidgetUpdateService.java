package com.sleekstopwatch.app;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

public class WidgetUpdateService extends Service {

    private Handler handler = new Handler(Looper.getMainLooper());
    private long startTime = 0;
    private long elapsed = 0;

    private Runnable ticker = new Runnable() {
        @Override
        public void run() {
            if (StopwatchWidget.isRunning) {
                elapsed = StopwatchWidget.hasTime
                        ? (System.currentTimeMillis() - startTime)
                        : 0;
                long total = elapsed;
                StopwatchWidget.currentTime = StopwatchUtils.formatTime(total);
                StopwatchWidget.currentMillis = StopwatchUtils.formatMillis(total);
                StopwatchWidget.updateAllWidgets(WidgetUpdateService.this);
                handler.postDelayed(this, 100);
            } else {
                stopSelf();
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startTime = System.currentTimeMillis();
        handler.post(ticker);
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(ticker);
        super.onDestroy();
    }
}

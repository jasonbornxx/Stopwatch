package com.sleekstopwatch.app;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;
import android.widget.ListView;
import android.widget.ArrayAdapter;

public class MainActivity extends AppCompatActivity {

    private StopwatchService stopwatchService;
    private boolean bound = false;

    private TextView tvTime;
    private TextView tvMillis;
    private MaterialButton btnStartStop;
    private MaterialButton btnLapReset;
    private ListView lvLaps;

    private ArrayAdapter<String> lapAdapter;
    private List<String> lapList = new ArrayList<>();

    private BroadcastReceiver tickReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (bound && stopwatchService != null) {
                updateTimeDisplay();
            }
        }
    };

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            StopwatchService.LocalBinder binder = (StopwatchService.LocalBinder) service;
            stopwatchService = binder.getService();
            bound = true;
            updateUI();
            updateLapList();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        tvTime = findViewById(R.id.tv_time);
        tvMillis = findViewById(R.id.tv_millis);
        btnStartStop = findViewById(R.id.btn_start_stop);
        btnLapReset = findViewById(R.id.btn_lap_reset);
        lvLaps = findViewById(R.id.lv_laps);

        lapAdapter = new ArrayAdapter<>(this, R.layout.lap_item, R.id.tv_lap_text, lapList);
        lvLaps.setAdapter(lapAdapter);

        btnStartStop.setOnClickListener(v -> {
            if (bound) {
                if (stopwatchService.isRunning()) {
                    stopwatchService.pause();
                } else {
                    stopwatchService.start();
                }
                updateUI();
            }
        });

        btnLapReset.setOnClickListener(v -> {
            if (bound) {
                if (stopwatchService.isRunning()) {
                    stopwatchService.lap();
                    updateLapList();
                } else {
                    stopwatchService.reset();
                    lapList.clear();
                    lapAdapter.notifyDataSetChanged();
                    updateUI();
                }
            }
        });

        Intent intent = new Intent(this, StopwatchService.class);
        startService(intent);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(StopwatchService.ACTION_TICK);
        ContextCompat.registerReceiver(this, tickReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(tickReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bound) {
            unbindService(connection);
            bound = false;
        }
    }

    private void updateTimeDisplay() {
        if (!bound || stopwatchService == null) return;
        long elapsed = stopwatchService.getElapsedTime();
        tvTime.setText(StopwatchUtils.formatTime(elapsed));
        tvMillis.setText(StopwatchUtils.formatMillis(elapsed));
    }

    private void updateUI() {
        if (!bound || stopwatchService == null) return;
        updateTimeDisplay();
        boolean running = stopwatchService.isRunning();
        boolean hasTime = stopwatchService.getElapsedTime() > 0;

        if (running) {
            btnStartStop.setText("PAUSE");
            btnStartStop.setBackgroundColor(getColor(R.color.pause_color));
            btnLapReset.setText("LAP");
            btnLapReset.setEnabled(true);
        } else if (hasTime) {
            btnStartStop.setText("RESUME");
            btnStartStop.setBackgroundColor(getColor(R.color.start_color));
            btnLapReset.setText("RESET");
            btnLapReset.setEnabled(true);
        } else {
            btnStartStop.setText("START");
            btnStartStop.setBackgroundColor(getColor(R.color.start_color));
            btnLapReset.setText("LAP");
            btnLapReset.setEnabled(false);
        }
    }

    private void updateLapList() {
        if (!bound) return;
        List<Long> laps = stopwatchService.getLaps();
        lapList.clear();
        for (int i = laps.size() - 1; i >= 0; i--) {
            long lapTime = laps.get(i);
            long prevLap = i > 0 ? laps.get(i - 1) : 0;
            long split = lapTime - prevLap;
            lapList.add(String.format("Lap %d    %s  (+%s)",
                    i + 1,
                    StopwatchUtils.formatFull(lapTime),
                    StopwatchUtils.formatFull(split)));
        }
        lapAdapter.notifyDataSetChanged();
    }
}

package com.appletv.launcher;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.FrameLayout;
import android.content.Context;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LinearLayout dockContainer;
    private TextView timeView;
    private TextView dateView;
    private TextView greetingView;
    private int selectedDockPosition = 0;
    private Handler handler = new Handler(Looper.getMainLooper());
    private View selectedView = null;

    // Streaming apps configuration: [package, display_name]
    private static final String[][] STREAMING_APPS = {
        {"com.netflix.mediaclient", "Netflix"},
        {"com.amazon.venezia", "Prime Video"},
        {"com.google.android.youtube.tv", "YouTube"},
        {"com.apple.atve.samsung", "Apple TV"},
        {"in.startv.hotstar", "Jio Hotstar"},
        {"com.sony.liv", "Sony LIV"},
        {"in.konosl.web", "Xstream Play"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Main container
        FrameLayout mainContainer = new FrameLayout(this);
        mainContainer.setBackgroundColor(0xFF000000);
        mainContainer.setLayoutParams(new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ));

        // Top bar with greeting, time, date
        LinearLayout topBar = createTopBar();
        FrameLayout.LayoutParams topBarParams = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(60)
        );
        topBar.setLayoutParams(topBarParams);

        // Apps grid
        LinearLayout gridContainer = createAppsGrid();
        FrameLayout.LayoutParams gridParams = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dpToPx(400),
            android.view.Gravity.CENTER
        );
        gridParams.topMargin = dpToPx(100);
        gridContainer.setLayoutParams(gridParams);

        // Dock with streaming apps
        dockContainer = createDock();
        FrameLayout.LayoutParams dockParams = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(120)
        );
        dockParams.gravity = android.view.Gravity.BOTTOM;
        dockContainer.setLayoutParams(dockParams);

        mainContainer.addView(topBar);
        mainContainer.addView(gridContainer);
        mainContainer.addView(dockContainer);

        setContentView(mainContainer);

        // Update time every second
        updateTime();
        handler.postDelayed(this::updateTime, 1000);
    }

    private LinearLayout createTopBar() {
        LinearLayout topBar = new LinearLayout(this);
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setBackgroundColor(0xFF1a1a1a);
        topBar.setPaddingRelative(dpToPx(30), dpToPx(10), dpToPx(30), dpToPx(10));

        greetingView = new TextView(this);
        greetingView.setText("Good Evening");
        greetingView.setTextColor(0xFFFFFFFF);
        greetingView.setTextSize(20);
        greetingView.setTypeface(null, android.graphics.Typeface.BOLD);
        greetingView.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        LinearLayout timeContainer = new LinearLayout(this);
        timeContainer.setOrientation(LinearLayout.VERTICAL);
        timeContainer.setGravity(android.view.Gravity.END);

        timeView = new TextView(this);
        timeView.setTextColor(0xFFFFFFFF);
        timeView.setTextSize(18);
        timeView.setTypeface(null, android.graphics.Typeface.BOLD);

        dateView = new TextView(this);
        dateView.setTextColor(0xFF999999);
        dateView.setTextSize(12);

        timeContainer.addView(timeView);
        timeContainer.addView(dateView);

        topBar.addView(greetingView);
        topBar.addView(timeContainer);

        return topBar;
    }

    private LinearLayout createAppsGrid() {
        LinearLayout grid = new LinearLayout(this);
        grid.setOrientation(LinearLayout.VERTICAL);
        grid.setGravity(android.view.Gravity.CENTER);

        // Row 1
        LinearLayout row1 = new LinearLayout(this);
        row1.setOrientation(LinearLayout.HORIZONTAL);
        row1.setGravity(android.view.Gravity.CENTER);
        row1.setPaddingRelative(dpToPx(20), dpToPx(10), dpToPx(20), dpToPx(10));

        // Add placeholder apps
        addGridApp(row1, "Settings", android.R.drawable.ic_menu_preferences);
        addGridApp(row1, "Apps", android.R.drawable.ic_menu_view);

        grid.addView(row1);
        return grid;
    }

    private void addGridApp(LinearLayout container, String name, int icon) {
        LinearLayout appCard = new LinearLayout(this);
        appCard.setOrientation(LinearLayout.VERTICAL);
        appCard.setGravity(android.view.Gravity.CENTER);
        appCard.setBackgroundColor(0xFF2a2a2a);
        appCard.setPadding(dpToPx(15), dpToPx(15), dpToPx(15), dpToPx(15));

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
            dpToPx(100), dpToPx(100)
        );
        cardParams.setMargins(dpToPx(10), dpToPx(10), dpToPx(10), dpToPx(10));
        appCard.setLayoutParams(cardParams);

        ImageView iconView = new ImageView(this);
        iconView.setImageResource(icon);
        iconView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        iconView.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(40), dpToPx(40)));

        TextView nameView = new TextView(this);
        nameView.setText(name);
        nameView.setTextColor(0xFFFFFFFF);
        nameView.setTextSize(12);
        nameView.setGravity(android.view.Gravity.CENTER);
        nameView.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(30)
        ));

        appCard.addView(iconView);
        appCard.addView(nameView);
        container.addView(appCard);
    }

    private LinearLayout createDock() {
        LinearLayout dock = new LinearLayout(this);
        dock.setOrientation(LinearLayout.HORIZONTAL);
        dock.setBackgroundColor(0xFF0a0a0a);
        dock.setGravity(android.view.Gravity.CENTER);
        dock.setPadding(dpToPx(20), dpToPx(15), dpToPx(20), dpToPx(15));

        PackageManager pm = getPackageManager();

        for (int i = 0; i < STREAMING_APPS.length; i++) {
            String packageName = STREAMING_APPS[i][0];
            String appName = STREAMING_APPS[i][1];

            LinearLayout appCard = createDockAppCard(
                packageName, appName, i, pm
            );
            dock.addView(appCard);
        }

        return dock;
    }

    private LinearLayout createDockAppCard(String packageName, String displayName,
                                          int position, PackageManager pm) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(android.view.Gravity.CENTER);
        card.setBackgroundColor(0xFF1a1a1a);
        card.setPadding(dpToPx(12), dpToPx(10), dpToPx(12), dpToPx(10));

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
            dpToPx(100), dpToPx(100)
        );
        cardParams.setMargins(dpToPx(8), dpToPx(0), dpToPx(8), dpToPx(0));
        card.setLayoutParams(cardParams);
        card.setTag(position);

        ImageView iconView = new ImageView(this);
        iconView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        iconView.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(50), dpToPx(50)));

        try {
            ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);
            iconView.setImageDrawable(pm.getApplicationIcon(appInfo));
        } catch (PackageManager.NameNotFoundException e) {
            iconView.setImageResource(android.R.drawable.ic_menu_view);
        }

        TextView nameView = new TextView(this);
        nameView.setText(displayName);
        nameView.setTextColor(0xFFFFFFFF);
        nameView.setTextSize(10);
        nameView.setGravity(android.view.Gravity.CENTER);
        nameView.setMaxLines(1);
        nameView.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(25)
        ));

        card.addView(iconView);
        card.addView(nameView);

        card.setOnClickListener(v -> launchApp(packageName));

        return card;
    }

    private void launchApp(String packageName) {
        try {
            Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTime() {
        Calendar now = Calendar.getInstance();

        // Time format HH:MM
        String timeFormat = (String) DateFormat.format("HH:mm", now);
        timeView.setText(timeFormat);

        // Date format
        String dateFormat = (String) DateFormat.format("EEE, MMM d", now);
        dateView.setText(dateFormat);

        // Greeting based on hour
        int hour = now.get(Calendar.HOUR_OF_DAY);
        String greeting;
        if (hour < 12) {
            greeting = "Good Morning";
        } else if (hour < 18) {
            greeting = "Good Afternoon";
        } else {
            greeting = "Good Evening";
        }
        greetingView.setText(greeting);

        handler.postDelayed(this::updateTime, 1000);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Handle remote control navigation
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (selectedDockPosition > 0) {
                selectedDockPosition--;
                updateDockFocus();
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (selectedDockPosition < STREAMING_APPS.length - 1) {
                selectedDockPosition++;
                updateDockFocus();
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER ||
                   keyCode == KeyEvent.KEYCODE_ENTER) {
            // Launch selected app
            launchApp(STREAMING_APPS[selectedDockPosition][0]);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void updateDockFocus() {
        // Remove previous focus styling
        if (selectedView != null) {
            selectedView.setBackgroundColor(0xFF1a1a1a);
        }

        // Apply new focus styling
        selectedView = dockContainer.getChildAt(selectedDockPosition);
        if (selectedView != null) {
            selectedView.setBackgroundColor(0xFF333333);
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    @Override
    public void onBackPressed() {
        // Do nothing - launcher can't go back
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}

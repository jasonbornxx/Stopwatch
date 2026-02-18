# ⏱ Sleek Stopwatch App

A beautiful, dark-themed Android stopwatch with a fully-functional **home screen widget**.

## Features
- 🎨 Sleek dark UI with cyan neon accents
- ⏯ Start / Pause / Resume
- 🔄 Reset
- 🏁 Lap tracking with split times
- 📱 **Home screen widget** with Start/Pause, Lap/Reset buttons — fully usable from your home screen
- 🔔 Background notification while running
- ⏰ Centisecond precision (.00)

---

## How to Get the APK (No PC needed!)

### Step 1 — Create a GitHub account
Go to **github.com** and sign up (free).

### Step 2 — Create a new repository
- Tap the **+** button → **New repository**
- Name it: `StopwatchApp`
- Set it to **Public**
- Do NOT add README or any files
- Tap **Create repository**

### Step 3 — Upload all the files
You need to upload these files keeping the **exact same folder structure**:

```
.github/workflows/build.yml
app/build.gradle
app/proguard-rules.pro
app/src/main/AndroidManifest.xml
app/src/main/java/com/sleekstopwatch/app/MainActivity.java
app/src/main/java/com/sleekstopwatch/app/StopwatchService.java
app/src/main/java/com/sleekstopwatch/app/StopwatchWidget.java
app/src/main/java/com/sleekstopwatch/app/WidgetUpdateService.java
app/src/main/java/com/sleekstopwatch/app/StopwatchUtils.java
app/src/main/res/layout/activity_main.xml
app/src/main/res/layout/widget_stopwatch.xml
app/src/main/res/layout/lap_item.xml
app/src/main/res/drawable/circle_bg.xml
app/src/main/res/drawable/widget_bg.xml
app/src/main/res/drawable/widget_btn_start.xml
app/src/main/res/drawable/widget_btn_pause.xml
app/src/main/res/drawable/widget_btn_secondary.xml
app/src/main/res/drawable/ic_stopwatch_notif.xml
app/src/main/res/mipmap-mdpi/ic_launcher.png
app/src/main/res/mipmap-mdpi/ic_launcher_round.png
app/src/main/res/mipmap-hdpi/ic_launcher.png
app/src/main/res/mipmap-hdpi/ic_launcher_round.png
app/src/main/res/mipmap-xhdpi/ic_launcher.png
app/src/main/res/mipmap-xhdpi/ic_launcher_round.png
app/src/main/res/mipmap-xxhdpi/ic_launcher.png
app/src/main/res/mipmap-xxhdpi/ic_launcher_round.png
app/src/main/res/mipmap-xxxhdpi/ic_launcher.png
app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png
app/src/main/res/values/strings.xml
app/src/main/res/values/colors.xml
app/src/main/res/values/themes.xml
app/src/main/res/xml/stopwatch_widget_info.xml
build.gradle
settings.gradle
gradlew
gradle/wrapper/gradle-wrapper.properties
logo.svg
```

**Tip:** On GitHub's website you can upload files by going into a folder and tapping "Add file" → "Upload files". Create folders by typing the path in the filename box (e.g., `app/build.gradle`).

### Step 4 — Wait for the build
- Go to the **Actions** tab in your repository
- You'll see "Build Android APK" running
- Wait ~5 minutes for it to finish ✅

### Step 5 — Download your APK
- Click the finished workflow run
- Scroll down to **Artifacts**
- Download **StopwatchApp-debug**
- Extract the zip → install `app-debug.apk` on your phone!

> ⚠️ You may need to enable "Install from unknown sources" in Android settings.

---

## Adding the Widget
After installing the app:
1. Long press your home screen
2. Tap **Widgets**
3. Find **Sleek Stopwatch** → drag it to your home screen
4. Use ▶ to start, ⊙ to lap, ↺ to reset — all from your home screen!

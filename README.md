# Xtream VOD

Lightweight **Android TV** client for **Xtream Codes** movie and series libraries on **Fire TV** (and other Android TV devices). Browse categories on demand and play titles in **VLC** — there is no built-in player.

## Requirements

- Android TV or Fire TV device (tested target: Fire TV Stick 4K Max, Fire OS 7/8)
- **[VLC for Android](https://www.videolan.org/vlc/)** (`org.videolan.vlc`) installed on the device
- Your own Xtream provider **server URL**, **username**, and **password** (nothing is bundled with this app)

## Features

- Sign in with Xtream `player_api.php` credentials (stored in app-private DataStore)
- **Movies** and **series** only (no live TV or catch-up)
- Category lists load when you open Movies or Series; **titles load only when you open a category**
- Play sends an `ACTION_VIEW` intent to VLC with the standard Xtream stream URL

## Download APK

The latest **signed release** build is committed at [`releases/xtream-vod-release.apk`](releases/xtream-vod-release.apk). Every build uses the same certificate ([`signing/xtream-vod-release.keystore`](signing/xtream-vod-release.keystore)), so you can update on the stick with `adb install -r` without uninstalling.

GitHub Actions also runs **Build signed APK** on push/PR (artifact backup).

## Build locally

```bash
./gradlew :app:assembleRelease
```

APK output: `app/build/outputs/apk/release/app-release.apk` (same signing config as CI).

Set `sdk.dir` in `local.properties` if the Android SDK is not detected automatically.

## Sideload (Fire TV)

1. Enable **Developer options** and **ADB debugging** on the Fire TV.
2. Install from a machine on the same network:

```bash
adb connect <fire-tv-ip>:5555
adb install -r app-release.apk
```

3. Open **Xtream VOD** from the TV app row, connect your account, and pick a title to play in VLC.

## Privacy

Credentials stay on the device in DataStore until you sign out in Settings. The app does not ship with servers, playlists, or sample accounts. Do not commit real credentials to source control.

## License

MIT (see repository license file if present).

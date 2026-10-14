# Sideload signing keystore

This repository keeps a **single release keystore** so every CI and local `assembleRelease` build is signed with the same certificate. That lets you install updates on your Fire TV stick with `adb install -r` without uninstalling first.

This key is for **personal sideloading only**, not for Play Store or Amazon Appstore distribution. Anyone with repo access can produce APKs signed with this key.

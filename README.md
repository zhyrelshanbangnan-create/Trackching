# Trackching

A personal finance tracker. It tracks and gives reports on your spending, income, accounts, 
credit and IOUs by the day and by event. This will be available as a mobile application for Android.
A web app version is planned to be developed in the future for iPhones.

This project is a personal side project of mine to practice mobile app development. 

## Project layout

| Folder | What lives there |
| --- | --- |
| `shared/` | Everything both apps share: screens (Compose Multiplatform), data and money logic. |
| `androidApp/` | The small Android shell: starts the app and shows the shared `AppShell()` screen. |
| `.github/workflows/` | CI: builds the app and runs all tests on every push and pull request. |

Built with Kotlin Multiplatform, Compose Multiplatform and (soon) SQLDelight.

## Try the app on your phone

You don't need Android Studio for this.

1. Open the **Actions** tab on GitHub and pick the latest green run on `main`.
2. Under **Artifacts**, download `trackching-debug-apk` and unzip it.
3. Copy the `.apk` to your phone and open it. Android will ask you to allow installs from that app once.

## Build it yourself

Needs JDK 21 and the Android SDK (Android Studio installs both).

```sh
./gradlew build                      # build everything and run all tests
./gradlew :androidApp:installDebug   # install on a connected phone
```

## How work is done

Work happens on a branch and comes in by pull request. CI must be green before a merge.

plugins {
    id("com.android.application") version "8.7.0"
    id("org.jetbrains.kotlin.android") version "2.0.21"
}
android {
    namespace = "dev.tzelon.hebrewdate"
    compileSdk = 35
    defaultConfig { applicationId = "dev.tzelon.hebrewdate"; minSdk = 26; targetSdk = 35; versionCode = 6; versionName = "0.7" }
    // Checked-in debug key: without it every CI runner signs with its own
    // throwaway keystore and the next APK won't install over the last one.
    signingConfigs {
        getByName("debug") {
            storeFile = file("keystore/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }
    kotlinOptions { jvmTarget = "17" }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
}
dependencies {
    implementation("com.kosherjava:zmanim:2.5.0")
    testImplementation("junit:junit:4.13.2")
}

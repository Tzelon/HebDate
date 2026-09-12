plugins {
    id("com.android.application") version "8.7.0"
    id("org.jetbrains.kotlin.android") version "2.0.21"
}
android {
    namespace = "dev.tzelon.hebrewdate"
    compileSdk = 35
    defaultConfig { applicationId = "dev.tzelon.hebrewdate"; minSdk = 26; targetSdk = 35; versionCode = 1; versionName = "0.2" }
    kotlinOptions { jvmTarget = "17" }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
}
dependencies {
    implementation("com.kosherjava:zmanim:2.5.0")
}

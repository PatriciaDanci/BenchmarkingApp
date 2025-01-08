plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.benchmarkapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.benchmarkapp"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.room)
    implementation(libs.gson)
    implementation ("com.jakewharton.timber:timber:5.0.1")
    implementation(libs.mpandroidchart)
    implementation(libs.timber)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation(libs.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
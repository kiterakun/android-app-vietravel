plugins {
    alias(libs.plugins.android.application)

    id("com.google.gms.google-services")
}

android {
    namespace = "com.group6.vietravel"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.group6.vietravel"
        minSdk = 24
        targetSdk = 36
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
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.legacy.support.v4)
    implementation(libs.annotation)
    implementation(libs.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(platform("com.google.firebase:firebase-bom:34.6.0"))

    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")

    // Thư viện Google Maps
    implementation("com.google.android.gms:play-services-maps:19.2.0")
    // Thu vien lay vi tri nguoi dung
    implementation("com.google.android.gms:play-services-location:21.3.0")
    // Thu vien HTTP de goi api tim duong di
    implementation("com.squareup.okhttp3:okhttp:5.3.2")
    // Thư viện Places
    implementation("com.google.android.libraries.places:places:5.1.1")
    // Thư viện dung de load anh
    implementation("com.github.bumptech.glide:glide:5.0.5")
    // Thu vien su ly json
    implementation("com.google.code.gson:gson:2.13.2")
    // Thu vien Google AI SDK for Android (Gemini)
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")
}

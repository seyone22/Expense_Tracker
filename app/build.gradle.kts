plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")

    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.21"
}

android {
    namespace = "com.example.expensetracker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.expensetracker"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "v0.3.2-alpha"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // AndroidX Core
    implementation(libs.androidx.core.ktx)

    // AndroidX Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // AndroidX Activity Compose
    implementation(libs.androidx.activity.compose)

    // AndroidX Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)

    // AndroidX Compose Material3
    implementation(libs.androidx.material3)

    // JUnit
    testImplementation(libs.junit)

    // AndroidX Test
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // AndroidX Compose Debug
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // AndroidX Compose Material Icons Extended
    implementation(libs.androidx.material.icons.extended)

    // AndroidX Navigation Compose
    implementation(libs.androidx.navigation.compose)

    // AndroidX Compose Material3 Adaptive Navigation Suite
    implementation(libs.androidx.material3.adaptive.navigation.suite)

    // AndroidX Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // AndroidX Preferences DataStore
    implementation(libs.androidx.datastore.preferences)

    // AndroidX Lifecycle ViewModel Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Retrofit
    implementation(libs.retrofit)

    // Retrofit with Kotlin Serialization Converter
    implementation(libs.retrofit.kotlinx.serialization.converter)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    // Kotlinx Serialization JSON
    implementation(libs.kotlinx.serialization.json)

    // AndroidX Window
    implementation(libs.androidx.window)

    // For `compose`. Creates a `ChartStyle` based on an M3 Material Theme.
    implementation(libs.vico.compose.m3)
    implementation(libs.vico.compose.m3.alpha)

    // Workers
    implementation(libs.androidx.work.runtime.ktx)

    // Biometrics
    implementation(libs.androidx.biometric.ktx)

    // Permission handling with accompanist
    implementation(libs.accompanist.permissions)

    // Coil image library
    implementation(libs.coil.compose)

    // Tehras charts
    implementation(libs.charts)

    // AndroidX Compose Material3 Window Size Class Android
    implementation("androidx.compose.material3:material3-window-size-class-android:1.2.1")
}
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("jacoco")
    id("org.jetbrains.kotlin.plugin.serialization") version "2.4.0"
    alias(libs.plugins.kotlinCompose)
}

android {
    namespace = "com.seyone22.expensetracker"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.seyone22.expenses"
        minSdk = 28
        targetSdk = 37
        versionCode = 16
        versionName = "0.5.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        versionNameSuffix = "-alpha"
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
    }
    kotlin {
        jvmToolchain(21) // This forces both Java and Kotlin to use JDK 17
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
    implementation(libs.androidx.lifecycle.process)

    // JUnit
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)

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
    implementation(libs.androidx.compose.adaptive)
    implementation(libs.androidx.compose.adaptive.layout)
    implementation(libs.androidx.compose.adaptive.navigation)

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

    // Vico Charts - M3 Compose
    implementation(libs.vico.compose)
    implementation(libs.vico.compose.m3)

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
    implementation(libs.androidx.compose.material3.window.size.class1.android)

    implementation(libs.androidx.activity.ktx) // Latest version
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    classDirectories.setFrom(
        fileTree(layout.buildDirectory.dir("tmp/kotlin-classes/debug")) {
            exclude(
                "**/R.class",
                "**/R\$*.class",
                "**/BuildConfig.*",
                "**/Manifest*.*",
                "**/*_MembersInjector.class",
                "**/Dagger*.*",
                "**/*_Factory*.*",
                "**/*_Provide*Factory*.*",
                "**/*\$Holder.class",
                "**/*_MembersInjector\$*.*"
            )
        }
    )
    sourceDirectories.setFrom(files("src/main/java"))
    executionData.setFrom(fileTree(layout.buildDirectory) {
        include("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
    })
}
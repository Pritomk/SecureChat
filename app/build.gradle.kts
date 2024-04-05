plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.securechat"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.securechat"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("String", "AUTH_API", "\"https://secure-chat-ccz5.onrender.com\"")
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("String", "AUTH_API", "\"https://secure-chat-ccz5.onrender.com\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.stream.chat.android.ui.components)
    implementation(libs.stream.chat.android.offline)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.material.v190)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.coil)

    //Retrofit dependency
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)

    implementation(libs.androidx.core.splashscreen)

    //Chucker
    debugImplementation(libs.library)
    releaseImplementation(libs.library.no.op)
    //Hunter
    implementation(libs.hunter.okhttp.library)

    implementation(libs.zxing.android.embedded)

    implementation(libs.material.v130alpha03)

    implementation(libs.circleimageview)

    implementation(libs.glide)
}
plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
}

android {
    namespace = "com.dicoding.mystory"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.dicoding.mystory"
        minSdk = 28
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
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

    dependencies {
        // Dependensi untuk UI dan antarmuka pengguna
        implementation("com.android.car.ui:car-ui-lib:2.6.0") // Perpustakaan UI khusus untuk Android Automotive
        implementation("androidx.appcompat:appcompat:1.6.1") // Perpustakaan compat untuk AppCompat
        implementation("com.google.android.material:material:1.11.0") // Perpustakaan Material Design

        // Dependensi untuk penyimpanan data
        implementation("androidx.datastore:datastore-preferences:1.0.0") // Datastore untuk menyimpan preferensi

        // Dependensi untuk pemrograman reaktif
        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1") // ViewModel
        implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1") // LiveData
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2") // Coroutines Core
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2") // Coroutines untuk Android

        // Dependensi untuk penyimpanan data lokal
        implementation("androidx.room:room-ktx:2.4.2") // Room Database

        // Dependensi untuk UI dan tata letak
        implementation("androidx.constraintlayout:constraintlayout:2.1.4") // ConstraintLayout
        implementation("androidx.viewpager2:viewpager2:1.0.0") // ViewPager2

        // Dependensi untuk memuat gambar
        implementation("com.github.bumptech.glide:glide:4.16.0") // Glide untuk memuat gambar

        // Dependensi untuk jaringan
        implementation("com.squareup.retrofit2:retrofit:2.9.0") // Retrofit untuk HTTP Client
        implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Converter Gson untuk Retrofit
        implementation("com.squareup.okhttp3:logging-interceptor:4.11.0") // Logging Interceptor untuk OkHttp
        implementation("com.loopj.android:android-async-http:1.4.11") // AsyncHttpClient untuk HTTP Client

        // Dependensi untuk pengujian
        testImplementation("junit:junit:4.13.2") // JUnit untuk pengujian unit
        androidTestImplementation("androidx.test.ext:junit:1.1.5") // JUnit untuk pengujian instrumen
        androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1") // Espresso untuk pengujian UI

        // Dependensi tambahan
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4") // Coroutines untuk Android (versi terbaru)
        implementation("androidx.activity:activity-ktx:1.7.2") // Perpustakaan Activity Kotlin Extensions
    }
}
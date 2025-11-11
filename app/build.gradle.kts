plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.tripgenie"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.tripgenie"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // 🔐 Gemini API key from gradle.properties
        buildConfigField(
            "String",
            "GEMINI_API_KEY",
            "\"${project.findProperty("GEMINI_API_KEY") ?: ""}\""
        )
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}

dependencies {
    // 🧩 AndroidX Core Components
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("androidx.fragment:fragment-ktx:1.8.3")

    // 🎨 Material Design 3 (Latest stable)
    implementation("com.google.android.material:material:1.13.0-alpha04")

    // 🌐 Networking
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.14")
    implementation("org.json:json:20240303")

    // 🖼 Image Loading
    implementation("com.squareup.picasso:picasso:2.8")

    // ⚙️ Google Gemini / AI SDK
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    // 🔁 Coroutines for async tasks
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    // 📍 Google Play Services - Location
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // 🌐 Retrofit + Gson
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.google.android.material:material:1.9.0")


    // 🧪 Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

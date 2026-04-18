import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.manish.tripgenie"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.manish.tripgenie"
        minSdk = 24
        targetSdk = 35
        versionCode = 2
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Load secrets from local.properties
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        
        var geminiKey = ""
        var amadeusId = ""
        var amadeusSecret = ""
        var mapsApiKey = ""
        var ticketmasterKey = ""
        var predictHqToken = ""
        
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { localProperties.load(it) }
            geminiKey = localProperties.getProperty("GEMINI_API_KEY") ?: ""
            amadeusId = localProperties.getProperty("AMADEUS_CLIENT_ID") ?: ""
            amadeusSecret = localProperties.getProperty("AMADEUS_CLIENT_SECRET") ?: ""
            mapsApiKey = localProperties.getProperty("MAPS_API_KEY") ?: ""
            ticketmasterKey = localProperties.getProperty("TICKETMASTER_API_KEY") ?: ""
            predictHqToken = localProperties.getProperty("PREDICTHQ_TOKEN") ?: ""
        }
        
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiKey\"")
        buildConfigField("String", "AMADEUS_CLIENT_ID", "\"$amadeusId\"")
        buildConfigField("String", "AMADEUS_CLIENT_SECRET", "\"$amadeusSecret\"")
        buildConfigField("String", "MAPS_API_KEY", "\"$mapsApiKey\"")
        buildConfigField("String", "TICKETMASTER_API_KEY", "\"$ticketmasterKey\"")
        buildConfigField("String", "PREDICTHQ_TOKEN", "\"$predictHqToken\"")

        resValue("string", "google_maps_key", mapsApiKey)
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
        compose = true
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
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("androidx.fragment:fragment-ktx:1.8.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.6")

    // Google Maps & Places
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.android.libraries.places:places:4.1.0")
    implementation("com.google.maps.android:android-maps-utils:3.8.2")

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.coil.compose)

    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation(libs.material)
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.14")
    implementation("org.json:json:20240303")
    implementation("com.squareup.picasso:picasso:2.8")
    implementation(libs.generativeai)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    
    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.analytics)

    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

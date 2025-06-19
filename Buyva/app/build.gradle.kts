import org.gradle.declarative.dsl.schema.FqName.Empty.packageName

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "2.1.10"
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    id("com.apollographql.apollo3").version("3.8.6")

}

android {
    namespace = "com.example.buyva"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.buyva"
        minSdk = 24
        targetSdk = 35
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.foundation.layout.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    //Navigation host
    implementation("androidx.navigation:navigation-compose:2.8.8")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")

    //Curved bottom navigation bar
    implementation ("np.com.susanthapa:curved_bottom_navigation:0.7.0")
    implementation("androidx.compose.material:material-icons-extended")
    // Firebase auth
    implementation(platform("com.google.firebase:firebase-bom:33.15.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth-ktx:22.3.1")
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    //google maps
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.maps.android:places-compose:0.1.3")
    implementation("com.google.android.gms:play-services-maps:19.1.0")
    implementation("com.google.maps.android:maps-compose:2.11.2")
    implementation("com.google.accompanist:accompanist-permissions:0.33.2-alpha")
    implementation("com.google.android.libraries.places:places:3.4.0")
    //ui
    implementation ("androidx.compose.material:material:1.6.1")
    implementation("com.apollographql.apollo3:apollo-runtime:3.8.6")

    implementation("com.airbnb.android:lottie-compose:6.3.0")

    //payment
    implementation("com.stripe:stripe-android:21.17.0")
    implementation("com.stripe:paymentsheet:21.17.0")

}
apollo {
    service("service") {
        packageName.set("com.example.buyva")
        }
}
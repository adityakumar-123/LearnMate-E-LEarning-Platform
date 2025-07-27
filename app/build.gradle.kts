plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.learnify"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.learnify"
        minSdk = 24
        targetSdk = 34
        versionCode = 3
        versionName = "1.8"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Firebase BoM to align all Firebase versions
    implementation(platform("com.google.firebase:firebase-bom:33.15.0"))

    // Firebase services (no versions needed)
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-functions")
    implementation("com.google.firebase:firebase-firestore")

    // AndroidX & Material
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("androidx.recyclerview:recyclerview:1.3.1")

    // UI Libraries
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.squareup.picasso:picasso:2.8")
    implementation("com.airbnb.android:lottie:6.4.0")
    implementation("com.mikhaellopez:circularprogressbar:3.1.0")

    // Kotlin Standard Library
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.0")
    implementation(libs.lifecycle.common.jvm)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    implementation("androidx.lifecycle:lifecycle-runtime:2.6.2")
    implementation("com.razorpay:checkout:1.6.33")

    implementation ("com.squareup.okhttp3:okhttp:4.12.0")
    implementation ("org.json:json:20240303")

// Code editor with highlighting

}

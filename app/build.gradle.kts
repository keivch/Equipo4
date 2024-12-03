plugins {
    id("com.android.application")
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.project1"
    compileSdk = 35



    defaultConfig {
        applicationId = "com.example.project1"
        minSdk = 24
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
        //sourceCompatibility = JavaVersion.VERSION_1_8
        //targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility =JavaVersion.VERSION_17
                targetCompatibility =JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "1.8"

        jvmTarget = "17"

    }
    buildFeatures{
        viewBinding = true
    }
}
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

dependencies {
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation (libs.android.lottie)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.auth.ktx)
    annotationProcessor(libs.compiler)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.activity:activity-ktx:1.8.0")
    implementation("androidx.fragment:fragment-ktx:1.7.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.logging.interceptor)
    implementation (libs.google.firebase.firestore)
    implementation("com.google.firebase:firebase-bom:33.6.0")
    implementation("com.google.firebase:firebase-analytics")


    //implementation(libs.firebase.firestore)

   // implementation (libs.material.v190)


    implementation(platform(libs.firebase.bom))
    //implementation(libs.birebase.auth)
    implementation(libs.firebase.analytics)

}
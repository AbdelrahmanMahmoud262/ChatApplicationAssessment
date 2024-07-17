plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)

    alias(libs.plugins.google.dagger.hilt.android)
    alias(libs.plugins.kotlin.kapt)

    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.androdevelopment.data"
    compileSdk = 34

    defaultConfig {
        minSdk = 30

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

kapt{
    correctErrorTypes = true
}

dependencies {

    implementation(project(":domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Hilt
    implementation (libs.hilt.android)
    kapt (libs.hilt.compiler)

    // Serialization
    implementation(libs.kotlin.serialization.json)
}
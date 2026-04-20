plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.arfurnitureshop"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.arfurnitureshop"
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Trong build.gradle.kts, sửa lại chính xác như sau:
    implementation("com.google.ar:core:1.31.0")
    implementation("com.gorisse.thomas.sceneform:sceneform:1.21.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    // Thêm thư viện Sceneform nâng cấp (Hỗ trợ quét khuôn mặt cực tốt)
    implementation("com.gorisse.thomas.sceneform:sceneform:1.21.0")
    implementation("com.google.mlkit:face-detection:16.1.6")

}
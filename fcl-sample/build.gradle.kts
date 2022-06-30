plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
}

setupAppModule {
    defaultConfig {
        applicationId = "com.portto.fcl.sample"
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    implementation(projects.fcl)
    implementation(libs.androidx.core)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.koin)
    implementation(libs.material)
    implementation(libs.timber)

    // Lifecycle
    implementation(libs.bundles.lifecycle)
}
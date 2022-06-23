plugins {
    id("com.android.application")
    kotlin("android")
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
    }
}

dependencies {
    implementation(projects.fclBase)

    implementation(libs.androidx.core)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.material)
}
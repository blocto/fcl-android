plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("plugin.serialization")
}

setupLibraryModule{
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.androidx.recyclerview)
    implementation(libs.bundles.network)
    implementation(libs.coil)
    implementation(libs.coroutines.android)
    implementation(libs.flow.sdk)
    implementation(libs.material)
    testImplementation(libs.junit)
}
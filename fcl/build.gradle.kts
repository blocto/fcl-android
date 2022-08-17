plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("plugin.serialization")
}

setupLibraryModule {
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    api(files("libs/core-debug.aar"))
    api(files("libs/wallet-debug.aar"))
    api(files("libs/flow-debug.aar"))
    api(libs.flow.sdk)
    implementation(libs.androidx.core)
    implementation(libs.androidx.recyclerview)
    implementation(libs.bundles.network)
    implementation(libs.coil)
    implementation(libs.coroutines.android)
    implementation(libs.grpc.okhttp)
    implementation(libs.material)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.junit)
}
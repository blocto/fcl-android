plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("plugin.serialization")
}

setupLibraryModule(publish = true) {
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    api(libs.flow.sdk)
    implementation(libs.androidx.core)
    implementation(libs.androidx.recyclerview)
    implementation(libs.blocto.sdk.flow)
    implementation(libs.bundles.network)
    implementation(libs.coil)
    implementation(libs.coroutines.android)
    implementation(libs.grpc.okhttp)
    implementation(libs.material)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.junit)
}
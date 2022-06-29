plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("plugin.serialization")
}

setupLibraryModule()

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.bundles.network)
    implementation(libs.coroutines.android)
    implementation(libs.flow.sdk)
    implementation(libs.material)
    testImplementation(libs.junit)
}
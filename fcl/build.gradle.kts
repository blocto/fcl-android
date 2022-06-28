plugins {
    id("com.android.library")
    kotlin("android")
}

setupLibraryModule()

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.coroutines.android)
    implementation(libs.flow.sdk)
    implementation(libs.material)
    testImplementation(libs.junit)
}
plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.firebase.appdistribution")
    kotlin("android")
    kotlin("kapt")
}

setupAppModule {
    defaultConfig {
        applicationId = "com.portto.fcl.sample"
    }

    signingConfigs {
        create("release") {
            keyAlias = getSigningProperties()["KEY_ALIAS"]
            keyPassword = getSigningProperties()["KEY_PASSWORD"]
            storeFile = rootProject.file("secrets/keystore.jks")
            storePassword = getSigningProperties()["STORE_PASSWORD"]
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            configure<com.google.firebase.appdistribution.gradle.AppDistributionExtension> {
                serviceCredentialsFile = "secrets/app-distribution.json"
                artifactType = "APK"
                groups = "portto"
            }
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

    implementation(platform("com.google.firebase:firebase-bom:30.3.1"))
}

fun getSigningProperties(): Map<String, String> {
    val items = HashMap<String, String>()

    val fl = rootProject.file("secrets/signing.properties")

    fl.exists().let {
        fl.forEachLine {
            val (key, value) = it.split("=")
            items[key] = value
        }
    }
    return items
}
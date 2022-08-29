plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
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
            storeFile = file("${project.rootDir}/keystore.jks")
            storePassword = getSigningProperties()["STORE_PASSWORD"]
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
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

    implementation(platform("com.google.firebase:firebase-bom:30.3.1"))
}

fun getSigningProperties(): Map<String, String> {
    val items = HashMap<String, String>()

    val fl = rootProject.file("signing.properties")

    fl.exists().let {
        fl.forEachLine {
            val (key, value) = it.split("=")
            items[key] = value
        }
    }
    return items
}
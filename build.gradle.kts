// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // IntelliJ bug for not being able to use libs explicitly
//    id ("com.android.application") version "7.2.1" apply false
//    id ("com.android.library") version "7.2.1" apply false
//    id ("org.jetbrains.kotlin.android") version "1.7.0" apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.gradlePlugin.android)
        classpath(libs.gradlePlugin.kotlin)
        classpath(libs.gradlePlugin.mavenPublish)
    }
}

tasks {
    register("clean", Delete::class) {
        delete(rootProject.buildDir)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }

    group = project.groupId
    version = project.versionName
}
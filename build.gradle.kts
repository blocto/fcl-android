// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.gradlePlugin.android)
        classpath(libs.gradlePlugin.kotlin)
        classpath(libs.gradlePlugin.mavenPublish)
        classpath(libs.kotlin.serialization)
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
        classpath ("com.google.gms:google-services:4.3.13")
        classpath ("com.google.firebase:firebase-appdistribution-gradle:3.0.3")
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
        maven("https://jitpack.io") // Required by flow-sdk (java-rlp)
        maven("https://s01.oss.sonatype.org/content/repositories/staging")
    }

    group = project.groupId
    version = project.versionName
}
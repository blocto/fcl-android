// Top-level build file where you can add configuration options common to all sub-projects/modules.
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost.S01

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

        // App distribution
        classpath("com.google.gms:google-services:4.3.14")
        classpath("com.google.firebase:firebase-appdistribution-gradle:3.1.0")
    }
}

@Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")
plugins {
    alias(libs.plugins.ktlint)
}

tasks {
    register("clean", Delete::class) {
        delete(rootProject.buildDir)
    }
}

tasks.register<org.jmailen.gradle.kotlinter.tasks.FormatTask>("ktFormat") {
    group = "formatting"
    source(files("src"))
    report.set(file("build/format-report.txt"))
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

    apply(plugin = "org.jmailen.kotlinter")

    kotlinter {
        disabledRules = arrayOf(
            "annotation",
            "argument-list-wrapping",
            "filename",
            "indent",
            "max-line-length",
            "parameter-list-wrapping",
            "spacing-between-declarations-with-annotations",
            "wrapping",
        )
    }

    plugins.withId("com.vanniktech.maven.publish.base") {
        group = project.groupId
        version = project.versionName

        extensions.configure<MavenPublishBaseExtension> {
            publishToMavenCentral(S01)
            signAllPublications()
            pomFromGradleProperties()
        }
    }
}
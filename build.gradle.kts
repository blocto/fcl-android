// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // IntelliJ bug for not being able to use libs explicitly
    id ("com.android.application") version "7.2.1" apply false
    id ("com.android.library") version "7.2.1" apply false
    id ("org.jetbrains.kotlin.android") version "1.7.0" apply false
}

tasks {
    register("clean", Delete::class) {
        delete(rootProject.buildDir)
    }
}
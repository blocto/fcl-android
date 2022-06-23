//pluginManagement {
//    repositories {
//        gradlePluginPortal()
//        google()
//        mavenCentral()
//    }
//}
//dependencyResolutionManagement {
//    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
//    repositories {
//        gradlePluginPortal()
//        google()
//        mavenCentral()
//    }
//}
rootProject.name = "fcl"
// Public modules
include(
    "fcl-base",
//    "fcl-compose",
)
// Private modules
include(
    "fcl-sample",
//    "fcl-sample-compose",
)

enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

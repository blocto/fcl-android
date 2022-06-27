rootProject.name = "fcl"

// Public modules
include(
    "fcl",
//    "fcl-compose",
)
// Private modules
include(
    "fcl-sample",
//    "fcl-sample-compose",
)

enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

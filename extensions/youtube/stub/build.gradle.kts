plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "app.silva.extension"
    buildToolsVersion = "34.0.0"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
    }
}

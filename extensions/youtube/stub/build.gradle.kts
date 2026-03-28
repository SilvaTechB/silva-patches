plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "app.silva.extension"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
    }
}

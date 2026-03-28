dependencies {
    compileOnly(project(":extensions:shared:library"))
    compileOnly(project(":extensions:reddit:stub"))

    // Used by SilvaSettingsIconVectorDrawable.
    implementation(libs.androidx.core)

    // Used by SpoofSignaturePatch.
    implementation(libs.hiddenapi)
}

android {
    buildToolsVersion = "34.0.0"
    compileSdk = 35

    defaultConfig {
        minSdk = 28
    }
}

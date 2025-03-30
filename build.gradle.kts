plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.google.ksp) apply false
}

buildscript {
    dependencies {
        classpath(libs.mapsplatform.secrets)
    }
}
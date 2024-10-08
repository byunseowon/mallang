// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript{
    dependencies{
        //google maps secret gradle
        //용도 : google api key 숨기기
        classpath(libs.google.maps.secrets.gradle)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlinx.serialization) apply false
}
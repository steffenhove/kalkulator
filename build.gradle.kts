// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val kotlinVersion by extra("2.1.0") // Updated Kotlin version
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
       // classpath("com.android.tools.build:gradle:8.2.2")
        //classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.0")
        classpath ("org.jetbrains.kotlin:kotlin-serialization:2.1.0")
        classpath ("com.android.tools:r8:8.7.18")
        classpath("com.android.tools.build:gradle:8.1.4")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.0")
    }
}
plugins {
    id("com.android.application") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "2.1.0" apply false
    id("com.google.devtools.ksp") version "2.1.0-1.0.29" apply false
}
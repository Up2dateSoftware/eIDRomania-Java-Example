pluginManagement {
    plugins {
        id("com.google.cloud.artifactregistry.gradle-plugin") version "2.2.1"
    }
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "eidromania-desktop-sdk-example"

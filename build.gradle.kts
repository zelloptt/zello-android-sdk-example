// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
	alias(libs.plugins.androidApplication) apply false
	id("org.jetbrains.kotlin.plugin.compose") version "2.3.20" apply false
	id("com.google.dagger.hilt.android") version "2.59.2" apply false
	id("com.google.devtools.ksp") version "2.3.6" apply false
	// Uncomment to enable Firebase services. Requires google-services.json file.
	// Also uncomment the noted line in the app level build.gradle.kts file.
	// id("com.google.gms.google-services") version "4.4.4" apply false
	alias(libs.plugins.androidLibrary) apply false
}
buildscript {
	dependencies {
		classpath("com.google.dagger:hilt-android-gradle-plugin:2.59.2")
	}
}

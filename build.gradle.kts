// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
	alias(libs.plugins.androidApplication) apply false
	alias(libs.plugins.jetbrainsKotlinAndroid) apply false
	id("com.google.dagger.hilt.android") version "2.52" apply false
	id("com.google.devtools.ksp") version "2.0.10-1.0.24" apply false
	alias(libs.plugins.compose.compiler)
	// Uncomment to enable Firebase services. Requires google-services.json file.
	// Also uncomment the noted line in the app level build.gradle.kts file.
	// id("com.google.gms.google-services") version "4.4.1" apply false
	alias(libs.plugins.androidLibrary) apply false
}
buildscript {
	dependencies {
		classpath("com.google.dagger:hilt-android-gradle-plugin:2.52")
	}
}

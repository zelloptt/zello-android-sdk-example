// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
	alias(libs.plugins.androidApplication) apply false
	alias(libs.plugins.compose.compiler) apply false
	alias(libs.plugins.hilt) apply false
	alias(libs.plugins.ksp) apply false
	// Uncomment to enable Firebase services. Requires google-services.json file.
	// Also uncomment the noted line in the app level build.gradle.kts file.
	// alias(libs.plugins.google.services) apply false
}

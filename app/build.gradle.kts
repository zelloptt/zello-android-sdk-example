plugins {
	alias(libs.plugins.androidApplication)
	id("org.jetbrains.kotlin.plugin.compose") version "2.3.20"
	id("com.google.dagger.hilt.android") version "2.59.2"
	id("com.google.devtools.ksp") version "2.3.6"
	// Uncomment to enable Firebase services. Requires google-services.json file.
	// Also uncomment the noted line in the project level build.gradle.kts file.
	// id("com.google.gms.google-services") version "4.4.4"
}

hilt {
	enableAggregatingTask = true
}

android {
	namespace = "com.zello.sdk.example.app"
	compileSdk = 36

	defaultConfig {
		applicationId = "com.zello.sdk.example.app"
		minSdk = 27
		targetSdk = 36
		versionCode = 1
		versionName = "1.0"

		ndkVersion = "27.0.12077973"
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
		}
		debug {
			isMinifyEnabled = false
			proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}
	buildFeatures {
		viewBinding = true
		compose = true
	}

	packaging {
		jniLibs {
			useLegacyPackaging = true
		}
	}
}

dependencies {

	// The only dependency needed to use the Zello SDK
	implementation(libs.sdk)

	implementation(platform("com.google.firebase:firebase-bom:34.12.0"))
	implementation("com.google.firebase:firebase-messaging")

	val composeBom = platform("androidx.compose:compose-bom:2024.06.00")
	implementation(composeBom)
	androidTestImplementation(composeBom)

	implementation("androidx.compose.runtime:runtime")
	implementation("androidx.compose.runtime:runtime-livedata")

	// Material Design 3
	implementation("androidx.compose.material3:material3")

	// Android Studio Preview support
	implementation("androidx.compose.ui:ui-tooling-preview")
	debugImplementation("androidx.compose.ui:ui-tooling")

	implementation("com.google.dagger:hilt-android:2.59.2")
	ksp("com.google.dagger:hilt-android-compiler:2.59.2")
	ksp("androidx.hilt:hilt-compiler:1.3.0")
	implementation("io.coil-kt:coil-compose:2.7.0")

	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.appcompat)
	implementation(libs.material)
	implementation(libs.androidx.constraintlayout)
	implementation(libs.androidx.lifecycle.livedata.ktx)
	implementation(libs.androidx.lifecycle.viewmodel.ktx)
	implementation(libs.androidx.navigation.fragment.ktx)
	implementation(libs.androidx.navigation.ui.ktx)
	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
	implementation(kotlin("script-runtime"))
}

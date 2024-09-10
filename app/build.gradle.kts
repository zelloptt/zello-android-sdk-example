plugins {
	alias(libs.plugins.androidApplication)
	alias(libs.plugins.jetbrainsKotlinAndroid)
	id("com.google.dagger.hilt.android") version "2.51"
	id("com.google.devtools.ksp") version "1.9.22+"
	// Uncomment to enable Firebase services. Requires google-services.json file.
	// Also uncomment the noted line in the project level build.gradle.kts file.
	// id("com.google.gms.google-services") version "4.4.1"
}

hilt {
	enableAggregatingTask = true
}

android {
	namespace = "com.zello.sdk.example.app"
	compileSdk = 34

	defaultConfig {
		applicationId = "com.zello.sdk.example.app"
		minSdk = 24
		targetSdk = 34
		versionCode = 1
		versionName = "1.0"

		ndkVersion = "24.0.8215888"
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
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
	}
	kotlinOptions {
		jvmTarget = "1.8"
	}
	buildFeatures {
		viewBinding = true
		compose = true
	}
	composeOptions {
		kotlinCompilerExtensionVersion = "1.5.8"
	}

	packaging {
		jniLibs {
			useLegacyPackaging = true
		}
	}
}

dependencies {

	// Required Dependencies to use the Zello SDK
	implementation("com.zello:sdk:0.4.0")
	implementation("com.zello:zello:0.4.0")
	implementation("com.zello:core:0.4.0") {
		exclude(module = "unspecified")
	}
	implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
	implementation("com.google.firebase:firebase-messaging")

	val composeBom = platform("androidx.compose:compose-bom:2024.03.00")
	implementation(composeBom)
	androidTestImplementation(composeBom)

	implementation("androidx.compose.runtime:runtime:1.6.4")
	implementation("androidx.compose.runtime:runtime-livedata:1.6.4")
	implementation("androidx.compose.runtime:runtime-rxjava2:1.6.4")

	// Material Design 3
	implementation("androidx.compose.material3:material3")

	// Android Studio Preview support
	implementation("androidx.compose.ui:ui-tooling-preview")
	debugImplementation("androidx.compose.ui:ui-tooling")

	implementation("com.google.dagger:hilt-android:2.51")
	ksp("com.google.dagger:hilt-android-compiler:2.51")
	ksp("androidx.hilt:hilt-compiler:1.2.0")
	implementation("io.coil-kt:coil-compose:2.6.0")

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

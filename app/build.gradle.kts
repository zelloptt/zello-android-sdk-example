plugins {
	alias(libs.plugins.androidApplication)
	alias(libs.plugins.compose.compiler)
	alias(libs.plugins.hilt)
	alias(libs.plugins.ksp)
	// Uncomment to enable Firebase services. Requires google-services.json file.
	// Also uncomment the noted line in the project level build.gradle.kts file.
	// alias(libs.plugins.google.services)
}

hilt {
	enableAggregatingTask = true
}

android {
	namespace = "com.zello.sdk.example.app"
	compileSdk {
		version = release(37) {
			minorApiLevel = 2
		}
	}

	defaultConfig {
		applicationId = "com.zello.sdk.example.app"
		minSdk = 27
		targetSdk = 37
		versionCode = 1
		versionName = "1.0"

		ndkVersion = "29.0.14206865"
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

	implementation(platform(libs.androidx.compose.bom))

	implementation(libs.androidx.compose.runtime)
	implementation(libs.androidx.compose.runtime.livedata)

	// Material Design 3
	implementation(libs.androidx.compose.material3)
	implementation(libs.androidx.compose.material.icons.core)

	// Android Studio Preview support
	implementation(libs.androidx.compose.ui.tooling.preview)
	debugImplementation(libs.androidx.compose.ui.tooling)

	implementation(libs.hilt.android)
	ksp(libs.hilt.android.compiler)
	implementation(libs.coil.compose)

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
}

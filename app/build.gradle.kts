plugins {
	id("com.android.application")
	id("com.google.gms.google-services")
}

android {
	namespace = "com.sip.busify"
	compileSdk = 34

	defaultConfig {
		applicationId = "com.sip.busify"
		minSdk = 26
		targetSdk = 30
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
		}
	}

	buildFeatures {
		viewBinding = true
	}

	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
	}
}

dependencies {
	implementation("androidx.appcompat:appcompat:1.6.1")
	implementation("com.google.android.material:material:1.10.0")
	implementation("androidx.constraintlayout:constraintlayout:2.1.4")
	implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
	implementation("com.google.firebase:firebase-auth:22.3.0")
	implementation("com.google.firebase:firebase-auth")
	implementation("com.google.firebase:firebase-database:20.3.0")
	implementation("androidx.annotation:annotation:1.6.0")
	implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
	implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
	implementation("com.google.firebase:firebase-auth")
	implementation("com.google.android.gms:play-services-auth:20.7.0")
	implementation("com.squareup.picasso:picasso:2.71828")
	implementation("com.google.firebase:firebase-firestore:24.10.0")
	implementation("com.journeyapps:zxing-android-embedded:4.3.0")
	implementation("com.google.zxing:core:3.4.1")
	implementation("com.razorpay:checkout:1.6.4")
	testImplementation("junit:junit:4.13.2")
	androidTestImplementation("androidx.test.ext:junit:1.1.5")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

buildscript {
	dependencies {
		classpath("com.google.gms:google-services:4.4.0")
	}
}
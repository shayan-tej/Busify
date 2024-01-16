// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
	id("com.android.application") version "8.2.1" apply false
	id("com.google.gms.google-services") version "4.4.0" apply true
}

buildscript {
	dependencies {
		classpath("com.google.gms:google-services:4.4.0")
	}
	repositories {
		google()
		mavenCentral()
	}
}
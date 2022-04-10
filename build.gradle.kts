buildscript {

  repositories {
    mavenCentral()
    google()

  }
  dependencies {
    classpath 'com.google.gms:google-services:4.3.10'
    classpath 'com.google.firebase:firebase-crashlytics-gradle:2.7.1'
    classpath "com.android.tools.build:gradle:$Versions.androidPluginVersion"
    classpath "org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:2.8"
    classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$Versions.kotlinVersion"
    classpath "org.jetbrains.kotlin:kotlin-allopen:$Versions.kotlinVersion"
    classpath "org.jacoco:org.jacoco.core:0.8.7"
  }
}

plugins {
  id "com.diffplug.gradle.spotless" version "3.30.0"
}

repositories {
  jcenter()
  mavenCentral()
  google()
  maven {
    url "https://oss.sonatype.org/content/repositories/snapshots"
  }
}

subprojects {
  repositories {
    mavenCentral()
    google()
    maven {
      url "https://oss.sonatype.org/content/repositories/snapshots"
    }
    jcenter() // Currently required by url-manager
    maven {
      url "https://kingargyle.github.io/repo"
    }
    maven { url 'https://jitpack.io' }
  }

  apply plugin: "com.diffplug.gradle.spotless"

  spotless {
    format 'xml', {
      target '**/layout*/*.xml'
      indentWithSpaces(2)
      eclipseWtp('xml').configFile rootProject.file('spotless.xml.prefs')
    }
  }

 tasks.withType(Test) {

   testLogging {
     exceptionFormat = "full"
   }
  }
}

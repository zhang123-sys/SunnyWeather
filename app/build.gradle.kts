import java.util.Properties
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.sunnyweather.android"
    compileSdk = 34

    viewBinding {
        enable=true
    }

    defaultConfig {
        applicationId = "com.sunnyweather.android"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release"){
            val keyPropertiesFile = file("../gradle.properties") // 假设你有一个包含签名信息的key.properties文件
            val keyProperties = Properties()
            try {
                keyProperties.load(keyPropertiesFile.inputStream())
                storeFile = file(keyProperties.getProperty("KEY_PATH"))
                storePassword = file(keyProperties.getProperty("KEY_PASS")).toString()
                keyAlias = file(keyProperties.getProperty("ALIAS_NAME")).toString()
                keyPassword = file(keyProperties.getProperty("ALIAS_PASS")).toString()
            }catch (e:Exception)
            {
                e.printStackTrace()
            }

        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    // livedata
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")

    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1-Beta")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1-Beta")


}
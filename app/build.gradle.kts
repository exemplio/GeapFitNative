plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.jetbrainsKotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.exemplio.geapfitmobile"
    compileSdk = 35

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.exemplio.geapfitmobile"
        minSdk = 26
        targetSdk = 35
        versionCode = 7
        versionName = "1.0.7"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField ("String", "AUTH_ID", "\"dfb8aca9-5259-4582-ad81-9ffe0ae75ad3\"")
        buildConfigField ("String", "CONTEXT_PATH", "\"api/\"")
        buildConfigField ("String", "CONTEXT_AUTH_PATH", "\"api/login/\"")
        buildConfigField ("String", "CLIENT_ID", "\"your_client_id_value\"")
        buildConfigField ("String", "API_URL", "\"express-mongo-cobq.onrender.com\"")
        buildConfigField ("String", "API_AUTH_URL", "\"express-mongo-cobq.onrender.com\"")
        buildConfigField ("String", "API_KEY", "\"AIzaSyCNGkGWhHJU8vSBEY37RYnDxTTQAC4sk-k\"")
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    //Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.kotlinx.serialization.json)

    //Navigation
    implementation(libs.androidx.navigation.compose)

    //DI
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation)
    implementation(libs.places)
    ksp(libs.hilt.compiler)

    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.adapter)
    implementation(libs.retrofit.gson)
    implementation(libs.coil.compose)

    //Style
    implementation(libs.kalendar.foundation)
    implementation(libs.kotlinx.datetime)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.androidx.core.splashscreen)

    //Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
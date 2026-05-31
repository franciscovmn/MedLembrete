plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)

    //google-services
    id("com.google.gms.google-services")
}

android {
    namespace = "br.edu.ifpb.pdm.medlembrete"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "br.edu.ifpb.pdm.medlembrete"
        minSdk = 24
        targetSdk = 36
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    //biblioteca de icones
    implementation("androidx.compose.material:material-icons-extended")

    //navegacao
    implementation("androidx.navigation:navigation-compose:2.9.8")

    //firebase BOM
    implementation(platform("com.google.firebase:firebase-bom:34.13.0"))

    //firebase firestore
    implementation("com.google.firebase:firebase-firestore-ktx:25.1.4")

    //rede: Retrofit + OkHttp + Gson
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging.interceptor)

    //coroutines (suspend funcs no Retrofit)
    implementation(libs.kotlinx.coroutines.android)

    //coroutines + Firebase Tasks (.await())
    implementation(libs.kotlinx.coroutines.play.services)

    //testes
    testImplementation(libs.kotlinx.coroutines.test)
}
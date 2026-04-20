plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.detekt)
}

val ciVersionCode = providers.gradleProperty("ciVersionCode").orNull?.toIntOrNull()
val ciVersionName = providers.gradleProperty("ciVersionName").orNull

android {
    namespace = "com.example.flat_rent_app"
    compileSdk = 36

    signingConfigs {
        val keystorePath = System.getenv("RELEASE_KEYSTORE_FILE")
        if (!keystorePath.isNullOrBlank()) {
            val keystoreFile = rootProject.file(keystorePath)
            if (keystoreFile.exists()) {
                create("releaseSigning") {
                    storeFile = keystoreFile
                    storePassword = System.getenv("KEY_STORE_PASSWORD")
                    keyAlias = System.getenv("KEY_ALIAS").orEmpty()
                    keyPassword = System.getenv("KEY_PASSWORD")
                }
            }
        }
    }

    defaultConfig {
        applicationId = "com.example.flat_rent_app"
        minSdk = 26
        targetSdk = 36
        versionCode = ciVersionCode ?: 1
        versionName = ciVersionName ?: "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            val releaseSigning = signingConfigs.findByName("releaseSigning")
            when {
                // CI: неподписанный APK → подпись шагом sign-android-release в workflow
                project.hasProperty("ciExternalSigning") -> signingConfig = null
                releaseSigning != null -> signingConfig = releaseSigning
                project.hasProperty("ciUseDebugSigning") -> signingConfig =
                    signingConfigs.getByName("debug")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_20
        targetCompatibility = JavaVersion.VERSION_20
    }
    kotlinOptions {
        jvmTarget = "20"
        freeCompilerArgs = listOf("-XXLanguage:+PropertyParamAnnotationDefaultTargetMode")
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

detekt {
    toolVersion = libs.versions.detekt.get()
    buildUponDefaultConfig = true
    parallel = true
    config.setFrom(rootProject.file("config/detekt/detekt.yml"))
}

tasks.withType<dev.detekt.gradle.Detekt>().configureEach {
    jvmTarget.set("20")
}

dependencies {
    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation("com.google.firebase:firebase-messaging-ktx:24.0.0")

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Coil
    implementation(libs.coil.compose)

    // Coroutines
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    // Retrofit + Moshi + OkHttp
    implementation(libs.retrofit)
    implementation(libs.retrofit.moshi)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.okhttp.logging)
    ksp(libs.moshi.kotlin.codegen)

    // Navigation
    implementation(libs.navigation.compose)

    // AndroidX + Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.viewmodel.compose)

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

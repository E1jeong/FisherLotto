import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics.plugin)
    id("dagger.hilt.android.plugin")
    alias(libs.plugins.ksp)
}

fun getSecretOrLocalProperty(key: String, defaultValue: String = ""): String {
    val secretsFile = rootProject.file("secrets.properties")
    if (secretsFile.exists()) {
        val props = Properties()
        secretsFile.inputStream().use { props.load(it) }
        val value = props.getProperty(key)
        if (!value.isNullOrBlank()) return value
    }
    val localProperties = com.android.build.gradle.internal.cxx.configure.gradleLocalProperties(rootDir, providers)
    return localProperties.getProperty(key, defaultValue) ?: defaultValue
}

android {
    namespace = "com.queentech.fisherlotto"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.queentech.fisherlotto"
        minSdk = 26
        targetSdk = 36
        versionCode = project.findProperty("versionCode")?.toString()?.toInt() ?: 8 // CI는 -PversionCode로 주입
        versionName = project.findProperty("versionName")?.toString() ?: "0.0.8" // CI는 태그(v0.0.9 → 0.0.9)로 주입

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("config") {
            keyAlias = getSecretOrLocalProperty("KEYSTORE_KEY_ALIAS", "fisherlotto")
            keyPassword = getSecretOrLocalProperty("KEYSTORE_KEY_PASSWORD", "")
            storePassword = getSecretOrLocalProperty("KEYSTORE_STORE_PASSWORD", "")
            storeFile = rootProject.file("fisherlotto.jks")
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("config")
        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // 로컬: jks가 있으면 업로드 키로 서명 / CI: jks 부재 → 미서명 빌드 후 deploy.yml 서명 단계에서 서명
            if (rootProject.file("fisherlotto.jks").exists()) {
                signingConfig = signingConfigs.getByName("config")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.material.icon.extended)

    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":presentation"))

    implementation(libs.google.hilt)
    ksp(libs.google.hilt.compiler)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.crashlytics)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.compose.hilt)

    implementation(libs.androidx.work.runtime.ktx)
}

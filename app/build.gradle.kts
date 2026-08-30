import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics.plugin)
    id("dagger.hilt.android.plugin")
    alias(libs.plugins.ksp)
}

// AdMob 앱 ID. 실 ID는 secrets.properties 또는 local.properties의 ADMOB_APP_ID로 주입하고,
// 값이 없거나 debug 빌드일 때는 Google 공식 테스트 ID를 쓴다.
val ADMOB_TEST_APP_ID = "ca-app-pub-3940256099942544~3347511713"

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
        versionCode = 7
        versionName = "0.0.7"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        manifestPlaceholders["admobAppId"] =
            getSecretOrLocalProperty("ADMOB_APP_ID", ADMOB_TEST_APP_ID)
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
            // 개발 중 실제 광고를 클릭하면 무효 트래픽으로 AdMob 계정이 정지될 수 있다.
            manifestPlaceholders["admobAppId"] = ADMOB_TEST_APP_ID
        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
//            signingConfig = signingConfigs.getByName("config") // github actions 자동 배포를 위해 주석처리
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

// CI 러너나 개발 환경에 배포 설정이 없을 때 release 빌드 시 조기 감지한다.
tasks.matching { it.name == "assembleRelease" || it.name == "bundleRelease" }.configureEach {
    doFirst {
        val missing = listOf("ADMOB_APP_ID", "ADMOB_REWARDED_AD_UNIT_ID")
            .filter { getSecretOrLocalProperty(it).isBlank() }

        if (missing.isEmpty()) return@doFirst

        // 메시지는 영문으로 둔다. Windows 콘솔 코드페이지에서 한글이 깨져 읽을 수 없다.
        val message = "Missing release config in secrets.properties/local.properties: $missing"
        if (System.getenv("CI") == "true") {
            throw GradleException("$message - add a step that writes secrets/local.properties from GitHub Secrets.")
        }
        logger.warn("WARNING: $message - local verification build only, do NOT distribute this artifact.")
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
    implementation(libs.kotlinx.serialization.json)
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

    implementation(libs.google.ads)
    implementation(libs.androidx.work.runtime.ktx)
}

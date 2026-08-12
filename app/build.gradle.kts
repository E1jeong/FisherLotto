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

// AdMob 앱 ID. 실 ID는 local.properties의 ADMOB_APP_ID로 주입하고,
// 값이 없거나 debug 빌드일 때는 Google 공식 테스트 ID를 쓴다.
val ADMOB_TEST_APP_ID = "ca-app-pub-3940256099942544~3347511713"

android {
    namespace = "com.queentech.fisherlotto"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.queentech.fisherlotto"
        minSdk = 26
        targetSdk = 36
        versionCode = 7
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val localProperties = com.android.build.gradle.internal.cxx.configure.gradleLocalProperties(rootDir, providers)
        manifestPlaceholders["admobAppId"] =
            localProperties.getProperty("ADMOB_APP_ID", ADMOB_TEST_APP_ID)
    }

    signingConfigs {
        create("config") {
            val localProperties = com.android.build.gradle.internal.cxx.configure.gradleLocalProperties(rootDir, providers)
            keyAlias = localProperties.getProperty("KEYSTORE_KEY_ALIAS", "fisherlotto")
            keyPassword = localProperties.getProperty("KEYSTORE_KEY_PASSWORD", "")
            storePassword = localProperties.getProperty("KEYSTORE_STORE_PASSWORD", "")
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

// CI 러너에는 local.properties가 없어 위 주입값이 조용히 폴백/null로 떨어진다.
// 그대로 bundleRelease가 성공하면 테스트 광고가 박힌 AAB가 Play에 올라가고,
// 앱은 정상 동작해 보이기 때문에 발견이 늦다. CI에서는 빌드를 실패시킨다.
tasks.matching { it.name == "assembleRelease" || it.name == "bundleRelease" }.configureEach {
    doFirst {
        val localProperties = com.android.build.gradle.internal.cxx.configure.gradleLocalProperties(rootDir, providers)
        val missing = listOf("ADMOB_APP_ID", "ADMOB_REWARDED_AD_UNIT_ID")
            .filter { localProperties.getProperty(it).isNullOrBlank() }

        if (missing.isEmpty()) return@doFirst

        // 메시지는 영문으로 둔다. Windows 콘솔 코드페이지에서 한글이 깨져 읽을 수 없다.
        val message = "Missing release config in local.properties: $missing"
        if (System.getenv("CI") == "true") {
            throw GradleException("$message - add a step that writes local.properties from GitHub Secrets.")
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
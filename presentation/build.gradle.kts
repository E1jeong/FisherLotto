plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    id("dagger.hilt.android.plugin")
    alias(libs.plugins.ksp)
}

// 보상형 광고 단위 ID. 실 ID는 local.properties의 ADMOB_REWARDED_AD_UNIT_ID로 주입하고,
// 값이 없거나 debug 빌드일 때는 Google 공식 테스트 ID를 쓴다.
val ADMOB_TEST_REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"

android {
    namespace = "com.queentech.presentation"
    compileSdk = 35

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        val localProperties = com.android.build.gradle.internal.cxx.configure.gradleLocalProperties(rootDir, providers)
        buildConfigField(
            "String",
            "ADMOB_REWARDED_AD_UNIT_ID",
            "\"${localProperties.getProperty("ADMOB_REWARDED_AD_UNIT_ID", ADMOB_TEST_REWARDED_AD_UNIT_ID)}\""
        )
    }

    buildTypes {
        debug {
            // 개발 중 실제 광고를 클릭하면 무효 트래픽으로 AdMob 계정이 정지될 수 있다.
            buildConfigField(
                "String",
                "ADMOB_REWARDED_AD_UNIT_ID",
                "\"$ADMOB_TEST_REWARDED_AD_UNIT_ID\""
            )
        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.material3)
    implementation(libs.material.icon.extended)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.turbine)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.orbit.test)
    androidTestImplementation(libs.androidx.test.core)

    implementation(project(":domain"))

    implementation(libs.google.hilt)
    ksp(libs.google.hilt.compiler)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.compose.hilt)
    androidTestImplementation(libs.google.hilt.testing)
    kspAndroidTest(libs.google.hilt.compiler)

    implementation(libs.google.gson)
    implementation(libs.google.ads)
    implementation(libs.google.guava)

    implementation(libs.coil.compose)

    implementation(libs.orbit.core)
    implementation(libs.orbit.compose)
    implementation(libs.orbit.viewmodel)

    implementation(libs.paging.compose)
    implementation(libs.paging.runtime)
    implementation(libs.paging.common)

    implementation(libs.camera)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)
    implementation(libs.mlkit)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.messaging)

    implementation(libs.lottie.compose)
}
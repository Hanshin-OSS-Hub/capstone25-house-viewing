plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

fun loadEnvFile(path: String): Map<String, String> {
    val envFile = file(path)
    if (!envFile.exists()) return emptyMap()
    return envFile.readLines()
        .asSequence()
        .map { it.trim() }
        .filter { it.isNotEmpty() && !it.startsWith("#") && it.contains("=") }
        .associate { line ->
            val idx = line.indexOf("=")
            val key = line.substring(0, idx).trim()
            val value = line.substring(idx + 1).trim().removeSurrounding("\"")
            key to value
        }
}

fun normalizeSecret(raw: String): String {
    return raw.trim()
        .removeSurrounding("'")
        .removeSurrounding("\"")
}

fun resolveKakaoKey(): String {
    // 프로젝트를 capstone 루트로 열었는지, app 하위로 열었는지 모두 대응
    val root = rootProject.projectDir
    val envCandidates = listOf(
        root.resolve(".env"),
        root.resolve("../.env").normalize(),
        root.resolve("../../.env").normalize()
    ).distinct()

    for (candidate in envCandidates) {
        val envMap = loadEnvFile(candidate.absolutePath)
        val value = normalizeSecret(envMap["KAKAO_REST_API_KEY"].orEmpty())
        if (value.isNotBlank()) return value
    }

    return ""
}

val kakaoRestApiKey = resolveKakaoKey()

android {
    namespace = "com.capstone.houseviewingapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.capstone.houseviewingapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // ADB reverse 사용 기준: 앱에서 localhost:8080 으로 백엔드 호출
        // (USB 연결 후 `adb reverse tcp:8080 tcp:8080` 1회 실행)
        buildConfigField("String", "API_BASE_URL", "\"http://127.0.0.1:8080/\"")
        buildConfigField(
            "String",
            "KAKAO_REST_API_KEY",
            "\"${kakaoRestApiKey.replace("\"", "\\\"")}\""
        )
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
        viewBinding = true
        buildConfig = true
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.5")
    implementation(libs.androidx.room.ktx)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.retrofit.converter.scalars)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.fragment.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
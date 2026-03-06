plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.baselineprofile)
}

android {
    namespace = "com.example.chatapplication"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.chatapplication"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file("my-release-key.jks")
            storePassword = "x123x123"
            keyAlias = "key0"
            keyPassword = "x123x123"
        }
    }


    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true // 开启资源缩减
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }

        // releaseDebug，用来调试接近 release 的构建
        create("releaseDebug") {
            initWith(getByName("release"))        // 继承 release 的全部配置
            isDebuggable = true                   // 允许调试 → Network Inspector 可用
            applicationIdSuffix = ".releaseDebug" // 不和正式包冲突
            signingConfig = signingConfigs.getByName("release")
        }

    }
    // 在 app 模块的 build.gradle.kts 中
    baselineProfile {
        // 💡 只有当你手动运行 "Generate Baseline Profile" 任务时才生成
        // 这能防止普通点击 "Run" 时触发复杂的 Profile 生成和应用逻辑
        automaticGenerationDuringBuild = false

        // 💡 如果在安装时依然频繁报错，可以尝试开启这一行（仅限开发阶段）
        // skipGeneration = true
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
    // 网络与基础
    implementation(libs.socket.io.client)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)

    // Compose (统一使用 BOM 管理，移除重复的手动引用)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.foundation) // 只保留这一个 foundation 即可
    implementation(libs.androidx.compose.runtime)

    // Hilt 注入
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.androidx.compose.animation.core)
    "baselineProfile"(project(":baselineprofile"))
    ksp(libs.hilt.compiler)

    // 数据持久化
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // 网络请求
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    // 图片加载
    implementation(libs.coil.compose)

    // 测试库 (保持在 test/androidTestScope 中，不要误入 implementation)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
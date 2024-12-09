plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "no.uio.ifi.in2000.weatheru"
    compileSdk = 34

    defaultConfig {
        applicationId = "no.uio.ifi.in2000.weatheru"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.activity:activity-compose:1.9.0")
    implementation(platform("androidx.compose:compose-bom:2024.05.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation( "androidx.compose.material3:material3:$1.2.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.compose.material3:material3-window-size-class-android:1.2.1")
    implementation("androidx.wear.compose:compose-material:1.3.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.05.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Ktor for network requests and serialization for JSON parsing
    val ktorVersion = "2.3.8"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-android:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-gson:$ktorVersion")
    implementation ("androidx.navigation:navigation-compose:2.7.7")
    implementation("com.google.maps.android:android-maps-utils:3.8.2")
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("io.ktor:ktor-client-core:<ktor_version>")

    implementation("io.ktor:ktor-client-json:$ktorVersion")
    implementation("io.ktor:ktor-client-gson:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")

    // Dependencies for room
    implementation("androidx.room:room-runtime:2.6.1")
    // Using ksp instead of kapt for increased performance and reliability
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    //  Dependencies for viewModels() / application
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.0")
    implementation("androidx.activity:activity-ktx:1.9.0")


    // Dependency for ProcessLifecycleOwner
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.0")
    implementation("androidx.lifecycle:lifecycle-process:2.8.0")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.8.0")

    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")


    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")

    // Google Play services for device location
    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation("com.google.accompanist:accompanist-permissions:0.35.0-alpha")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.0")

    //resize
    implementation("androidx.compose.animation:animation-core-android:1.6.7@aar")

    // Ktor services for proxy
    implementation("io.ktor:ktor-server-compression:$ktorVersion")
    implementation("io.ktor:ktor-client-websockets:$ktorVersion")


    implementation("com.google.accompanist:accompanist-pager:0.20.0")


    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.compose.runtime:runtime:1.6.7")


    implementation("com.google.accompanist:accompanist-pager-indicators:0.20.0")

    //Navigation
    val navVersion = "2.7.7"

    // Kotlin
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")

    // Feature module Support
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$navVersion")

    // Testing Navigation
    androidTestImplementation("androidx.navigation:navigation-testing:$navVersion")

    // Jetpack Compose Integration
    implementation("androidx.navigation:navigation-compose:$navVersion")

    //MPAndroidChart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation("androidx.compose.material:material:1.6.7")
    implementation("androidx.compose.ui:ui-tooling:1.6.7")



    implementation("androidx.compose.ui:ui:1.6.7")
    implementation("androidx.compose.runtime:runtime-livedata:1.6.7")


    //  coil to show gifs
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation("io.coil-kt:coil-gif:2.4.0")
    implementation("io.coil-kt:coil-svg:2.4.0")

    // JUnit for unit testing
    testImplementation("junit:junit:4.13.2")

    // Mockito for mocking in tests
    testImplementation("org.mockito:mockito-core:4.8.0")

    // Mockito-Kotlin for better Kotlin support (unofficial library)
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")

    // AndroidX Test libraries for LiveData and more
    testImplementation("androidx.arch.core:core-testing:2.2.0")

    // Coroutines test support
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.1")

    // Needed for instrumented tests (Android-specific)
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


}

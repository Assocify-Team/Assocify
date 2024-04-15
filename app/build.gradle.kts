import org.jetbrains.kotlin.util.capitalizeDecapitalize.toLowerCaseAsciiOnly
import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.sonarqube") version "4.4.1.3373"
    id("com.ncorti.ktfmt.gradle") version "0.16.0"
    id("com.google.gms.google-services")
}

android {
    namespace = "com.github.se.assocify"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.github.se.assocify"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    val keystorePropertiesFile = rootProject.file("keystore.properties")

    // Initialize a new Properties() object called keystoreProperties.
    val keystoreProperties = Properties()

    // Load your keystore.properties file into the keystoreProperties object.
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))

    signingConfigs {
        create("release") {
            // You need to specify either an absolute path or include the
            // keystore file in the same directory as the build.gradle file.
            storeFile = file("../keystore.jks")
            storePassword = keystoreProperties["storePassword"] as String
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
        }
    }


    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
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
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/LICENSE*.md"
        }
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }

        packaging {
            jniLibs {
                useLegacyPackaging = true
            }
        }
    }
}

dependencies {
    // When using the BoM, don't specify versions in order to use latest of each in library

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.4")) // Firebase BoM
    implementation("com.google.firebase:firebase-analytics") // google analytics
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-auth-ktx:22.3.1") // authentication (remove version numbre?)
    implementation("com.google.firebase:firebase-database-ktx:20.3.1")

    // Google Authentication
    implementation("com.google.android.gms:play-services-auth:21.0.0")

    // Google Maps
    val mapsComposeVersion = "4.3.0" // update to 4.3.3 ?
    implementation("com.google.maps.android:maps-compose:$mapsComposeVersion")
    implementation("com.google.maps.android:maps-compose-utils:$mapsComposeVersion")
    implementation("com.google.android.gms:play-services-maps:18.2.0")

    // Androidx
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.appcompat:appcompat:1.6.1") // access to newer API from older API
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4") // maybe unused?

    // Material Design 3
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.material3:material3-android:1.2.1")
    implementation("androidx.compose.material:material-icons-extended") // extra icons

    implementation("com.google.android.material:material:1.11.0") // material design - to remove ?

    // Navigation
    val navVersion = "2.7.7"
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")
    implementation("androidx.navigation:navigation-compose:2.6.0-rc01") // compose

    // Coil (image loading)
    implementation("io.coil-kt:coil-compose:2.0.0")

    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:3.14.9")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2") // json serialization

    // Compose
    implementation(platform("androidx.compose:compose-bom:2024.03.00")) // latest ?
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")

    // Junit & Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.03.00")) // remove ?
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.4")
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.4")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.4")

    // Espresso
    val espressoVersion = "3.5.1"
    androidTestImplementation("androidx.test.espresso:espresso-core:$espressoVersion")
    androidTestImplementation("androidx.test.espresso:espresso-intents:$espressoVersion")

    // Kaspresso
    val kaspressoVersion = "1.5.5"
    androidTestImplementation("com.kaspersky.android-components:kaspresso:$kaspressoVersion")
    androidTestImplementation("com.kaspersky.android-components:kaspresso-allure-support:$kaspressoVersion") // allure support
    androidTestImplementation("com.kaspersky.android-components:kaspresso-compose-support:$kaspressoVersion") // compose support

    // Robolectric
    testImplementation("org.robolectric:robolectric:4.11.1")

    // Mockito
    val mockitoVersion = "5.11.0"
    androidTestImplementation("org.mockito:mockito-android:$mockitoVersion")

    // Mockk
    val mockkVersion = "1.13.10"
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("io.mockk:mockk-android:$mockkVersion")
    testImplementation("io.mockk:mockk-agent:$mockkVersion")
    androidTestImplementation("io.mockk:mockk-android:$mockkVersion")
    androidTestImplementation("io.mockk:mockk-agent:$mockkVersion")

}

tasks.register("jacocoTestReport", JacocoReport::class) {
    mustRunAfter("testDebugUnitTest", "connectedDebugAndroidTest")

    reports {
        xml.required = true
        html.required = true
    }

    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*",
    )
    val debugTree = fileTree("${project.buildDir}/tmp/kotlin-classes/debug") {
        exclude(fileFilter)
    }
    val mainSrc = "${project.projectDir}/src/main/java"

    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(fileTree(project.buildDir) {
        include("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
        include("outputs/code_coverage/debugAndroidTest/connected/*/coverage.ec")
    })
}

// Avoid redundant tests, debug is sufficient
tasks.withType<Test> {
    onlyIf {
        !name.toLowerCaseAsciiOnly().contains("release")
    }
}

sonar {
    properties {
        property("sonar.projectKey", "Assocify-Team_Assocify")
        property("sonar.organization", "assocify-team")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}

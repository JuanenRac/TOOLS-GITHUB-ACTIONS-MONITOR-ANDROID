plugins { id("com.android.application"); id("org.jetbrains.kotlin.plugin.compose") }

android {
    namespace = "es.electrohobby3d.ghamonitor"
    compileSdk = 37
    defaultConfig {
        applicationId = "es.electrohobby3d.ghamonitor"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "0.0.1"
    }
    buildFeatures { compose = true; buildConfig = true }
    // The release is shrunk and obfuscated (R8). Signing is not done here: an APK is signed afterwards with the maintainer's own key, kept outside the repository.
    buildTypes { release { isMinifyEnabled = true; isShrinkResources = true; proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro") } }
    // Lint stops the build on an error. What is deliberate is written down in lint.xml with its reason.
    lint { abortOnError = true; checkDependencies = false; lintConfig = file("lint.xml") }
}

// The version lives in two places the Android tools need (here, and the manifest every other tool reads); a build stops when they differ,
// so a build can never carry a number the project's own manifest does not say.
afterEvaluate {
    val manifestVersion = (groovy.json.JsonSlurper().parse(rootProject.file("electro-hobby-3d.project.json")) as Map<*, *>)["version"]
    val appVersion = android.defaultConfig.versionName
    check(manifestVersion == appVersion) { "electro-hobby-3d.project.json says $manifestVersion but versionName is $appVersion: change them together" }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2026.09.00"))
    implementation("androidx.activity:activity-compose:1.12.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.11.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.11.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.11.0")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.11.0")
    testImplementation("junit:junit:4.13.2")
    // The Android stubs of org.json do nothing in local tests; the real library lets the parsers be tested on the JVM.
    testImplementation("org.json:json:20260814")
}

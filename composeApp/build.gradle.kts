import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import org.gradle.internal.os.OperatingSystem

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.navigation.safeargs)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.googleServices)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName.set("composeApp")
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            // Html to PDF
            implementation(libs.html.to.pdf.convertor)
        }
        commonMain{
            resources.srcDirs("src/commonMain/composeResources")

            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.androidx.lifecycle.viewmodelCompose)
                implementation(libs.androidx.lifecycle.runtimeCompose)

                // Compose-Navigation
                implementation(libs.navigation.compose)

                // Serialization
                implementation(libs.kotlinx.serialization.json)

                // Material
                implementation(compose.materialIconsExtended)

                implementation(libs.ktor.utils)

                //Firebase
                implementation(libs.firebase.common)
                implementation(project.dependencies.platform(libs.firebase.bom))
                implementation(libs.firebase.database)


                // add date time library
                implementation(libs.kotlinx.datetime)

                implementation(libs.coil.compose)
                implementation(libs.coil.network.okhttp)
            }
        }
        iosMain.dependencies {
            // Ktor iOS engine
            implementation(libs.ktor.client.darwin)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            // Html to PDF
            implementation(libs.openhtmltopdf.pdfbox)
            implementation(libs.commons.logging)

            implementation(libs.firebase.admin)

        }
    }
}

android {
    namespace = "com.bilty.generator"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.bilty.generator"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "com.bilty.generator.MainKt"

        nativeDistributions {
            /*sign in tool
              * Windows: .exe (executable), .msi (installer)
              * Linux: .deb (Debian package)
              * macOS: .dmg (Disk Image)
             */
            targetFormats(TargetFormat.Dmg, TargetFormat.Exe, TargetFormat.Msi, TargetFormat.Deb)
            val appName = "Bilty Generator"
            packageName = "com.bilty.generator"
            packageVersion = "1.0.0"

            // Set vendor/publisher information (fixes "Unknown" publisher on Windows)
            vendor = "Parallel Quintillion Coders"

            // Optional: You can add more metadata
            description = "Bilty Generator Application"
            copyright = "© 2025 Parallel Quintillion Coders. All rights reserved."

            // Ensure resources are included in the packaged application
            includeAllModules = true

            macOS {
                iconFile.set(project.file("src/commonMain/composeResources/drawable/pqc_logo_ico.ico"))
                packageName = appName
            }
            windows {
                packageName = appName
                iconFile.set(project.file("src/commonMain/composeResources/drawable/pqc_logo_ico.ico"))

                // Windows-specific properties for proper publisher information
                menuGroup = "Parallel Quintillion Coders"
                // IMPORTANT: This UUID must NEVER be changed across versions - it enables proper upgrades
                upgradeUuid = "0444a49d-d0cc-4ed9-9034-1f08835615bb"
            }
            linux {
                packageName = appName
                iconFile.set(project.file("src/commonMain/composeResources/drawable/pqc_logo_ico.ico"))
            }
        }

        // Required for files browser
        jvmArgs("--add-opens", "java.desktop/sun.awt=ALL-UNNAMED")
        jvmArgs("--add-opens", "java.desktop/java.awt.peer=ALL-UNNAMED")

        // Mac-specific arguments
        if (System.getProperty("os.name").contains("Mac")) {
            jvmArgs("--add-opens", "java.desktop/sun.lwawt=ALL-UNNAMED")
            jvmArgs("--add-opens", "java.desktop/sun.lwawt.macosx=ALL-UNNAMED")
        }

        // ProGuard configuration for release builds (currently disabled)
        buildTypes.release.proguard {
            configurationFiles.from("compose-desktop.pro")
        }
    }
}

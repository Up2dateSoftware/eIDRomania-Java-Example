plugins {
    java
    application
    id("com.google.cloud.artifactregistry.gradle-plugin")
}

group = "com.example"
version = "1.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

application {
    mainClass = "com.example.eid.Main"
    // javax.smartcardio is in java.smartcardio module (JDK 9+)
    applicationDefaultJvmArgs = listOf("--add-modules=java.smartcardio")
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://europe-west1-maven.pkg.dev/eid-romania/eid-romania-sdk")
        credentials {
            username = "_json_key_base64"
            password = "YOUR_SDK_KEY_HERE" // provided by Up2Date Software SRL with your license
        }
        authentication {
            create<BasicAuthentication>("basic")
        }
    }
}

dependencies {
    implementation("com.up2date.eidromania:eidromania-desktop-sdk:1.0.5")
    // Kotlin stdlib and BouncyCastle needed at compile time only.
    // Both are bundled in the fat JAR at runtime.
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib:2.0.21")
}

// Forward stdin so interactive prompts work with ./gradlew run
tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

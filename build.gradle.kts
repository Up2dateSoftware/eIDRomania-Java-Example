plugins {
    java
    application
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
    // eIDRomania Desktop SDK (public read access — no credentials required)
    maven {
        url = uri("https://europe-west1-maven.pkg.dev/eid-romania/eid-romania-sdk")
    }
}

dependencies {
    implementation("com.up2date.eidromania:eidromania-desktop-sdk:1.0.6")
    // Kotlin stdlib and BouncyCastle needed at compile time only.
    // Both are bundled in the fat JAR at runtime.
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib:2.0.21")
}

// Forward stdin so interactive prompts work with ./gradlew run
tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

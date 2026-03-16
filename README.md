# eIDRomania Desktop SDK — Java Example

Demonstrates how to integrate the **eIDRomania Desktop SDK** into a Java desktop application to read Romanian electronic identity cards (CEI) via PC/SC smart card readers.

## What this example shows

- Initializing the SDK with a license key
- Listing connected PC/SC readers
- Reading a card with **CAN only** — retrieves MRTD data (name, MRZ, face photo) without requiring a PIN
- Reading a card with **CAN + PIN** — retrieves full data including address
- Typed error handling for every failure scenario (wrong CAN, wrong PIN, card locked, reader not found, etc.)
- Progress callbacks during reading

> **Tested on:** REINER SCT cyberJack RFID basis (NFC/contactless reader) on macOS

## Prerequisites

| Requirement | Notes |
|-------------|-------|
| JDK 17+ | [Eclipse Temurin](https://adoptium.net/) recommended |
| PC/SC smart card reader | Contact (chip) or NFC/contactless — NFC readers can read biometric photos |
| Romanian eID card (CEI) | Physical card required — no emulation available |
| eIDRomania Desktop SDK license key | Contact [office@up2date.ro](mailto:office@up2date.ro) |

## Setup

### 1. Add your license key

Edit `src/main/java/com/example/eid/Main.java` and replace the placeholder values:

```java
private static final String LICENSE_KEY = "YOUR_LICENSE_KEY_HERE";
private static final String APP_IDENTIFIER = "com.example.eid";
```

Use the `APP_IDENTIFIER` that was specified when your license was issued.

## Run

```bash
./gradlew run
```

The app will:
1. Initialize the SDK
2. List all connected PC/SC readers
3. Prompt for the CAN (6 digits printed on the card front, near the photo)
4. Read the card — keep it still on the reader during reading (~5–10 seconds)
5. Prompt for the PIN (optional, for address and extended data)

### Windows note

On Windows with ACS ACR1252 readers, the first read after placing the card may fail with `READ_FAILURE`. This is a known hardware issue — physically remove and reinsert the card to resolve it.

## Project structure

```
src/main/java/com/example/eid/
└── Main.java          # Complete example — single file, no framework
build.gradle.kts       # Gradle build with SDK dependency
settings.gradle.kts    # Project name + plugin management
```

## SDK dependency

```kotlin
// build.gradle.kts
repositories {
    mavenCentral()
    maven {
        url = uri("https://europe-west1-maven.pkg.dev/eid-romania/eid-romania-sdk")
        credentials {
            username = "_json_key_base64"
            password = "YOUR_SDK_KEY_HERE" // provided by Up2Date Software SRL with your license
        }
        authentication { create<BasicAuthentication>("basic") }
    }
}

dependencies {
    implementation("com.up2date.eidromania:eidromania-desktop-sdk:1.0.6")
}
```

## Error handling reference

| Error code | Meaning | Action |
|------------|---------|--------|
| `NOT_INITIALIZED` | `initialize()` not called | Call `sdk.initialize(licenseKey, appId)` first |
| `NO_READER` | No PC/SC reader detected | Connect reader, install driver |
| `NO_CARD` | No card on reader | Place card and retry |
| `TAG_LOST` | Card removed during reading | Keep card still; retry |
| `INVALID_CAN` | Wrong 6-digit CAN | Check CAN on card front |
| `INVALID_PIN` | Wrong PIN (`attemptsRemaining` tells you how many left) | Check PIN; stop at 1 remaining |
| `CARD_LOCKED` | Too many wrong PINs | Card owner must visit DEP office |
| `TIMEOUT` | Communication timeout | Retry; check reader connection |
| `UNSUPPORTED_CARD` | Not a Romanian eID | Only CEI cards supported |
| `READ_FAILURE` | Partial read failure | Remove/reinsert card; retry |
| `UNKNOWN` | Unexpected error | Check `getCause()` |

## License

This example application is provided by **Up2Date Software SRL** for integration reference.
The SDK itself requires a separate commercial license — contact [office@up2date.ro](mailto:office@up2date.ro).

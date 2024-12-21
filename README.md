# GeoKode

GeoKode is a Kotlin Multiplatform library that provides a unified geocoding interface for both Android and iOS platforms, utilizing native platform capabilities. Unlike paid solutions such as Google Places API or Geocod.io, GeoKode leverages free platform-specific geocoding services, making it a cost-effective solution for basic geocoding needs.

## Current Status

⚠️ This library is currently in alpha state (v0.1.0-alpha01).

> **Note**: iOS development is currently paused due to lack of access to Apple hardware. If you find this library useful, please consider [sponsoring me](https://github.com/sponsors/DevinDuricka) to help fund a Mac Mini for continued iOS development and testing.

## Features

- Unified API for geocoding across Android and iOS platforms
- Native implementation using platform-specific geocoding capabilities
- **Free to use** - No API keys or paid services required
- Supports both single string and component-based address inputs
- Returns detailed location information including coordinates, address components, and administrative areas

## Installation

> **Coming Soon**: This library will be available on Maven Central in the future, which will simplify the installation process. For now, please follow the GitHub Packages installation steps below.

### 1. GitHub Package Authentication

To access the package, you'll need to create a GitHub Personal Access Token (classic):

1. Go to [GitHub Settings > Developer Settings > Personal Access Tokens > Tokens (classic)](https://github.com/settings/tokens)
2. Click "Generate new token (classic)"
3. Select at least the `read:packages` scope
4. Generate and copy your token

Then add the GitHub Maven repository to your `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        
        maven {
            name = "GeoKode Github Maven"
            url = uri("https://maven.pkg.github.com/Fable-One/GeoKode")
            credentials {
                username = "YOUR_GITHUB_USERNAME"  // Your GitHub username or email
                password = "YOUR_GITHUB_TOKEN"     // The personal access token you created
            }
        }
    }
}
```

### 2. Add Dependencies

In your `libs.versions.toml`:

```toml
[versions]
geokode = "0.1.0-alpha01"

[libraries]
geokode = { group = "one.fable", name = "geokode", version.ref = "geokode" }
```

In your module's `build.gradle.kts`:


```kotlin
commonMain.dependencies {
    implementation(libs.geokode)
}
```

## Usage

The GeoKode library requires platform-specific initialization but can be used from common code.

### Platform-Specific Initialization

For better architecture and testing, it's recommended to use dependency injection. Here's an example using Koin:

#### Android

```kotlin
// In your Android-specific DI setup
val appModule = module {
    single { GeoKode(androidContext(), maxResults = 5) }
}

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(appModule)
        }
    }
}
```

#### iOS

```kotlin
// In your iOS-specific DI setup
fun initKoin() {
    startKoin {
        modules(module {
            single { GeoKode() }
        })
    }
}
```

### Usage Examples

Once initialized, you can use GeoKode from anywhere in your common code:

```kotlin
val geoKode = get<GeoKode>() 

// Using a single address string
suspend fun searchAddress() {
    val locations = geoKode.getLocation("1600 Amphitheatre Parkway, Mountain View, CA")
    locations?.forEach { location ->
        println("Lat: ${location.latitude}, Lon: ${location.longitude}")
        println("Address: ${location.addressLines.joinToString(", ")}")
    }
}

// Using address components
suspend fun searchByComponents() {
    val locations = geoKode.getLocation(listOf(
        "1600 Amphitheatre Parkway",
        "Mountain View",
        "CA"
    ))
    locations?.firstOrNull()?.let { location ->
        println("Found: ${location.featureName}")
        println("City: ${location.locality}")
        println("State: ${location.adminArea}")
        println("Country: ${location.countryName}")
    }
}
```

## Platform Specifics

### Android

- Utilizes Android's native Geocoder class
- Supports configurable maximum results (1-5 recommended)
- Handles API level differences (pre and post Android 13)
- Free to use with no API key required

### iOS

- Uses native CLGeocoder implementation
- No maximum results configuration (platform limitation)
- Returns all available results
- Free to use with no API key required
- ⚠️ Currently not actively tested due to lack of Apple hardware

## Limitations

- Currently relies on platform-specific geocoding services which may have rate limits
- iOS implementation cannot limit the number of results
- Performance may vary based on platform and network conditions
- iOS development currently paused pending hardware availability

## Roadmap

- [ ] Integration with Google Places API as fallback/alternative
- [ ] Addition to MavenCentral
- [ ] Reverse geocoding support
- [ ] Caching mechanism
- [ ] Additional platform-specific optimizations

## License
This project is licensed under the MIT License:

```txt
MIT License

Copyright (c) 2025 Fable One

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## Support the Project

GeoKode is an open-source project that aims to provide a free geocoding solution for Kotlin Multiplatform developers. Due to recent circumstances, iOS development is currently paused as I no longer have access to Apple hardware for development and testing.
If you find GeoKode useful for your projects, please consider:

1. Sponsoring the project to help fund a Mac Mini for iOS development
2. Contributing to the codebase
3. Reporting issues and suggesting improvements
4. Sharing the project with others

Your support will help ensure continued development and maintenance of both Android and iOS platforms.

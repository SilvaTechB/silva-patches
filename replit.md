# Silva Patches

A Gradle/Kotlin multi-module build project that produces bytecode patches and extension libraries for Android applications (YouTube, YouTube Music, Reddit).

## Architecture

- **Language**: Kotlin (patches) + Java (extensions)
- **Build system**: Gradle 8.14.4 with Kotlin DSL (`*.gradle.kts`)
- **Package manager**: Gradle (dependencies via `gradle/libs.versions.toml`)
- **Target**: Android APK patching via the `morphe-patcher` framework

## Module Structure

- `patches/` — Core Kotlin patch logic for YouTube, YouTube Music, Reddit
- `patches/stub/` — Android API stubs for compiling patches
- `extensions/youtube/` — Java extension library injected into YouTube
- `extensions/music/` — Java extension library injected into YouTube Music
- `extensions/reddit/` — Java extension library injected into Reddit
- `extensions/shared/` — Common utilities across all extensions
- `extensions/shared-youtube/` — Shared YouTube utilities (incl. Protobuf)

## Environment Setup

### Required Secrets
- `GITHUB_TOKEN` — GitHub PAT with `read:packages` for the SilvaTechB registry
- `GITHUB_ACTOR` — GitHub username for the token owner

### Required Environment Variables
- `ANDROID_HOME=/home/runner/android-sdk` — Path to the Android SDK

### Android SDK
Installed at `/home/runner/android-sdk` with:
- Platform: `android-34`
- Build tools: `34.0.0`
- Command-line tools: latest

The SDK was installed manually (not via Nix) because `sdkmanager` was used to accept licenses and install components. The `local.properties` file (gitignored) points to the SDK:
```
sdk.dir=/home/runner/android-sdk
```

### Java Version
The build requires OpenJDK 17 (not GraalVM) for `jlink` compatibility with the Android SDK. The `JAVA_HOME` environment variable points to OpenJDK 17 installed via `jdk17` Nix package. The `build.sh` script prepends `$JAVA_HOME/bin` to `PATH` to ensure the correct `jlink` is used.

## Workflow

**Build**: `bash build.sh` (console workflow)

The `build.sh` script:
1. Prepends `$JAVA_HOME/bin` to PATH (ensures JDK 17's `jlink` is used)
2. Sets `ANDROID_HOME`
3. Runs `./gradlew build generatePatchesList`

**Release**: `bash release.sh [major|minor|patch|x.y.z]`

The `release.sh` script (used because GitHub Actions is disabled for this account):
1. Bumps the version in `gradle.properties` (default: patch bump)
2. Builds the `.mpp` and generates `patches-list.json`
3. Creates a GitHub release with the new tag
4. Uploads `patches-*.mpp` as the release asset
5. Updates `patches-bundle.json` with the new download URL and version
6. Commits and pushes all changes to `main`

Example: `bash release.sh patch` → bumps 1.21.1 → 1.21.2

## Build Output

- `patches/build/libs/` — Compiled patch JARs
- `extensions/*/build/outputs/apk/` — Extension APKs (DEX files injected into target apps)

## Private Registry

The `morphe-patcher` Gradle plugin is fetched from `maven.pkg.github.com/MorpheApp/registry` (private, external dependency — this URL is not changed as it's an upstream dependency). Credentials are injected via `GITHUB_TOKEN` and `GITHUB_ACTOR` environment variables.

## Branding

- GitHub org: `SilvaTechB`
- Repository: `silva-patches`
- Package namespace: `app.silva`
- Resource prefix: `silva_`

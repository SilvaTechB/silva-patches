<div align="center">

<br>

<img src="https://img.shields.io/badge/Silva-Patches-1A3A6E?style=for-the-badge&logoColor=white" alt="Silva Patches" width="300"/>

<br><br>

[![GitHub release](https://img.shields.io/github/v/release/SilvaTechB/silva-patches?color=1A3A6E&label=Download&style=for-the-badge)](https://github.com/SilvaTechB/silva-patches/releases/latest)
[![GitHub](https://img.shields.io/github/license/SilvaTechB/silva-patches?color=2563EB&style=for-the-badge)](LICENSE)

</div>

&nbsp;

# Silva Patches

**Silva Patches** is a collection of bytecode patches and extension libraries for popular Android applications — including YouTube, YouTube Music, and Reddit.

## About

Silva Patches are based on the prior work of [ReVanced](https://github.com/ReVanced/revanced-patches).
All modifications made by Silva, along with their dates, can be found in the Git history.

- **Supported apps:** YouTube, YouTube Music, Reddit
- **Package namespace:** `app.silva`

## Download

Get the latest release from the [Releases page](https://github.com/SilvaTechB/silva-patches/releases/latest).

## Building

### Requirements

- Android SDK 34
- JDK 17
- Gradle 8.14+
- GitHub token with `read:packages` for the SilvaTechB registry

### Build

```bash
bash build.sh
```

Set the following environment variables before building:

```
GITHUB_ACTOR=<your GitHub username>
GITHUB_TOKEN=<your GitHub token with read:packages>
```

## License

Silva Patches are licensed under the [GNU General Public License v3.0](LICENSE), with additional conditions under GPLv3 Section 7.

See the [LICENSE](LICENSE) file for the full GPLv3 terms and the [NOTICE](NOTICE) file for full conditions.

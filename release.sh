#!/bin/bash
set -e

export PATH="$JAVA_HOME/bin:$PATH"
export ANDROID_HOME=/home/runner/android-sdk

GITHUB_REPO="SilvaTechB/silva-patches"

if [ -z "$GITHUB_TOKEN" ] || [ -z "$GITHUB_ACTOR" ]; then
  echo "ERROR: GITHUB_TOKEN and GITHUB_ACTOR must be set"
  exit 1
fi

CURRENT_VERSION=$(grep "^version" gradle.properties | sed 's/version = //')
echo "Current version: $CURRENT_VERSION"

BUMP="${1:-patch}"

IFS='.' read -r MAJOR MINOR PATCH <<< "$CURRENT_VERSION"
case "$BUMP" in
  major) MAJOR=$((MAJOR + 1)); MINOR=0; PATCH=0 ;;
  minor) MINOR=$((MINOR + 1)); PATCH=0 ;;
  patch) PATCH=$((PATCH + 1)) ;;
  *)
    if echo "$BUMP" | grep -qE '^[0-9]+\.[0-9]+\.[0-9]+$'; then
      IFS='.' read -r MAJOR MINOR PATCH <<< "$BUMP"
    else
      echo "Usage: $0 [major|minor|patch|x.y.z]"
      exit 1
    fi
    ;;
esac

NEW_VERSION="${MAJOR}.${MINOR}.${PATCH}"
echo "New version: $NEW_VERSION"

echo "Updating gradle.properties..."
sed -i "s/^version = .*/version = $NEW_VERSION/" gradle.properties

echo "Building project..."
./gradlew :patches:buildAndroid generatePatchesList clean --no-daemon

MPP_FILE="patches/build/libs/patches-${NEW_VERSION}.mpp"
if [ ! -f "$MPP_FILE" ]; then
  MPP_FILE=$(ls patches/build/libs/patches-*.mpp 2>/dev/null | grep -v javadoc | grep -v sources | head -1)
fi
if [ -z "$MPP_FILE" ]; then
  echo "ERROR: No .mpp file found in patches/build/libs/"
  exit 1
fi
echo "Found .mpp: $MPP_FILE"

TAG="v${NEW_VERSION}"
RELEASE_DATE=$(date -u +"%Y-%m-%dT%H:%M:%S")
RELEASE_NOTES="## [${NEW_VERSION}](https://github.com/${GITHUB_REPO}/compare/v${CURRENT_VERSION}...v${NEW_VERSION}) ($(date -u +%Y-%m-%d))\n\nPatches release ${NEW_VERSION}"

echo "Creating GitHub release $TAG..."
RELEASE_RESPONSE=$(curl -s -X POST "https://api.github.com/repos/${GITHUB_REPO}/releases" \
  -H "Authorization: token $GITHUB_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"tag_name\": \"${TAG}\",
    \"name\": \"${TAG}\",
    \"body\": \"${RELEASE_NOTES}\",
    \"draft\": false,
    \"prerelease\": false,
    \"target_commitish\": \"main\"
  }")

RELEASE_ID=$(echo "$RELEASE_RESPONSE" | node -e "const d=JSON.parse(require('fs').readFileSync('/dev/stdin','utf8')); process.stdout.write(String(d.id || ''));" 2>/dev/null)
if [ -z "$RELEASE_ID" ]; then
  echo "ERROR: Failed to create release"
  echo "$RELEASE_RESPONSE"
  exit 1
fi
echo "Release created: ID=$RELEASE_ID"

echo "Uploading $(basename $MPP_FILE)..."
UPLOAD_RESPONSE=$(curl -s -X POST \
  "https://uploads.github.com/repos/${GITHUB_REPO}/releases/${RELEASE_ID}/assets?name=patches-${NEW_VERSION}.mpp" \
  -H "Authorization: token $GITHUB_TOKEN" \
  -H "Content-Type: application/octet-stream" \
  --data-binary "@${MPP_FILE}")

DOWNLOAD_URL=$(echo "$UPLOAD_RESPONSE" | node -e "const d=JSON.parse(require('fs').readFileSync('/dev/stdin','utf8')); process.stdout.write(d.browser_download_url || '');" 2>/dev/null)
if [ -z "$DOWNLOAD_URL" ]; then
  echo "ERROR: Failed to upload .mpp file"
  echo "$UPLOAD_RESPONSE"
  exit 1
fi
echo "Uploaded: $DOWNLOAD_URL"

echo "Updating patches-bundle.json..."
node -e "
const fs = require('fs');
const bundle = {
  created_at: '${RELEASE_DATE}',
  description: 'Silva Patches release v${NEW_VERSION}',
  download_url: '${DOWNLOAD_URL}',
  signature_download_url: '',
  version: '${NEW_VERSION}'
};
fs.writeFileSync('patches-bundle.json', JSON.stringify(bundle, null, 2) + '\n');
console.log('patches-bundle.json updated');
"

sed -i "s/\"version\": \".*\"/\"version\": \"v${NEW_VERSION}\"/" patches-list.json

echo "Committing and pushing..."
git add gradle.properties patches-bundle.json patches-list.json CHANGELOG.md
git diff --cached --quiet && echo "Nothing to commit" || \
  git commit -m "chore: Release v${NEW_VERSION} [skip ci]"

git push "https://${GITHUB_ACTOR}:${GITHUB_TOKEN}@github.com/${GITHUB_REPO}.git" main

echo ""
echo "Release v${NEW_VERSION} complete!"
echo "Download: $DOWNLOAD_URL"

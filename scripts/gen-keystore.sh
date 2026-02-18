#!/usr/bin/env bash
# Generate a self-signed keystore for local HTTPS (run once, then start the app).
# Requires JAVA_HOME or 'keytool' on PATH.

set -e
KEYSTORE=src/main/resources/keystore.p12
PASS="${SSL_KEY_STORE_PASSWORD:-changeit}"

if command -v keytool >/dev/null 2>&1; then
  keytool -genkeypair -alias letsplay -keyalg RSA -keysize 2048 -storetype PKCS12 \
    -keystore "$KEYSTORE" -validity 365 -storepass "$PASS" -keypass "$PASS" \
    -dname "CN=localhost, OU=Dev, O=LetsPlay, L=Local, ST=Local, C=US"
  echo "Created $KEYSTORE (password: $PASS). Start the app with HTTPS on port 8443."
else
  echo "keytool not found. Set JAVA_HOME or add JDK bin to PATH, then run again."
  exit 1
fi

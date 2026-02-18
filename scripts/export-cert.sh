#!/usr/bin/env bash
# Export the server certificate from keystore.p12 so you can trust it in the OS/browser.
# After running: double-click the generated .cer file to add it to your keychain (macOS)
# or install in Trusted Root Certification Authorities (Windows).
# Requires keytool on PATH.

set -e
KEYSTORE=src/main/resources/keystore.p12
CERT=localhost.cer
PASS="${SSL_KEY_STORE_PASSWORD:-changeit}"

if [[ ! -f "$KEYSTORE" ]]; then
  echo "Keystore not found. Run ./scripts/gen-keystore.sh first."
  exit 1
fi

keytool -exportcert -alias letsplay -keystore "$KEYSTORE" -storetype PKCS12 \
  -storepass "$PASS" -rfc -file "$CERT"
echo "Exported certificate to $CERT"
echo "Double-click $CERT to add to keychain (macOS) or install as trusted root (Windows)."
